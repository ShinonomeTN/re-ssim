package com.shinonometn.re.ssim.application.configuration.preparation

import com.shinonometn.re.ssim.application.configuration.preparation.endpoint.scanning.ApiEndpointScanningConfiguration
import com.shinonometn.re.ssim.application.configuration.preparation.endpoint.scanning.ApiEndpointScanningResultReceiver
import com.shinonometn.re.ssim.application.configuration.preparation.endpoint.scanning.EndpointInformation
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.util.*

@Component
class EndpointScanningTask(private val mapping: RequestMappingHandlerMapping,
                           private val configuration: ApiEndpointScanningConfiguration,
                           private val receiver: ApiEndpointScanningResultReceiver) : ServerInitializeTask {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun order() = 2

    override fun onlyAtFirstTime() = false

    override fun run() {
        val scanningTimestamp = Date()

        receiver.acceptResult(mapping.handlerMethods
                .filter { configuration.endpointFilter.apply(it.key, it.value) }
                .map {
                    EndpointInformation().apply {
                        permissionInfo = configuration.apiPermissionInfoExtractor.extract(it.value)
                        metaInfo = configuration.apiMetaInfoExtractor.extract(it.key, it.value)
                        signatureInfo = configuration.apiMethodSignatureExtractor.extract(it.key, it.value)
                    }
                }
                .also {
                    logger.info("Endpoint scanning finished, total {} apis", it.size)
                }, scanningTimestamp)
    }
}
