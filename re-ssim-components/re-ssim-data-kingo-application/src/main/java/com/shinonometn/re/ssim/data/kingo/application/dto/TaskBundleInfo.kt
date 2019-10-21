package com.shinonometn.re.ssim.data.kingo.application.dto

data class TaskBundleInfo(var hasBundleFile: Boolean = false,
                          var fileCount: Int = 0,
                          var bundleFileSize: Long = 0) {
    val canBundle: Boolean
        get() = fileCount > 0
}