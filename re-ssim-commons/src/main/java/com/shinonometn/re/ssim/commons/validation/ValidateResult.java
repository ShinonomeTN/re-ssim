package com.shinonometn.re.ssim.commons.validation;

import java.util.Map;

public class ValidateResult {

    private Map<String,String> messages;

    public ValidateResult() {
    }

    public ValidateResult(Map<String, String> messages) {
        this.messages = messages;
    }

    public Map<String, String> getMessages() {
        return messages;
    }

    public boolean hasError(){
        return !messages.isEmpty();
    }
}
