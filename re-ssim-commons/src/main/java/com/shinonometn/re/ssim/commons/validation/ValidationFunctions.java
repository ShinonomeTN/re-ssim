package com.shinonometn.re.ssim.commons.validation;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

public final class ValidationFunctions {
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

    @NotNull
    public static Function<Object, Boolean> shouldNull() {
        return Objects::isNull;
    }
}
