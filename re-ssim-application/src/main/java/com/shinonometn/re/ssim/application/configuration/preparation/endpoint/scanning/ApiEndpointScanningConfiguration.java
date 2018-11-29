package com.shinonometn.re.ssim.application.configuration.preparation.endpoint.scanning;

import com.shinonometn.commons.tools.Names;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ApiEndpointScanningConfiguration {

    private ApiMetaInfoExtractor apiMetaInfoExtractor = (requestMappingInfo, handlerMethod) -> {
        ApiDescription apiDescription = handlerMethod.getMethod().getAnnotation(ApiDescription.class);

        EndpointInformation.MetaInfo metaInfo = new EndpointInformation.MetaInfo();
        if (apiDescription == null) return metaInfo;

        metaInfo.setTitle(apiDescription.title());
        metaInfo.setDescription(apiDescription.description());
        return metaInfo;
    };

    private ApiPermissionInfoExtractor apiPermissionInfoExtractor = handlerMethod -> {
        RequiresPermissions requiresPermissions = handlerMethod.getMethod().getAnnotation(RequiresPermissions.class);

        EndpointInformation.PermissionInfo permissionInfo = new EndpointInformation.PermissionInfo();
        if (requiresPermissions == null) return permissionInfo;

        HashSet<String> hashSet = new HashSet<>(Arrays.asList(requiresPermissions.value()));
        permissionInfo.setPermissionsRequired(hashSet);

        return permissionInfo;
    };

    private ApiMethodSignatureExtractor apiMethodSignatureExtractor = (requestMappingInfo, handlerMethod) -> {
        final Function<Set<String>, String> removeQuotingsIfSingle = collection -> {
            String s = String.valueOf(collection);
            if (collection.size() <= 1) return s.replace("[", "").replace("]", "");
            else return s;
        };

        EndpointInformation.SignatureInfo signatureInfo = new EndpointInformation.SignatureInfo();

        signatureInfo.setRequestSignature(String.format(
                "%s@%s%s%s",
                requestMappingInfo.getMethodsCondition().getMethods(),
                removeQuotingsIfSingle.apply(requestMappingInfo.getPatternsCondition().getPatterns()),
                requestMappingInfo.getParamsCondition().getExpressions().isEmpty() ? "" : "?",
                removeQuotingsIfSingle.apply(requestMappingInfo
                        .getParamsCondition()
                        .getExpressions()
                        .stream()
                        .map(NameValueExpression::getName)
                        .collect(Collectors.toSet())
                )));

        signatureInfo.setMethodSignature(String.format(
                "%s%s@%s",
                handlerMethod.getMethod().getName(),
                Names.getShortClassNameList(Arrays.asList(handlerMethod.getMethod().getParameterTypes())),
                Names.getShortClassName(handlerMethod.getMethod().getDeclaringClass().getName())));

        return signatureInfo;

    };

    private BiFunction<RequestMappingInfo, HandlerMethod, Boolean> endpointFilter = (a, b) -> true;

    public ApiMetaInfoExtractor getApiMetaInfoExtractor() {
        return apiMetaInfoExtractor;
    }

    public void setApiMetaInfoExtractor(ApiMetaInfoExtractor apiMetaInfoExtractor) {
        this.apiMetaInfoExtractor = apiMetaInfoExtractor;
    }

    public ApiPermissionInfoExtractor getApiPermissionInfoExtractor() {
        return apiPermissionInfoExtractor;
    }

    public void setApiPermissionInfoExtractor(ApiPermissionInfoExtractor apiPermissionInfoExtractor) {
        this.apiPermissionInfoExtractor = apiPermissionInfoExtractor;
    }

    public ApiMethodSignatureExtractor getApiMethodSignatureExtractor() {
        return apiMethodSignatureExtractor;
    }

    public void setApiMethodSignatureExtractor(ApiMethodSignatureExtractor apiMethodSignatureExtractor) {
        this.apiMethodSignatureExtractor = apiMethodSignatureExtractor;
    }

    public BiFunction<RequestMappingInfo, HandlerMethod, Boolean> getEndpointFilter() {
        return endpointFilter;
    }

    public void setEndpointFilter(BiFunction<RequestMappingInfo, HandlerMethod, Boolean> endpointFilter) {
        this.endpointFilter = endpointFilter;
    }
}
