package com.example.hello.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 对象存储配置
 */
@Component
@ConfigurationProperties(prefix = "storage")
public class ObjectStorageProperties {
    private String internalEndpoint;
    private String externalEndpoint;
    private String bucket;
    private String accessKey;
    private String secretKey;
    private String basePath = "uploads/doctors";

    public String getInternalEndpoint() {
        return internalEndpoint;
    }

    public void setInternalEndpoint(String internalEndpoint) {
        this.internalEndpoint = internalEndpoint;
    }

    public String getExternalEndpoint() {
        return externalEndpoint;
    }

    public void setExternalEndpoint(String externalEndpoint) {
        this.externalEndpoint = externalEndpoint;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        if (StringUtils.hasText(basePath)) {
            this.basePath = basePath.replaceFirst("^/+", "");
        }
    }

    public String getUploadEndpoint() {
        return StringUtils.hasText(internalEndpoint) ? internalEndpoint : externalEndpoint;
    }

    public String buildObjectKey(String fileName) {
        if (!StringUtils.hasText(basePath)) {
            return fileName;
        }
        return basePath.replaceAll("/+$", "") + "/" + fileName;
    }

    public String buildPublicUrl(String objectKey) {
        if (!StringUtils.hasText(objectKey)) {
            return null;
        }
        if (!StringUtils.hasText(externalEndpoint)) {
            return "/" + bucket + "/" + objectKey;
        }
        String endpoint = externalEndpoint.endsWith("/") ? externalEndpoint.substring(0, externalEndpoint.length() - 1)
                : externalEndpoint;
        return endpoint + "/" + bucket + "/" + objectKey;
    }
}



