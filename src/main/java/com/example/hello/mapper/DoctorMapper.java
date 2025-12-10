package com.example.hello.mapper;

import com.example.hello.entity.Doctor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 医生Mapper接口
 */
@Mapper
public interface DoctorMapper {
    /**
     * 分页查询医生列表
     */
    List<Doctor> selectDoctorList(@Param("name") String name,
                                   @Param("hospitalName") String hospitalName,
                                   @Param("department") String department,
                                   @Param("title") String title);

    /**
     * 根据ID查询医生详情
     */
    Doctor selectDoctorById(Integer doctorId);

    /**
     * 新增医生
     */
    int insertDoctor(Doctor doctor);

    /**
     * 修改医生
     */
    int updateDoctor(Doctor doctor);

    /**
     * 删除医生
     */
    int deleteDoctor(Integer doctorId);

    /**
     * 检查医院是否存在
     */
    int checkHospitalExists(Integer hospitalId);

}

