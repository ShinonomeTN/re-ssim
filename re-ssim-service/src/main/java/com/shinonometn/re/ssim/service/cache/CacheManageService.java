package com.shinonometn.re.ssim.service.cache;

import com.shinonometn.re.ssim.service.bus.Listener;
import com.shinonometn.re.ssim.service.bus.MessageBus;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheManageService {

    private final CacheManager cacheManager;

    public CacheManageService(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") CacheManager cacheManager,
                              MessageBus messageBus) {
        this.cacheManager = cacheManager;

        messageBus.register(new Listener("import.finished", o -> clearCache()));
    }


    @NotNull
    public void clearCache() {
        cacheManager.getCacheNames().forEach(e -> cacheManager.getCache(e).clear());
    }
}
