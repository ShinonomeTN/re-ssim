package com.shinonometn.re.ssim.data.kingo.application

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.task.TaskExecutor
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.integration.redis.util.RedisLockRegistry
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.io.File

fun main(args: Array<String>) {
    SpringApplication.run(ReSsimCaterpillarApplication::class.java, *args)
}

@SpringBootApplication
open class ReSsimCaterpillarApplication {

    @Value("\${info.application.data.path}")
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

    @Bean
    open fun redisLockRegistry(redisConnectionFactory: RedisConnectionFactory): RedisLockRegistry {
        return RedisLockRegistry(redisConnectionFactory, "ressim_data_kingo_locks")
    }
}