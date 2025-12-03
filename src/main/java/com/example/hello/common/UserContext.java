package com.example.hello.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 用户上下文工具类，用于获取当前登录用户信息
 */
public class UserContext {
    
    public static Integer getCurrentUserId() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                Object userIdAttr = request.getAttribute("userId");
                if (userIdAttr instanceof Number) {
                    return ((Number) userIdAttr).intValue();
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}

