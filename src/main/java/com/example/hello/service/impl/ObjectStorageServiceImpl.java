package com.example.hello.service.impl;

import com.example.hello.common.ObjectStorageProperties;
import com.example.hello.service.ObjectStorageService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;

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
                .endpoint(properties.getUploadEndpoint())
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
        try (InputStream stream = inputStream) {
            PutObjectArgs.Builder builder = PutObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .object(objectKey)
                    .stream(stream, size, -1);
            if (contentType != null) {
                builder.contentType(contentType);
            }
            minioClient.putObject(builder.build());
            return properties.buildPublicUrl(objectKey);
        } catch (Exception e) {
            log.error("上传对象到存储失败", e);
            throw new IllegalStateException("上传失败，请重试");
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



