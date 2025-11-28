package com.example.hello.service;

import java.io.InputStream;

/**
 * 对象存储服务
 */
public interface ObjectStorageService {

    /**
     * 上传文件
     *
     * @param objectKey 对象键
     * @param inputStream 文件流
     * @param size 文件大小
     * @param contentType 内容类型
     * @return 可访问的URL
     */
    String upload(String objectKey, InputStream inputStream, long size, String contentType);

    /**
     * 删除文件
     */
    void delete(String objectKey);
}



