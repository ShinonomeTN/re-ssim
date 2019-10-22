package com.shinonometn.re.ssim.data.kingo.application.commons

import org.springframework.context.ApplicationEvent

class ImportTaskEvent(source: Any, val type: EventType, val taskId: Int) : ApplicationEvent(source) {
    enum class EventType {
        START, FINISHED, ERROR
    }
}
