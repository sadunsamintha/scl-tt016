package com.sicpa.standard.gui.components.format;


import javax.swing.*;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.Locale;
import java.util.LongSummaryStatistics;

public class LocaleFormatter extends JFormattedTextField.AbstractFormatter{

    private final NumberFormat formatter;

    private final Class type;

    public LocaleFormatter(Locale locale,Class type){
        formatter = NumberFormat.getNumberInstance(locale);
        this.type = type;
    }

    @Override
    public Object stringToValue(String text) throws ParseException {
        Object o = formatter.parse(text);
        if (type.equals(Integer.class)) {
            return Integer.valueOf(o.toString());
        }
        if (type.equals(Long.class)){
            return Long.valueOf(o.toString());
        }
        if (type.equals(Float.class)){
            return Float.valueOf(o.toString());
        }
        if (type.equals(Double.class)){
            return Double.valueOf(o.toString());
        }
        //if type is unsupported return anyway... and hope
        return o;
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        return formatter.format(value);
    }
}
