package com.shinonometn.re.ssim.caterpillar.application

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.servlet.config.annotation.EnableWebMvc

import java.io.File

fun main(args: Array<String>) {
    SpringApplication.run(ReSsimCaterpillarApplication::class.java, *args)
}

@SpringBootApplication
@EnableWebMvc
open class ReSsimCaterpillarApplication {

    @Value("\${application.data.path}")
    private val rootFolderPath: String? = null

    @Bean
    open fun taskExecutor(): TaskExecutor {
        return ThreadPoolTaskExecutor()
    }

    @Bean
    open fun rootFolder(): File {
        val file = File(rootFolderPath!!)
        if (!file.exists() && !file.mkdirs())
            throw RuntimeException("Could not create folder " + file.absolutePath + ".")

        return file
    }
}
