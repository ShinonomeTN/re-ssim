package com.shinonometn.re.ssim.commons.validation;

import java.util.Collection;
import java.util.Map;

public class ValidateResult {

    private Map<String, Collection<String>> messages;

    public ValidateResult() {
    }

    public ValidateResult(Map<String, Collection<String>> messages) {
        this.messages = messages;
    }

    public Map<String, Collection<String>> getMessages() {
        return messages;
    }

    public boolean hasError() {
        return !messages.isEmpty();
    }
}
