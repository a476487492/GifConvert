package com.getting.util.binding;

import com.sun.javafx.binding.StringFormatter;
import javafx.beans.property.ObjectProperty;


public class NullableObjectStringFormatter<T> extends StringFormatter {

    private final ObjectProperty<T> objectProperty;

    public NullableObjectStringFormatter(ObjectProperty<T> objectProperty) {
        super.bind(objectProperty);
        this.objectProperty = objectProperty;
    }

    @Override
    public void dispose() {
        super.unbind(objectProperty);
    }

    @Override
    protected String computeValue() {
        if (objectProperty.get() == null) {
            return "";
        } else {
            return objectProperty.get().toString();
        }
    }

}
