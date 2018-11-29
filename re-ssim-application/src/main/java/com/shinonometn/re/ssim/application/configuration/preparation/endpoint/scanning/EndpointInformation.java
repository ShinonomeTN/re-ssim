package com.shinonometn.re.ssim.application.configuration.preparation.endpoint.scanning;

import java.io.Serializable;
import java.util.Set;

public class EndpointInformation implements Serializable {

    private SignatureInfo signatureInfo;
    private PermissionInfo permissionInfo;
    private MetaInfo metaInfo;

    public SignatureInfo getSignatureInfo() {
        return signatureInfo;
    }

    public void setSignatureInfo(SignatureInfo signatureInfo) {
        this.signatureInfo = signatureInfo;
    }

    public PermissionInfo getPermissionInfo() {
        return permissionInfo;
    }

    public void setPermissionInfo(PermissionInfo permissionInfo) {
        this.permissionInfo = permissionInfo;
    }

    public MetaInfo getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(MetaInfo metaInfo) {
        this.metaInfo = metaInfo;
    }

    public static class SignatureInfo {
        private String methodSignature;
        private String requestSignature;

        public String getMethodSignature() {
            return methodSignature;
        }

        public void setMethodSignature(String methodSignature) {
            this.methodSignature = methodSignature;
        }

        public String getRequestSignature() {
            return requestSignature;
        }

        public void setRequestSignature(String requestSignature) {
            this.requestSignature = requestSignature;
        }
    }

    public static class PermissionInfo {
        private Set<String> permissionsRequired;
        private Set<String> roleRequired;

        public Set<String> getPermissionsRequired() {
            return permissionsRequired;
        }

        public void setPermissionsRequired(Set<String> permissionsRequired) {
            this.permissionsRequired = permissionsRequired;
        }

        public Set<String> getRoleRequired() {
            return roleRequired;
        }

        public void setRoleRequired(Set<String> roleRequired) {
            this.roleRequired = roleRequired;
        }
    }

    public static class MetaInfo {
        private String title;
        private String description;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
