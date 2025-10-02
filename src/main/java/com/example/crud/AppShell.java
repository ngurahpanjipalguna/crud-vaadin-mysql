package com.example.crud;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.PWA;

@PWA(
    name = "CRUD Vaadin",
    shortName = "CRUD",
    iconPath = "images/Logo_Nusantara.png" // ini untuk PWA installable app
)
public class AppShell implements AppShellConfigurator {

    @Override
    public void configurePage(AppShellSettings settings) {
        // favicon utama
        settings.addFavIcon("icon", "images/Logo_Nusantara.png", "32x32");

        // bisa juga tambahan ukuran lain (opsional)
    }
}
