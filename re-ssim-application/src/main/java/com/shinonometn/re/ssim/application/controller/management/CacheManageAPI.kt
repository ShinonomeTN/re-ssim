package com.shinonometn.re.ssim.application.controller.management

import com.shinonometn.re.ssim.application.configuration.preparation.endpoint.scanning.ApiDescription
import com.shinonometn.re.ssim.service.cache.CacheManageService
import com.shiononometn.commons.web.RexModel
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/cache")
class CacheManageAPI(private val cacheManageService: CacheManageService){
    /**
     *
     * Force clear all cache
     *
     */
    @ApiDescription(title = "Clear all cache", description = "Clear all caches.")
    @PostMapping("/cache", params = ["clear"])
    @ResponseBody
    @RequiresPermissions("cache:delete")
    open fun clearCache(): RexModel<Any>? {
        cacheManageService.clearCache()
        return RexModel.success<Any>()
    }
}
