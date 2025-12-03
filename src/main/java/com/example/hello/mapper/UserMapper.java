package com.example.hello.mapper;

import com.example.hello.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User selectByUsername(String username);

    User selectById(Integer userId);

    int insertUser(User user);

    int updateLastLogin(@Param("userId") Integer userId, @Param("lastLogin") java.time.LocalDateTime lastLogin);
}


