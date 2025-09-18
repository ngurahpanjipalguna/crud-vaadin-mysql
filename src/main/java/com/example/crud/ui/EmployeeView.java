package com.example.crud.ui;

import com.example.crud.entity.Employee;
import com.example.crud.service.EmployeeService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

@Route("")
public class EmployeeView extends VerticalLayout {

    private final EmployeeService service;
    private final Grid<Employee> grid = new Grid<>(Employee.class, false);

    @Autowired
    public EmployeeView(EmployeeService service) {
        this.service = service;
        setSizeFull();

        // Language Selector
        Select<Locale> languageSelect = new Select<>();
        languageSelect.setLabel("Language");
        languageSelect.setItems(new Locale("en", "US"), new Locale("id", "ID"));
        languageSelect.setValue(UI.getCurrent().getLocale());
        languageSelect.setItemLabelGenerator(locale -> getTranslation("language." + locale.getLanguage()));

        languageSelect.addValueChangeListener(event -> {
            UI.getCurrent().getSession().setLocale(event.getValue());
            UI.getCurrent().getPage().reload(); 
        });

        grid.addColumn(Employee::getId).setHeader("ID");
        grid.addColumn(Employee::getName).setHeader(getTranslation("employee.name"));
        grid.addColumn(Employee::getEmail).setHeader(getTranslation("employee.email"));
        grid.addColumn(Employee::getPosition).setHeader(getTranslation("employee.position"));

        Button addButton = new Button(getTranslation("employee.add"), e -> openForm(new Employee()));
        Button editButton = new Button(getTranslation("employee.edit"), e -> {
            Employee selected = grid.asSingleSelect().getValue();
            if (selected != null) openForm(selected);
        });
        Button deleteButton = new Button(getTranslation("employee.delete"), e -> {
            Employee selected = grid.asSingleSelect().getValue();
            if (selected != null) {
                service.delete(selected);
                refreshGrid();
            }
        });

        HorizontalLayout actions = new HorizontalLayout(addButton, editButton, deleteButton);
        add(languageSelect, actions, grid);
        refreshGrid();
    }

    private void refreshGrid() {
        grid.setItems(service.findAll());
    }

    private void openForm(Employee employee) {
        Dialog dialog = new Dialog();
        FormLayout form = new FormLayout();

        Binder<Employee> binder = new BeanValidationBinder<>(Employee.class);

        TextField name = new TextField(getTranslation("employee.name"));
        binder.forField(name)
                .asRequired(getTranslation("employee.name.required"))
                .bind(Employee::getName, Employee::setName);

        TextField email = new TextField(getTranslation("employee.email"));
        binder.forField(email)
                .asRequired(getTranslation("employee.email.required"))
                .withValidator(emailValue -> emailValue.contains("@"), getTranslation("employee.email.invalid"))
                .bind(Employee::getEmail, Employee::setEmail);

        TextField position = new TextField(getTranslation("employee.position"));
        binder.forField(position)
                .asRequired(getTranslation("employee.position.required"))
                .bind(Employee::getPosition, Employee::setPosition);

        binder.setBean(employee);

        Button save = new Button(getTranslation("employee.save"), e -> {
            if (binder.validate().isOk()) {
                service.save(employee);
                dialog.close();
                refreshGrid();
            }
        });

        form.add(name, email, position, save);
        dialog.add(form);
        dialog.open();
    }
}