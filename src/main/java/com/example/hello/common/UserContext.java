package com.example.hello.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 用户上下文工具类，用于获取当前登录用户信息
 */
public class UserContext {
    
    /**
     * 获取当前登录用户的ID
     * 从请求头中获取userId，如果不存在则返回默认值1（用于测试）
     * 
     * @return 用户ID
     */
    public static Integer getCurrentUserId() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String userIdStr = request.getHeader("userId");
                if (userIdStr != null && !userIdStr.isEmpty()) {
                    return Integer.parseInt(userIdStr);
                }
            }
        } catch (Exception e) {
            // 如果获取失败，返回默认值
        }
        // 默认返回1，用于测试环境
        // 实际生产环境应该从JWT token或其他认证机制中获取
        return 1;
    }
}

