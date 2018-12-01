package com.shinonometn.re.ssim.service.commons;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class InMemoryStoreManager {

    private List<InMemoryStore> stores;

    public InMemoryStoreManager(List<InMemoryStore> stores) {
        this.stores = stores;
    }

    @NotNull
    public List<String> listStores() {
        return stores.stream().map(InMemoryStore::domain).collect(Collectors.toList());
    }
}
