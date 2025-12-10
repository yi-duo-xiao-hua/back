package com.example.hello.service.impl;

import com.example.hello.common.ObjectStorageProperties;
import com.example.hello.service.ObjectStorageService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.UploadObjectArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * 对象存储实现（基于MinIO/S3协议）
 */
@Service
public class ObjectStorageServiceImpl implements ObjectStorageService {

    private static final Logger log = LoggerFactory.getLogger(ObjectStorageServiceImpl.class);

    private final ObjectStorageProperties properties;
    private MinioClient minioClient;

    public ObjectStorageServiceImpl(ObjectStorageProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        this.minioClient = MinioClient.builder()
                // 按文档说明默认使用外部访问地址；部署在 Sealos Cloud 内部时可改为 internalEndpoint
                .endpoint(properties.getExternalEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
        ensureBucket();
    }

    private void ensureBucket() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(properties.getBucket())
                    .build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(properties.getBucket())
                        .build());
            }
        } catch (Exception e) {
            log.error("初始化对象存储桶失败", e);
        }
    }

    @Override
    public String upload(String objectKey, InputStream inputStream, long size, String contentType) {
        Path tempFile = null;
        try {
            // 将输入流先落盘为临时文件，再按官方文档使用 uploadObject 上传
            tempFile = Files.createTempFile("avatar-", ".tmp");
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

            UploadObjectArgs.Builder builder = UploadObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .object(objectKey)
                    .filename(tempFile.toString());
            if (contentType != null) {
                builder.contentType(contentType);
            }

            minioClient.uploadObject(builder.build());
            return properties.buildPublicUrl(objectKey);
        } catch (Exception e) {
            log.error("上传对象到存储失败", e);
            throw new IllegalStateException("STORAGE_ERROR", e);
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (Exception ignore) {
                }
            }
        }
    }

    @Override
    public void delete(String objectKey) {
        if (objectKey == null || objectKey.isEmpty()) {
            return;
        }
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .object(objectKey)
                    .build());
        } catch (Exception e) {
            log.warn("删除对象失败：{}", objectKey, e);
        }
    }
}



