package com.shinonometn.re.ssim.commons.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.List;

import static java.util.stream.Collectors.toMap;

public class Validator {

    private final static Logger logger = LoggerFactory.getLogger("com.shinonometn.web.validator");

    private final ValidationMeta validationMeta;
//    private final Map<String, BeanInfo> beanInfoCaching;

    public Validator(ValidationMeta validationMeta) {
        this.validationMeta = validationMeta;

        validationMeta
                .getGroup()
                .stream()
                .filter(name -> name.matches("([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*"))
                .map(name -> {
                    try {
                        return Introspector.getBeanInfo(Class.forName(name));
                    } catch (IntrospectionException | ClassNotFoundException e) {
                        throw new RuntimeException("Could not get class " + name);
                    }
                })
                .collect(toMap(
                        c -> c.getBeanDescriptor().getBeanClass().getName(),
                        c -> c))
                .forEach((name, value) -> validationMeta.getFields(name).forEach(fieldValidator -> {
                    try {
                        fieldValidator.setPropertyDescriptor(new PropertyDescriptor(fieldValidator.getField(), Class.forName(name)));
                    } catch (IntrospectionException | ClassNotFoundException ignore) {
                        throw new RuntimeException(
                                String.format("Could not create field validator for field %s@%s", name, fieldValidator.getField()),
                                ignore);
                    }
                }));
    }

    public void validate(Object form) {
        String group = form.getClass().getName();
        validate(group, form);
    }

    public void validate(String group, Object form) {
        List<FieldValidator> validators = validationMeta.getFields(group);
        if (validators == null) {
            logger.info("Field validators for {} not registered, validation skipped.", group);
            return;
        }

        ValidateResult result = new ValidateResult(validators
                .stream()
                .filter(validator -> !validator.validate(form))
                .collect(toMap(
                        FieldValidator::getField,
                        FieldValidator::getMessage
                )));

        if (result.hasError()) throw new ValidationException(result);
    }
}
