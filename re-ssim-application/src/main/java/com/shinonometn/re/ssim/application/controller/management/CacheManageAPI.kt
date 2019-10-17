package com.shinonometn.re.ssim.application.controller.management

import com.shinonometn.re.ssim.application.configuration.preparation.endpoint.scanning.ApiDescription
import com.shinonometn.re.ssim.service.cache.CacheManageService
import com.shinonometn.re.ssim.service.commons.InMemoryStore
import com.shinonometn.re.ssim.service.commons.InMemoryStoreManager
import com.shiononometn.commons.web.RexModel
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/storage/memory")
open class CacheManageAPI(private val cacheManageService: CacheManageService,
                          private val inMemoryStoreManager: InMemoryStoreManager) {

    @GetMapping
    @ApiDescription(title = "List memory store information", description = "List all registered InMemoryStore descriptions")
    @RequiresPermissions("memoryStore:info:read")
    open fun listStores() : Any = inMemoryStoreManager.listStores()

    /**
     *
     * Force clear all cache
     *
     */
    @ApiDescription(title = "Clear all cache", description = "Clear all caches.")
    @PostMapping("/cache", params = ["clear"])
    @RequiresPermissions("cache:delete")
    open fun clearCache(): RexModel<Any>? {
        cacheManageService.clearCache()
        return RexModel.success<Any>()
    }


}
