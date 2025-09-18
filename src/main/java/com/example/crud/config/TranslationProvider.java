package com.example.crud.config;

import com.vaadin.flow.i18n.I18NProvider;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class TranslationProvider implements I18NProvider {

    private static final String BUNDLE_PREFIX = "vaadin-i18n/translations";

    private final List<Locale> providedLocales = Collections.unmodifiableList(Arrays.asList(
            new Locale("en", "US"),
            new Locale("id", "ID")
    ));

    @Override
    public List<Locale> getProvidedLocales() {
        return providedLocales;
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (key == null) {
            return "";
        }
        final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_PREFIX, locale);
        String value;
        try {
            value = bundle.getString(key);
        } catch (final Exception e) {
            // Translation not found
            return "!" + locale.getLanguage() + ": " + key;
        }
        if (params.length > 0) {
            value = MessageFormat.format(value, params);
        }
        return value;
    }
}