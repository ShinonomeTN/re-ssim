package com.shinonometn.re.ssim.application.configuration.preparation.endpoint.scanning;

import org.springframework.web.method.HandlerMethod;

public interface ApiPermissionInfoExtractor {
    EndpointInformation.PermissionInfo extract(HandlerMethod handlerMethod);
}
