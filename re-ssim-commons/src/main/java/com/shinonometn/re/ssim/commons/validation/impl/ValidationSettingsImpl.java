package com.shinonometn.re.ssim.commons.validation.impl;

import com.shinonometn.re.ssim.commons.KeyValue;
import com.shinonometn.re.ssim.commons.validation.FieldValidator;
import com.shinonometn.re.ssim.commons.validation.ValidationMeta;
import com.shinonometn.re.ssim.commons.validation.ValidationMetaBuilder;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ValidationSettingsImpl implements ValidationMetaBuilder, ValidationMeta {

    private Stack<KeyValue<String, List<FieldValidator>>> meta = new Stack<>();
    private Map<String, List<FieldValidator>> validatorInfo;

    /*
     *
     * ValidationMetaBuilder
     *
     * */

    @Override
    public ValidationMetaBuilder ofGroup(String groupName) {

        Optional<KeyValue<String, List<FieldValidator>>> exists = findByName(groupName);

        if (!exists.isPresent()) {
            KeyValue<String, List<FieldValidator>> kv = new KeyValue<>();
            kv.setKey(groupName);
            kv.setValue(new LinkedList<>());
            meta.push(kv);
            return this;
        }

        throw new RuntimeException("Group already in the same ValidationMeta. Only ordinary configuring allowed.");
    }

    @Override
    public ValidationMetaBuilder baseOn(String groupName) {
        if (meta.isEmpty()) throw new RuntimeException("ValidationMeta is empty. Nothing can base on.");

        List<FieldValidator> queryResult = findByName(groupName)
                .orElseThrow(() -> new RuntimeException("Target group not found."))
                .getValue();

        KeyValue<String, List<FieldValidator>> current = latest();
        // Copy all the validator reference to new Setting
        current.getValue().addAll(queryResult);

        return this;
    }

    @Override
    public ValidationMetaBuilder exclude(String... fields) {
        Set<String> aSet = new HashSet<>(Arrays.asList(fields));

        // Get all validator in origin list
        // pick all which field name not in set and
        // put the result back
        latest().setValue(
                latest()
                        .getValue()
                        .stream()
                        .filter(i -> !aSet.contains(i.getField()))
                        .collect(Collectors.toList())
        );

        return this;
    }

    public ValidationMetaBuilder addValidator(String field, Function<Object, Boolean> logic) {
        latest().getValue().add(new FieldValidator(field, logic));
        return this;
    }

    public ValidationMetaBuilder addValidator(String field, Function<Object, Boolean> logic, String message) {
        latest().getValue().add(new FieldValidator(field, logic, message));
        return this;
    }

    @Override
    public ValidationMeta build() {
        validatorInfo = meta.stream().collect(Collectors.toMap(
                KeyValue::getKey,
                KeyValue::getValue
        ));

        return this;
    }

    /*
     *
     * ValidationMeta
     *
     * */

    @Override
    public Set<String> getGroup() {
        return validatorInfo.keySet();
    }

    @Override
    public List<FieldValidator> getFields(String group) {
        return validatorInfo.getOrDefault(group, Collections.emptyList());
    }

    /*
     *
     * Private procedure
     *
     *
     * */

    private Optional<KeyValue<String, List<FieldValidator>>> findByName(String name) {
        return meta.stream().filter(p -> p.getKey().equals(name)).findFirst();
    }

    // Just get the latest setting item
    private KeyValue<String, List<FieldValidator>> latest() {
        return meta.peek();
    }
}
