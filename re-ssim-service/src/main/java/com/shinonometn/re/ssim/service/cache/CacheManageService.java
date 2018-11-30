package com.shinonometn.re.ssim.service.cache;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheManageService {

    private final CacheManager cacheManager;

    public CacheManageService(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }


    @NotNull
    public void clearCache() {
        cacheManager.getCacheNames().forEach(e -> cacheManager.getCache(e).clear());
    }
}
