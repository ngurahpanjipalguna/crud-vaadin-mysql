package com.example.crud.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import com.example.crud.entity.Employee;
import com.example.crud.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;
import java.util.ResourceBundle;

@Route("")
public class EmployeeView extends VerticalLayout {
    
    private final EmployeeService service;
    private final Grid<Employee> grid = new Grid<>(Employee.class, false);
    private Locale currentLocale = Locale.ENGLISH;
    private ResourceBundle bundle = ResourceBundle.getBundle("messages", currentLocale);
    
    private Button addButton;
    private ComboBox<String> languageSelect;
    private H2 title; // Keep reference to title for easy updates
    
    @Autowired
    public EmployeeView(EmployeeService service) {
        this.service = service;
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        
        initializeToolbar();
        initializeGrid();
        
        refreshGrid();
    }

    private void initializeGrid() {
        grid.setSizeFull();
        grid.addColumn(employee -> grid.getListDataView().getItems().toList().indexOf(employee) + 1)
                .setHeader(bundle.getString("grid.no")).setWidth("80px").setFlexGrow(0).setSortable(false);
        grid.addColumn(Employee::getName).setHeader(bundle.getString("grid.name"));
        grid.addColumn(Employee::getEmail).setHeader(bundle.getString("grid.email"));
        grid.addColumn(Employee::getPosition).setHeader(bundle.getString("grid.position"));
        
        // Add action column with edit and delete buttons
        grid.addComponentColumn(employee -> {
            Button editButton = new Button(new Icon(VaadinIcon.EDIT));
            editButton.addClickListener(e -> openForm(employee));
            editButton.setTooltipText(bundle.getString("button.edit"));
            
            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addClickListener(e -> deleteEmployee(employee));
            deleteButton.setTooltipText(bundle.getString("button.delete"));
            
            return new HorizontalLayout(editButton, deleteButton);
        }).setHeader(bundle.getString("grid.action")).setWidth("150px").setFlexGrow(0);
        
        add(grid);
    }

    private void initializeToolbar() {
        title = new H2(bundle.getString("title.employees")); // Initialize title field
        
        addButton = new Button(new Icon(VaadinIcon.PLUS));
        addButton.setText(bundle.getString("button.add"));
        addButton.addClickListener(e -> openForm(new Employee()));
        
        languageSelect = new ComboBox<>();
        languageSelect.setItems("English", "Indonesian");
        languageSelect.setValue("English");
        languageSelect.setPlaceholder(bundle.getString("select.language"));
        languageSelect.addValueChangeListener(event -> {
            if ("English".equals(event.getValue())) {
                switchLanguage(Locale.ENGLISH);
            } else if ("Indonesian".equals(event.getValue())) {
                switchLanguage(new Locale("id"));
            }
        });
        
        HorizontalLayout toolbar = new HorizontalLayout(languageSelect,title, addButton);
        toolbar.setAlignItems(Alignment.CENTER);
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        toolbar.setWidthFull();
        
        add(toolbar);
    }

    private void switchLanguage(Locale locale) {
        currentLocale = locale;
        bundle = ResourceBundle.getBundle("messages", currentLocale);
        updateTexts();
    }

    private void updateTexts() {
        // Update toolbar using the title reference
        title.setText(bundle.getString("title.employees"));
        addButton.setText(bundle.getString("button.add"));
        languageSelect.setPlaceholder(bundle.getString("select.language"));
        
        // Update grid headers
        grid.getColumns().get(0).setHeader(bundle.getString("grid.no"));
        grid.getColumns().get(1).setHeader(bundle.getString("grid.name"));
        grid.getColumns().get(2).setHeader(bundle.getString("grid.email"));
        grid.getColumns().get(3).setHeader(bundle.getString("grid.position"));
        grid.getColumns().get(4).setHeader(bundle.getString("grid.action"));
        
        // Refresh the grid to update action button tooltips
        refreshGrid();
    }

    private void refreshGrid() {
        grid.setItems(service.findAll());
        
        // Update action button tooltips in the grid
        grid.getListDataView().getItems().forEach(employee -> {
            // This is a bit tricky as we need to access the component column
            // For now, we'll just refresh the data which will recreate the components
        });
    }

    private void openForm(Employee employee) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");
        
        H2 formTitle = new H2(employee.getId() == null ? 
                bundle.getString("form.title.add") : bundle.getString("form.title.edit"));
        
        TextField nameField = new TextField(bundle.getString("form.name"));
        nameField.setRequired(true);
        nameField.setRequiredIndicatorVisible(true);
        nameField.setWidthFull();
        
        EmailField emailField = new EmailField(bundle.getString("form.email"));
        emailField.setRequired(true);
        emailField.setRequiredIndicatorVisible(true);
        emailField.setWidthFull();
        
        TextField positionField = new TextField(bundle.getString("form.position"));
        positionField.setRequired(true);
        positionField.setRequiredIndicatorVisible(true);
        positionField.setWidthFull();
        
        // Create binder with validation
        Binder<Employee> binder = new BeanValidationBinder<>(Employee.class);
        binder.forField(nameField).asRequired(bundle.getString("validation.required"))
                .bind(Employee::getName, Employee::setName);
        binder.forField(emailField).asRequired(bundle.getString("validation.required"))
                .withValidator(this::isValidEmail, bundle.getString("validation.email"))
                .bind(Employee::getEmail, Employee::setEmail);
        binder.forField(positionField).asRequired(bundle.getString("validation.required"))
                .bind(Employee::getPosition, Employee::setPosition);
        
        binder.readBean(employee);
        
        Button saveButton = new Button(bundle.getString("button.save"), e -> {
            try {
                binder.writeBean(employee);
                service.save(employee);
                dialog.close();
                refreshGrid();
                Notification.show(bundle.getString(employee.getId() == null ? 
                        "notification.added" : "notification.updated"), 3000, Notification.Position.MIDDLE);
            } catch (ValidationException ex) {
                Notification.show(bundle.getString("validation.fix.errors"), 3000, Notification.Position.MIDDLE);
            }
        });
        
        Button cancelButton = new Button(bundle.getString("button.cancel"), e -> dialog.close());
        
        FormLayout formLayout = new FormLayout();
        formLayout.add(nameField, emailField, positionField);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        
        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);
        buttons.setSpacing(true);
        
        VerticalLayout dialogLayout = new VerticalLayout(formTitle, formLayout, buttons);
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(false);
        
        dialog.add(dialogLayout);
        dialog.open();
    }
    
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private void deleteEmployee(Employee employee) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setWidth("300px");
        
        H2 confirmTitle = new H2(bundle.getString("dialog.delete.title"));
        String message = String.format(bundle.getString("dialog.delete.message"), employee.getName());
        
        Button confirmButton = new Button(bundle.getString("button.delete"), e -> {
            service.delete(employee);
            confirmDialog.close();
            refreshGrid();
            Notification.show(bundle.getString("notification.deleted"), 3000, Notification.Position.MIDDLE);
        });
        
        Button cancelButton = new Button(bundle.getString("button.cancel"), e -> confirmDialog.close());
        
        HorizontalLayout buttons = new HorizontalLayout(confirmButton, cancelButton);
        buttons.setSpacing(true);
        
        VerticalLayout dialogLayout = new VerticalLayout(confirmTitle, new Span(message), buttons);
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(false);
        
        confirmDialog.add(dialogLayout);
        confirmDialog.open();
    }
}