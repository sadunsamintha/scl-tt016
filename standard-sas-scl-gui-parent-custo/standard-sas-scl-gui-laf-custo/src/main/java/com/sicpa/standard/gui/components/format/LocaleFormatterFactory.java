package com.sicpa.standard.gui.components.format;

import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.text.DefaultFormatterFactory;
import java.util.Locale;


public class LocaleFormatterFactory extends AbstractFormatterFactory {

    private final Locale locale;

    private final Class type;

    public LocaleFormatterFactory(Locale locale,Class type){
        this.locale = locale;
        this.type = type;
    }
    @Override
    public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
        return new DefaultFormatterFactory(new LocaleFormatter(locale,type)).getDefaultFormatter();
    }
}
