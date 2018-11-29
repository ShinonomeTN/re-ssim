package com.shinonometn.re.ssim.service.commons;

import org.springframework.beans.factory.annotation.Value;

public abstract class InMemoryStoreManagement {

    @Value("${app.inMemoryDataKey:app\\:store}")
    private String keyPrefix = "app:store";

    protected abstract String domain();

    protected String cachePrefix() {
        return keyPrefix + ":" + domain();
    }
}
