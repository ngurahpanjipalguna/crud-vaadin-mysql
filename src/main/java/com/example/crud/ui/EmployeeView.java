package com.example.crud.ui;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.example.crud.entity.Employee;
import com.example.crud.service.EmployeeService;
import com.vaadin.flow.component.button.Button;
import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

@Route("")
public class EmployeeView extends VerticalLayout {
    
    private final EmployeeService service;
    private final Grid<Employee> grid = new Grid<>(Employee.class, false);
    private Locale currentLocal = Locale.ENGLISH;
    private ResourceBundle bundle = ResourceBundle.getBundle("messages", currentLocal);
    
    private Button addButton;
    private Button editButton;
    private Button deleteButton;

    @Autowired
    public EmployeeView(EmployeeService service) {
        this.service = service;
        setSizeFull();

        grid.addColumn(Employee -> grid.getListDataView().getItems().toList().indexOf(Employee) + 1).setHeader("No");
        grid.addColumn(Employee::getName).setHeader(bundle.getString("grid.name"));
        grid.addColumn(Employee::getEmail).setHeader(bundle.getString("grid.email"));
        grid.addColumn(Employee::getPosition).setHeader(bundle.getString("grid.position"));
        grid.addColumn(Employee::getPosition).setHeader(bundle.getString("grid.action"));

        addButton = new Button(bundle.getString("button.add"), e -> openForm(new Employee()));
        editButton = new Button(bundle.getString("button.edit"), e -> {
            Employee selected = grid.asSingleSelect().getValue();
            if (selected != null) openForm(selected);
        });
        deleteButton = new Button(bundle.getString("button.delete"), e -> {
            Employee selected = grid.asSingleSelect().getValue();
            if (selected != null) {
                service.delete(selected);
                refreshGrid();
            }
        });
        
        ComboBox<String> languageSelect = new ComboBox<>(bundle.getString("select.language"));
        languageSelect.setItems("English", "Indonesian");

        languageSelect.addValueChangeListener(event -> {
            if ("English".equals(event.getValue())) {
                switchLanguage(Locale.ENGLISH);
            } else if ("Indonesian".equals(event.getValue())) {
                switchLanguage(new Locale("id"));
            }
        });

        HorizontalLayout actions = new HorizontalLayout(addButton, editButton, deleteButton, languageSelect);
        add(actions, grid);
        refreshGrid();
    }

    private void switchLanguage(Locale locale) {
        currentLocal = locale;
        bundle = ResourceBundle.getBundle("messages", currentLocal);
        updateTexts();
    }

    private void updateTexts() {
        // Update grid headers
        grid.getColumns().get(0).setHeader(bundle.getString("grid.no"));
        grid.getColumns().get(1).setHeader(bundle.getString("grid.name"));
        grid.getColumns().get(2).setHeader(bundle.getString("grid.email"));
        grid.getColumns().get(3).setHeader(bundle.getString("grid.position"));
        grid.getColumns().get(4).setHeader(bundle.getString("grid.action"));
        
        // Update button labels
        addButton.setText(bundle.getString("button.add"));
        editButton.setText(bundle.getString("button.edit"));
        deleteButton.setText(bundle.getString("button.delete"));
    }

    private void refreshGrid() {
        grid.setItems(service.findAll());
    }

    private void openForm(Employee employee) {
        Dialog dialog = new Dialog();
        FormLayout form = new FormLayout();

        TextField name = new TextField(bundle.getString("form.name"));
        name.setValue(employee.getName() != null ? employee.getName() : "");

        TextField email = new TextField(bundle.getString("form.email"));
        email.setValue(employee.getEmail() != null ? employee.getEmail() : "");

        TextField position = new TextField(bundle.getString("form.position"));
        position.setValue(employee.getPosition() != null ? employee.getPosition() : "");

        Button save = new Button(bundle.getString("button.save"), e -> {
            employee.setName(name.getValue());
            employee.setEmail(email.getValue());
            employee.setPosition(position.getValue());
            service.save(employee);
            dialog.close();
            refreshGrid();
        });

        form.add(name, email, position, save);
        dialog.add(form);
        dialog.open(); 
    }
}
