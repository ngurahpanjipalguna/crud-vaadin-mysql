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
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.Route;


import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;


@Route("")
public class EmployeeView extends VerticalLayout {

    private final EmployeeService service;
    private final Grid<Employee> grid = new Grid<>(Employee.class, false);
    private Locale currentLocal = Locale.ENGLISH;
    private ResourceBundle bundle = ResourceBundle.getBundle("messages", currentLocal);
    

   
    @Autowired
    public EmployeeView(EmployeeService service) {
        this.service = service;
        setSizeFull();

        grid.addColumn(Employee -> grid.getListDataView().getItems().toList().indexOf(Employee)+1).setHeader("No");
        grid.addColumn(Employee::getName).setHeader(bundle.getString("grid.no"));
        grid.addColumn(Employee::getEmail).setHeader(bundle.getString("grid.email"));
        grid.addColumn(Employee::getPosition).setHeader(bundle.getString("grid.position"));
        grid.addColumn(Employee::getPosition).setHeader(bundle.getString("grid.action"));

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
        
        Button switchLang = new Button();
            switchLang.addClickListener(e -> {
                currentLocal = currentLocal.equals(Locale.ENGLISH) ? new Locale("id") : Locale.ENGLISH;
                bundle = ResourceBundle.getBundle("messages", currentLocal);
    
                // HorizontalLayout actions = new HorizontalLayout(addButton, editButton, deleteButton, switchLang);
                updateTexts(addButton, editButton, deleteButton, switchLang);
            });
        
        updateTexts(addButton, editButton, deleteButton, switchLang);
        HorizontalLayout actions = new HorizontalLayout(addButton, editButton, deleteButton, switchLang);
        add(actions, grid);
        refreshGrid();
    }

    private void updateTexts(Button addButton, Button editButton, Button deleteButton, Button switchLang) {
        // pastikan jumlah kolom sesuai index
        grid.getColumns().get(0).setHeader(bundle.getString("grid.no"));
        grid.getColumns().get(1).setHeader(bundle.getString("grid.name"));
        grid.getColumns().get(2).setHeader(bundle.getString("grid.email"));
        grid.getColumns().get(3).setHeader(bundle.getString("grid.position"));
        grid.getColumns().get(4).setHeader(bundle.getString("grid.action"));

        addButton.setText(bundle.getString("button.add"));
        editButton.setText(bundle.getString("button.edit"));
        deleteButton.setText(bundle.getString("button.delete"));
        switchLang.setText(bundle.getString("button.switch"));
    }

    private void refreshGrid() {
        grid.setItems(service.findAll());
    }

    private void openForm(Employee employee) {
        Dialog dialog = new Dialog();
        FormLayout form = new FormLayout();
        Binder<Employee> binder = new Binder<>(Employee.class); 

            TextField name = new TextField("Name");
            binder.forField(name)
                .asRequired("Name is required")
                .bind(Employee::getName, Employee::setName);

            TextField email = new TextField("Email");
            binder.forField(email)
                .asRequired("Email is required")
                .withValidator(new EmailValidator("Invailed email format"))
                .bind(Employee::getEmail, Employee::setEmail);
            
            TextField position = new TextField("Position");
            binder.forField(position)
                .asRequired("Position is required")
                .bind(Employee::getPosition, Employee::setPosition);
            
            Button save = new Button("Save", e -> {
                if (binder.writeBeanIfValid(employee)) {
                    service.save(employee);
                    dialog.close();
                    refreshGrid();
                } else {
                    // Gagal validasi, pesan error otomatis muncul di field
                    System.out.println("Validation gagal!");
                }
            });

        form.add(name, email, position, save);
        dialog.add(form);
        dialog.open();
    }
}
