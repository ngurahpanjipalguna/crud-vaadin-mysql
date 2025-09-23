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
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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
    private H2 title;

    @Autowired
    public EmployeeView(EmployeeService service) {
        this.service = service;
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        initializeToolbar();
        initializeGrid();
        refreshGrid();
    }

    private void initializeToolbar() {
        title = new H2(bundle.getString("title.employees"));
        title.getStyle().set("color", "#d32f2f");

        addButton = new Button(bundle.getString("button.add"), new Icon(VaadinIcon.PLUS));
        styleRedButton(addButton);
        addButton.addClickListener(e -> openForm(new Employee())); // tambahkan listener


        languageSelect = new ComboBox<>();
        languageSelect.setItems("English", "Indonesia");
        languageSelect.setValue(currentLocale.equals(Locale.ENGLISH) ? "English" : "Indonesia");
        languageSelect.setPlaceholder(bundle.getString("select.language"));
        languageSelect.addValueChangeListener(event -> {
            if ("English".equals(event.getValue())) {
                switchLanguage(Locale.ENGLISH);
            } else if ("Indonesia".equals(event.getValue())) {
                switchLanguage(new Locale("id"));
            }
        });

        HorizontalLayout toolbar = new HorizontalLayout(languageSelect, title, addButton);
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        toolbar.setWidthFull();
        toolbar.setPadding(true);
        toolbar.getStyle().set("background-color", "white");
        toolbar.getStyle().set("border-bottom", "2px solid #d32f2f");

        Span aksara = new Span("ᬳᬶᬤᬸᬧ᭄ ᭞ ᬚᭀᬓᭀᬯᬶ");

        aksara.addClassName("balinese-text");


        add(toolbar,aksara);
    }

    private void initializeGrid() {
        grid.setSizeFull();

        // Number column
        grid.addColumn(employee -> grid.getListDataView().getItems().toList().indexOf(employee) + 1)
                .setHeader(bundle.getString("grid.no"))
                .setWidth("80px")
                .setFlexGrow(0)
                .setSortable(false)
                .setResizable(true);

        // Name column
        grid.addColumn(Employee::getName)
                .setHeader(bundle.getString("grid.name"))
                .setResizable(true)
                .setSortable(true)
                .setAutoWidth(true);

        // Email column
        grid.addColumn(Employee::getEmail)
                .setHeader(bundle.getString("grid.email"))
                .setResizable(true)
                .setSortable(true)
                .setAutoWidth(true);

        // Position column
        grid.addColumn(Employee::getPosition)
                .setHeader(bundle.getString("grid.position"))
                .setResizable(true)
                .setSortable(true)
                .setAutoWidth(true);

        // Action column
        grid.addComponentColumn(employee -> {
            Button editButton = new Button(new Icon(VaadinIcon.EDIT));
            editButton.addClickListener(e -> openForm(employee));
            editButton.getElement().setAttribute("title", bundle.getString("button.edit"));
            styleWhiteRedBorderButton(editButton);

            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addClickListener(e -> deleteEmployee(employee));
            deleteButton.getElement().setAttribute("title", bundle.getString("button.delete"));
            styleRedButton(deleteButton);

            HorizontalLayout actions = new HorizontalLayout(editButton, deleteButton);
            actions.setSpacing(true);
            actions.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            return actions;
        }).setHeader(bundle.getString("grid.action"))
          .setWidth("150px")
          .setFlexGrow(0)
          .setResizable(false);

        // Header merah
        grid.getElement().executeJs(
                "this.querySelectorAll('thead th').forEach(th => {" +
                        "th.style.backgroundColor = '#d32f2f';" +
                        "th.style.color = 'white';" +
                        "th.style.fontWeight = 'bold';" +
                        "});"
        );

        // Baris terpilih merah muda
        grid.getElement().executeJs(
                "this.addEventListener('active-item-changed', e => {" +
                "  this.querySelectorAll('tr[selected]').forEach(tr => {" +
                "    tr.style.backgroundColor = '#ffcdd2';" +
                "  });" +
                "});"
        );

        add(grid);
        setFlexGrow(1, grid);
    }

    private void switchLanguage(Locale locale) {
        currentLocale = locale;
        bundle = ResourceBundle.getBundle("messages", currentLocale);
        updateTexts();
    }

    private void updateTexts() {
        title.setText(bundle.getString("title.employees"));
        addButton.setText(bundle.getString("button.add"));
        languageSelect.setPlaceholder(bundle.getString("select.language"));

        grid.getColumns().get(0).setHeader(bundle.getString("grid.no"));
        grid.getColumns().get(1).setHeader(bundle.getString("grid.name"));
        grid.getColumns().get(2).setHeader(bundle.getString("grid.email"));
        grid.getColumns().get(3).setHeader(bundle.getString("grid.position"));
        grid.getColumns().get(4).setHeader(bundle.getString("grid.action"));

        refreshGrid();
    }

    private void refreshGrid() {
        grid.setItems(service.findAll());
    }

    private void openForm(Employee employee) {
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");
        dialog.setMaxWidth("90vw");

        H2 formTitle = new H2(employee.getId() == null ? bundle.getString("form.title.add") : bundle.getString("form.title.edit"));
        formTitle.getStyle().set("color", "#d32f2f");

        TextField nameField = new TextField(bundle.getString("form.name"));
        nameField.setRequired(true);
        nameField.setWidthFull();
        nameField.setPrefixComponent(new Icon(VaadinIcon.USER));

        EmailField emailField = new EmailField(bundle.getString("form.email"));
        emailField.setRequired(true);
        emailField.setWidthFull();
        emailField.setPrefixComponent(new Icon(VaadinIcon.ENVELOPE));

        TextField positionField = new TextField(bundle.getString("form.position"));
        positionField.setRequired(true);
        positionField.setWidthFull();
        positionField.setPrefixComponent(new Icon(VaadinIcon.BRIEFCASE));

        Binder<Employee> binder = new BeanValidationBinder<>(Employee.class);
        binder.forField(nameField).asRequired(bundle.getString("validation.required")).bind(Employee::getName, Employee::setName);
        binder.forField(emailField).asRequired(bundle.getString("validation.required")).withValidator(this::isValidEmail, bundle.getString("validation.email")).bind(Employee::getEmail, Employee::setEmail);
        binder.forField(positionField).asRequired(bundle.getString("validation.required")).bind(Employee::getPosition, Employee::setPosition);

        binder.readBean(employee);

        Button saveButton = new Button(bundle.getString("button.save"), new Icon(VaadinIcon.CHECK));
        styleRedButton(saveButton);
        saveButton.addClickListener(e -> {
            try {
                binder.writeBean(employee);
                service.save(employee);
                dialog.close();
                refreshGrid();
                Notification.show(bundle.getString(employee.getId() == null ? "notification.added" : "notification.updated"), 3000, Notification.Position.MIDDLE);
            } catch (ValidationException ex) {
                Notification.show(bundle.getString("validation.fix.errors"), 3000, Notification.Position.MIDDLE);
            }
        });

        Button cancelButton = new Button(bundle.getString("button.cancel"), new Icon(VaadinIcon.CLOSE));
        styleWhiteRedBorderButton(cancelButton);
        cancelButton.addClickListener(e -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);
        buttons.setSpacing(true);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setWidthFull();

        FormLayout formLayout = new FormLayout(nameField, emailField, positionField);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );
        formLayout.setColspan(nameField, 2);
        formLayout.setColspan(emailField, 2);
        formLayout.setColspan(positionField, 2);

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
        confirmDialog.setWidth("400px");
        confirmDialog.setMaxWidth("90vw");

        H2 confirmTitle = new H2(bundle.getString("dialog.delete.title"));
        confirmTitle.getStyle().set("color", "#d32f2f");

        Span messageSpan = new Span(String.format(bundle.getString("dialog.delete.message"), employee.getName()));

        Button confirmButton = new Button(bundle.getString("button.delete"), new Icon(VaadinIcon.TRASH));
        styleRedButton(confirmButton);
        confirmButton.addClickListener(e -> {
            service.delete(employee);
            confirmDialog.close();
            refreshGrid();
            Notification.show(bundle.getString("notification.deleted"), 3000, Notification.Position.MIDDLE);
        });

        Button cancelButton = new Button(bundle.getString("button.cancel"), new Icon(VaadinIcon.CLOSE));
        styleWhiteRedBorderButton(cancelButton);
        cancelButton.addClickListener(e -> confirmDialog.close());

        HorizontalLayout buttons = new HorizontalLayout(confirmButton, cancelButton);
        buttons.setSpacing(true);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setWidthFull();

        VerticalLayout dialogLayout = new VerticalLayout(confirmTitle, messageSpan, buttons);
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(false);

        confirmDialog.add(dialogLayout);
        confirmDialog.open();
    }

    // ====== HELPER METHODS ======
    private void styleRedButton(Button button) {
        button.getStyle().set("background-color", "#d32f2f");
        button.getStyle().set("color", "white");
        button.getStyle().set("border", "none");
        button.getStyle().set("cursor", "pointer");
        button.getElement().executeJs(
                "this.addEventListener('mouseover', function(){ this.style.backgroundColor='#b71c1c'; });" +
                "this.addEventListener('mouseout', function(){ this.style.backgroundColor='#d32f2f'; });"
        );
    }

    private void styleWhiteRedBorderButton(Button button) {
        button.getStyle().set("background-color", "white");
        button.getStyle().set("color", "#d32f2f");
        button.getStyle().set("border", "2px solid #d32f2f");
        button.getStyle().set("cursor", "pointer");
        button.getElement().executeJs(
                "this.addEventListener('mouseover', function(){ this.style.backgroundColor='#ffcdd2'; });" +
                "this.addEventListener('mouseout', function(){ this.style.backgroundColor='white'; });"
        );
    }
}
