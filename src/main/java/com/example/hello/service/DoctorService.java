package com.example.hello.service;

import com.example.hello.common.Result;
import com.example.hello.dto.*;

/**
 * 医生服务接口
 */
public interface DoctorService {
    /**
     * 分页查询医生列表
     */
    Result<PageResult<DoctorListVO>> getDoctorList(DoctorQueryDTO queryDTO);

    /**
     * 根据ID查询医生详情
     */
    Result<DoctorDetailVO> getDoctorById(Integer doctorId);

    /**
     * 新增医生
     */
    Result<Void> addDoctor(DoctorDTO doctorDTO);

    /**
     * 修改医生
     */
    Result<Void> updateDoctor(DoctorDTO doctorDTO);

    /**
     * 删除医生
     */
    Result<Void> deleteDoctor(Integer doctorId);

    /**
     * 上传医生头像
     */
    Result<AvatarUploadVO> uploadAvatar(org.springframework.web.multipart.MultipartFile file, Integer doctorId);

    /**
     * 删除医生头像
     */
    Result<Void> deleteAvatar(Integer doctorId);
}

