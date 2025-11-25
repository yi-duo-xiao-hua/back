package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.dto.*;
import com.example.hello.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 医生管理控制器
 */
@RestController
@RequestMapping("/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    /**
     * 医生信息分页查询
     */
    @GetMapping
    public Result<PageResult<DoctorListVO>> getDoctorList(DoctorQueryDTO queryDTO) {
        return doctorService.getDoctorList(queryDTO);
    }

    /**
     * 根据ID查询医生详情
     */
    @GetMapping("/{id}")
    public Result<DoctorDetailVO> getDoctorById(@PathVariable Integer id) {
        return doctorService.getDoctorById(id);
    }

    /**
     * 新增医生
     */
    @PostMapping
    public Result<Void> addDoctor(@Valid @RequestBody DoctorDTO doctorDTO) {
        return doctorService.addDoctor(doctorDTO);
    }

    /**
     * 修改医生
     */
    @PutMapping
    public Result<Void> updateDoctor(@Valid @RequestBody DoctorDTO doctorDTO) {
        return doctorService.updateDoctor(doctorDTO);
    }

    /**
     * 删除医生
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteDoctor(@PathVariable Integer id) {
        return doctorService.deleteDoctor(id);
    }

    /**
     * 医生头像上传
     */
    @PostMapping("/avatar/upload")
    public Result<AvatarUploadVO> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "doctorId", required = false) Integer doctorId) {
        return doctorService.uploadAvatar(file, doctorId);
    }
}

