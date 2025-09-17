package com.example.crud.ui;

import com.example.crud.entity.Employee;
import com.example.crud.service.EmployeeService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
public class EmployeeView extends VerticalLayout {

    private final EmployeeService service;
    private final Grid<Employee> grid = new Grid<>(Employee.class, false);

    @Autowired
    public EmployeeView(EmployeeService service) {
        this.service = service;
        setSizeFull();

        grid.addColumn(Employee::getId).setHeader("ID");
        grid.addColumn(Employee::getName).setHeader("Name");
        grid.addColumn(Employee::getEmail).setHeader("Email");
        grid.addColumn(Employee::getPosition).setHeader("Position");

        Button addButton = new Button("Add Employee", e -> openForm(new Employee()));
        Button editButton = new Button("Edit Employee", e -> {
            Employee selected = grid.asSingleSelect().getValue();
            if (selected != null) openForm(selected);
        });
        Button deleteButton = new Button("Delete Employee", e -> {
            Employee selected = grid.asSingleSelect().getValue();
            if (selected != null) {
                service.delete(selected);
                refreshGrid();
            }
        });

        HorizontalLayout actions = new HorizontalLayout(addButton, editButton, deleteButton);
        add(actions, grid);
        refreshGrid();
    }

    private void refreshGrid() {
        grid.setItems(service.findAll());
    }

    private void openForm(Employee employee) {
        Dialog dialog = new Dialog();
        FormLayout form = new FormLayout();

        TextField name = new TextField("Name");
        name.setValue(employee.getName() != null ? employee.getName() : "");

        TextField email = new TextField("Email");
        email.setValue(employee.getEmail() != null ? employee.getEmail() : "");

        TextField position = new TextField("Position");
        position.setValue(employee.getPosition() != null ? employee.getPosition() : "");

        Button save = new Button("Save", e -> {
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
