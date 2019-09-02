package com.shinonometn.re.ssim.commons.validation;

import com.shinonometn.re.ssim.commons.KeyValue;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ValidationSettingsImpl implements ValidationBuilder, ValidationMeta {

    private Stack<KeyValue<String, List<FieldValidator>>> meta = new Stack<>();
    private Map<String, List<FieldValidator>> validatorInfo;

    /*
    *
    *
    * ValidationBuilder
    *
    * */

    @Override
    public ValidationBuilder ofGroup(String groupName) {

        Optional<KeyValue<String, List<FieldValidator>>> exists = findByName(groupName);

        if(!exists.isPresent()){
            KeyValue<String, List<FieldValidator>> kv = new KeyValue<>();
            kv.setKey(groupName);
            kv.setValue(new ArrayList<>());
            meta.push(kv);
            return this;
        }

        throw new RuntimeException("Allow ordinary configuring only");
    }

    @Override
    public ValidationBuilder baseOn(String groupName) {
        if (meta.isEmpty()) throw new RuntimeException("Set some groups first");

        List<FieldValidator> latest = findByName(groupName)
                .orElseThrow(() -> new RuntimeException("Base group unknown"))
                .getValue();

        latest().setValue(latest);

        return this;
    }

    @Override
    public ValidationBuilder exclude(String... fields) {
        Set<String> aSet = new HashSet<>(Arrays.asList(fields));
        latest().setValue(latest().getValue().stream().filter(i -> !aSet.contains(i.getField())).collect(Collectors.toList()));
        return this;
    }

    @Override
    public ValidationBuilder addValidator(String field, Function<Object, Boolean> logic) {
        latest().getValue().add(new FieldValidator(field, logic));
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

    private KeyValue<String, List<FieldValidator>> latest() {
        return meta.peek();
    }
}
