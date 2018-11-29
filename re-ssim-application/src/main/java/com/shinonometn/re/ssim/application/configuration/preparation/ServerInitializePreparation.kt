package com.shinonometn.re.ssim.application.configuration.preparation

import com.shinonometn.re.ssim.commons.file.StartupInfoFile
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import java.io.File

@Component
open class ServerInitializePreparation(private val tasks: List<ServerInitializeTask>) : ApplicationListener<ContextRefreshedEvent> {

    private val log = LoggerFactory.getLogger(ServerInitializePreparation::class.java)

    @Value("\${app.dataDir:./}")
    private var dataDir = "./"

    @Value("\${app.forceInitialize:false}")
    private var forceInitialize = false

    private var firstTimeStartup = false

    override fun onApplicationEvent(event: ContextRefreshedEvent) {

        firstTimeStartup = isFirstTimeStartUp()

        tasks.filter { (firstTimeStartup && it.onlyAtFirstTime()) || !it.onlyAtFirstTime() }
                .sortedBy { t -> t.order() }
                .also { log.info("System startup preparation list:\n${it.map { it.name() }}\n") }
                .forEach {
                    try {
                        it.run()
                        log.info("Task [${it.order()}]${it.name()} finished")
                    } catch (e: Exception) {
                        StartupInfoFile().apply {
                            timestamp = System.currentTimeMillis()

                            errorInfo = LinkedHashMap<String, Any?>().apply {
                                this["failedTask"] = it
                                this["cause"] = e.cause.toString()
                                this["errorMessage"] = e.message
                            }
                        }.toFile(File(dataDir, StartupInfoFile.filename()))

                        throw RuntimeException(e)
                    }
                }

        StartupInfoFile().apply {
            timestamp = System.currentTimeMillis()

            meta = LinkedHashMap<String,Any?>().apply{
                this["succeededTask"] = tasks.filter { (firstTimeStartup && it.onlyAtFirstTime()) || !it.onlyAtFirstTime() }
                this["skippedTask"] = tasks.filter { firstTimeStartup }
            }
        }.toFile(File(dataDir, StartupInfoFile.filename()))
    }

    private fun isFirstTimeStartUp() = if (forceInitialize) true else File(dataDir, StartupInfoFile.filename()).exists().not()
}
