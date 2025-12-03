package com.example.hello.dto;

public class CurrentUserVO {
    private Integer userId;
    private String username;
    private PatientBriefVO patientInfo;
    private String createTime;
    private String lastLogin;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public PatientBriefVO getPatientInfo() {
        return patientInfo;
    }

    public void setPatientInfo(PatientBriefVO patientInfo) {
        this.patientInfo = patientInfo;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public static class PatientBriefVO {
        private Integer patientId;
        private String name;

        public Integer getPatientId() {
            return patientId;
        }

        public void setPatientId(Integer patientId) {
            this.patientId = patientId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}


