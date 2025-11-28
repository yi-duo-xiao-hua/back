package com.example.hello.dto;

/**
 * 头像上传响应VO
 */
public class AvatarUploadVO {
    private String avatarUrl;
    private String originalName;
    private Long fileSize;
    private String fileType;

    public AvatarUploadVO() {
    }

    public AvatarUploadVO(String avatarUrl, String originalName, Long fileSize, String fileType) {
        this.avatarUrl = avatarUrl;
        this.originalName = originalName;
        this.fileSize = fileSize;
        this.fileType = fileType;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}

