package com.example.crud.ui;

import com.example.crud.entity.Employee;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

public class EmployeeForm extends FormLayout {

    private Employee employee;

    TextField name = new TextField("Name");
    EmailField email = new EmailField("Email");
    TextField position = new TextField("Position");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<Employee> binder = new BeanValidationBinder<>(Employee.class);

    public EmployeeForm() {
        addClassName("employee-form");
        binder.bindInstanceFields(this);

        add(name, email, position, createButtonsLayout());
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        // Events
        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, employee)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, delete, close);
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        binder.readBean(employee);
    }

    private void validateAndSave() {
        try {
            if (employee != null) {
                binder.writeBean(employee);
                fireEvent(new SaveEvent(this, employee));
            }
        } catch (ValidationException e) {
            // Error validasi akan ditampilkan di field secara otomatis
        }
    }


    // Event Classes
    public static abstract class EmployeeFormEvent extends ComponentEvent<EmployeeForm> {
        private final Employee employee;

        protected EmployeeFormEvent(EmployeeForm source, Employee employee) {
            super(source, false);
            this.employee = employee;
        }

        public Employee getEmployee() {
            return employee;
        }
    }

    public static class SaveEvent extends EmployeeFormEvent {
        SaveEvent(EmployeeForm source, Employee employee) {
            super(source, employee);
        }
    }

    public static class DeleteEvent extends EmployeeFormEvent {
        DeleteEvent(EmployeeForm source, Employee employee) {
            super(source, employee);
        }
    }

    public static class CloseEvent extends EmployeeFormEvent {
        CloseEvent(EmployeeForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}