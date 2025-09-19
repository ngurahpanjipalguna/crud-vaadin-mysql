package com.example.crud.ui;

import com.example.crud.entity.Employee;
import com.example.crud.service.EmployeeService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.Route;

import java.util.Locale;

@Route("")
public class EmployeeView extends VerticalLayout {

    private final EmployeeService employeeService;
    private final Grid<Employee> grid = new Grid<>(Employee.class, false);
    private final TextField filterText = new TextField();
    private final EmployeeForm form;
    private final Dialog dialog = new Dialog();

    public EmployeeView(EmployeeService employeeService) {
        this.employeeService = employeeService;
        this.form = new EmployeeForm();

        // Menambahkan listener untuk event dari form
        form.addListener(EmployeeForm.SaveEvent.class, this::saveEmployee);
        form.addListener(EmployeeForm.DeleteEvent.class, this::deleteEmployee);
        form.addListener(EmployeeForm.CloseEvent.class, e -> closeEditor());

        addClassName("employee-view");
        setSizeFull();
        configureGrid();
        configureDialog();

        add(getToolbar(), grid);

        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassName("employee-grid");
        grid.setSizeFull();
        grid.addColumn(Employee::getId).setHeader("ID");
        grid.addColumn(Employee::getName).setHeader(getTranslation("employee.name"));
        grid.addColumn(Employee::getEmail).setHeader(getTranslation("employee.email"));
        grid.addColumn(Employee::getPosition).setHeader(getTranslation("employee.position"));
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> editEmployee(event.getValue()));
    }

    private void configureDialog() {
        dialog.add(form);
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder(getTranslation("filter.by.name"));
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addEmployeeButton = new Button(getTranslation("employee.add"));
        addEmployeeButton.addClickListener(click -> addEmployee());

        // Language Selector
        Select<Locale> languageSelect = new Select<>();
        languageSelect.setItems(new Locale("en", "US"), new Locale("id", "ID"));
        languageSelect.setValue(UI.getCurrent().getLocale());
        languageSelect.setItemLabelGenerator(locale -> getTranslation("language." + locale.getLanguage()));
        languageSelect.addValueChangeListener(event -> {
            UI.getCurrent().getSession().setLocale(event.getValue());
            UI.getCurrent().getPage().reload();
        });

        // Dark Mode Toggle Button with Icon
        Button darkModeButton = new Button();
        
        // Atur ikon berdasarkan tema saat ini
        updateDarkModeIcon(darkModeButton);

        darkModeButton.addClickListener(click -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();
            if (themeList.contains("dark")) {
                themeList.remove("dark");
            } else {
                themeList.add("dark");
            }
            updateDarkModeIcon(darkModeButton); // Perbarui ikon setelah diklik
        });


        HorizontalLayout toolbar = new HorizontalLayout(filterText, addEmployeeButton);
        toolbar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        HorizontalLayout rightToolbar = new HorizontalLayout(languageSelect, darkModeButton);
        rightToolbar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        rightToolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        rightToolbar.setWidthFull();

        HorizontalLayout mainToolbar = new HorizontalLayout(toolbar, rightToolbar);
        mainToolbar.setWidthFull();
        mainToolbar.expand(toolbar);
        mainToolbar.addClassName("toolbar");

        return mainToolbar;
    }
    
    private void updateDarkModeIcon(Button button) {
        ThemeList themeList = UI.getCurrent().getElement().getThemeList();
        if (themeList.contains("dark")) {
            // Jika mode gelap, tampilkan ikon BULAN
            button.setIcon(VaadinIcon.MOON_O.create());
        } else {
            // Jika mode terang, tampilkan ikon MATAHARI
            button.setIcon(VaadinIcon.SUN_O.create());
        }
    }

    public void updateList() {
        grid.setItems(employeeService.findAll(filterText.getValue()));
    }

    private void addEmployee() {
        grid.asSingleSelect().clear();
        editEmployee(new Employee());
    }

    private void editEmployee(Employee employee) {
        if (employee == null) {
            closeEditor();
        } else {
            form.setEmployee(employee);
            dialog.open();
            addClassName("editing");
        }
    }

    private void saveEmployee(EmployeeForm.SaveEvent event) {
        employeeService.save(event.getEmployee());
        updateList();
        closeEditor();
    }

    private void deleteEmployee(EmployeeForm.DeleteEvent event) {
        employeeService.delete(event.getEmployee());
        updateList();
        closeEditor();
    }

    private void closeEditor() {
        dialog.close();
        form.setEmployee(null);
        removeClassName("editing");
    }
}