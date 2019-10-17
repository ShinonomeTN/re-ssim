package com.shinonometn.re.ssim.commons.validation.helper;

import java.util.Objects;
import java.util.function.Function;

public final class ValidateFunctions {
    public static Function<Object, Boolean> forRegex(String regex) {
        return o -> ((String) o).matches(regex);
    }

    public static Function<Object, Boolean> forLength(int min, int max) {
        return o -> {
            int length = ((String) o).length();
            return length >= min && length <= max;
        };
    }

    public static Function<Object, Boolean> notNull() {
        return Objects::nonNull;
    }

    public static Function<Object, Boolean> notEmpty() {
        return o -> o != null && !((String) o).isEmpty();
    }

    public static Function<Object, Boolean> shouldNull() {
        return Objects::isNull;
    }

    public static Function<Object, Boolean> integerGt(Integer number) {
        return o -> o instanceof Integer && (((int) o) > number);
    }

    public static Function<Object, Boolean> integerGe(Integer number) {
        return o -> o instanceof Integer && (((int) o) == number);
    }

    public static Function<Object, Boolean> doubleGe(Double v) {
        return o -> o instanceof Double && ((Double) o) >= v;
    }
}
