package com.shinonometn.re.ssim.commons.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.util.function.Function;

public class FieldValidator {

    private final static Logger logger = LoggerFactory.getLogger("com.shinonometn.web.validator");

    private String field;
    private String message = "field_failed";
    private Function<Object, Boolean> logic;
    private PropertyDescriptor propertyDescriptor;

    public FieldValidator() {
    }

    public FieldValidator(String field, Function<Object, Boolean> logic) {
        this.field = field;
        this.logic = logic;
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
            if (propertyDescriptor == null) {
                logic.apply(new PropertyDescriptor(field, o.getClass()).getReadMethod().invoke(o, null));
            }

            return logic.apply(propertyDescriptor.getReadMethod().invoke(o, null));
        } catch (Exception e) {
            logger.info("Field validation for {} failed, skip, reason {}", field, e);
            return true;
        }
    }
}
