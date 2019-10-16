package com.shinonometn.re.ssim.commons.validation;


import com.shinonometn.re.ssim.commons.validation.impl.ValidationSettingsImpl;

import java.util.function.Function;

/**
 * 语义化的校验构造器
 */
public interface ValidationMetaBuilder {

    /**
     * Which class the following validation settings for.
     * <p>
     * For the same class in the same ValidationMeta, this method can only be invoke once.
     *
     * @param beanClass target class
     * @return Validation Builder
     */
    default ValidationMetaBuilder of(Class<?> beanClass) {
        return ofGroup(beanClass.getName());
    }

    /**
     * Add a validation group.
     * <p>
     * For the same group in the same ValidationMeta, this method can only be invoke once.
     *
     * @param groupName group name
     * @return ValidationMetaBuilder
     */
    ValidationMetaBuilder ofGroup(String groupName);

    /**
     * Copy validators to current setting group
     *
     * @param groupName group name
     * @return ValidationMetaBuilder
     */
    ValidationMetaBuilder baseOn(String groupName);

    /**
     * Copy validators to current setting class
     *
     * @param beanClass class
     * @return ValidationMetaBuilder
     */
    default ValidationMetaBuilder baseOn(Class<?> beanClass) {
        return baseOn(beanClass.getName());
    }

    /**
     * Remove validator(s) of field(s)
     *
     * @param fields field name(s)
     * @return ValidationMetaBuilder
     */
    ValidationMetaBuilder exclude(String... fields);


    /**
     * Add validator to field
     *
     * @param field field name
     * @param logic validate logic
     * @return ValidationMetaBuilder
     */
    ValidationMetaBuilder addValidator(String field, Function<Object, Boolean> logic);

    /**
     * Add validator to field, with message
     *
     * @param field field name
     * @param logic validate logic
     * @return ValidationMetaBuilder
     */
    ValidationMetaBuilder addValidator(String field, Function<Object, Boolean> logic, String message);

    /**
     * Build the ValidationMeta
     *
     * @return ValidationMeta
     */
    ValidationMeta build();

    /**
     * Start build a ValidationMeta
     *
     * @return ValidationMetaBuilder
     */
    static ValidationMetaBuilder create() {
        return new ValidationSettingsImpl();
    }
}
