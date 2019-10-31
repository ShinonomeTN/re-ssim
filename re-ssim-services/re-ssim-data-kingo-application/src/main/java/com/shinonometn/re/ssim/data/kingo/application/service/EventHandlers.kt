package com.shinonometn.re.ssim.data.kingo.application.service

import com.shinonometn.re.ssim.data.kingo.application.commons.ImportTaskEvent
import com.shinonometn.re.ssim.data.kingo.application.entity.TermInfo
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationListener
import org.springframework.integration.redis.util.RedisLockRegistry
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Component
class ImportTaskEventHandler(private val termManageService: TermManageService,
                             private val taskService: TaskService,
                             private val redisLockRegistry: RedisLockRegistry) : ApplicationListener<ImportTaskEvent> {

    private val logger = LoggerFactory.getLogger("ressim.kingo.import_task_event_handler")

    override fun onApplicationEvent(event: ImportTaskEvent) {
        logger.debug("Received event {} from {}. {}", event.type, event.source, event)
        if (event.type != ImportTaskEvent.EventType.FINISHED) return

        val captureTaskQuery = taskService.get(event.taskId)
        if (!captureTaskQuery.isPresent) {
            logger.debug("Term code unknown for event {}. Skip actions.", event)
            return
        }

        val captureTask = captureTaskQuery.get().taskInfo ?: return run {
            logger.debug("Capture task info broken. {}", event)
        }

        try {
            termManageService.refreshTermInfo(captureTask.termCode!!, captureTask.termName!!, captureTask.versionCode!!)
        }finally {
//            lock.unlock()
        }
    }

}