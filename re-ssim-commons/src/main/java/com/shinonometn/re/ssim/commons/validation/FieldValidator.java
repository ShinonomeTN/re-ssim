package com.shinonometn.re.ssim.commons.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.util.function.Function;

public class FieldValidator {

    private final static Logger logger = LoggerFactory.getLogger("com.shinonometn.web.validator");

    private String field;
    private String message;
    private Function<Object, Boolean> logic;
    private PropertyDescriptor propertyDescriptor;

    public FieldValidator() {
    }

    public FieldValidator(String field, Function<Object, Boolean> logic) {
        this.field = field;
        this.logic = logic;

        message = "failed";
    }

    public FieldValidator(String field, Function<Object, Boolean> logic, String message) {
        this.field = field;
        this.logic = logic;

        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Function<Object, Boolean> getLogic() {
        return logic;
    }

    public PropertyDescriptor getPropertyDescriptor() {
        return propertyDescriptor;
    }

    public synchronized void setPropertyDescriptor(PropertyDescriptor propertyDescriptor) {
        this.propertyDescriptor = propertyDescriptor;
    }

    @SuppressWarnings("ConfusingArgumentToVarargsMethod")
    public boolean validate(Object o) {
        try {

            Object value;

            if (propertyDescriptor == null) {
                value = new PropertyDescriptor(field, o.getClass()).getReadMethod().invoke(o, null);
            } else {
                value = propertyDescriptor.getReadMethod().invoke(o, null);
            }

            return logic.apply(value);
        } catch (Exception e) {
            logger.info("Field validation for {} failed, skip", field, e);
            return true;
        }
    }
}
