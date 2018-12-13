package com.shinonometn.re.ssim.commons.validation;

import java.util.function.Function;

public interface ValidationBuilder {

    ValidationBuilder ofGroup(String groupName);

    default ValidationBuilder of(Class<?> beanClass) {
        return ofGroup(beanClass.getName());
    }

    ValidationBuilder baseOn(String groupName);

    default ValidationBuilder baseOn(Class<?> beanClass) {
        return baseOn(beanClass.getName());
    }

    ValidationBuilder exclude(String... fields);

    ValidationBuilder addValidator(String field, Function<Object, Boolean> logic);

    ValidationMeta build();

    static ValidationBuilder create() {
        return new ValidationSettingsImpl();
    }
}
