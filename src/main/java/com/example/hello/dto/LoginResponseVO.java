package com.example.hello.dto;

public class LoginResponseVO {
    private String token;
    private SimpleUserInfo userInfo;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public SimpleUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(SimpleUserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public static class SimpleUserInfo {
        private Integer userId;
        private String username;

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
    }
}


