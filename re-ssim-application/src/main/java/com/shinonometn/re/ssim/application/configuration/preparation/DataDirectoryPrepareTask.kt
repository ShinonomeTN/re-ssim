package com.shinonometn.re.ssim.application.configuration.preparation

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File

@Component
class DataDirectoryPrepareTask : ServerInitializeTask {

    @Value("\${app.dataDir:./}")
    private var dataDir = "./"

    override fun order() = 0

    override fun onlyAtFirstTime() = true

    override fun run() {
        val dataDir = File(this.dataDir)
        if (!dataDir.exists() && !dataDir.mkdir() && !dataDir.mkdirs()) throw IllegalStateException("Could not create data directory ${dataDir.absolutePath}")
    }

}