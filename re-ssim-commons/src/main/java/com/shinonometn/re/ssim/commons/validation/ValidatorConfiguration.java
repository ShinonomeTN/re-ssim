package com.shinonometn.re.ssim.commons.validation;

import java.util.HashSet;
import java.util.Set;

public class ValidatorConfiguration {

    private final Set<ValidationMeta> logicSet = new HashSet<>();

    public void addLogic(ValidationMeta validatorMeta) {
        logicSet.add(validatorMeta);
    }

    public Set<ValidationMeta> getLogicSet() {
        return logicSet;
    }

}
