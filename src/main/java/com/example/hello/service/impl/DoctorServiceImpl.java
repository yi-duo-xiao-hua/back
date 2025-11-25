package com.example.hello.service.impl;

import com.example.hello.common.Result;
import com.example.hello.dto.*;
import com.example.hello.entity.Doctor;
import com.example.hello.mapper.DoctorMapper;
import com.example.hello.service.DoctorService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 医生服务实现类
 */
@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorMapper doctorMapper;

    @Value("${file.upload.path:/uploads/doctors}")
    private String uploadPath;

    @Override
    public Result<PageResult<DoctorListVO>> getDoctorList(DoctorQueryDTO queryDTO) {
        // 设置分页参数
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getPageSize());

        // 查询数据
        List<Doctor> doctors = doctorMapper.selectDoctorList(
                queryDTO.getName(),
                queryDTO.getHospitalName(),
                queryDTO.getDepartment(),
                queryDTO.getTitle()
        );

        // 转换为VO
        List<DoctorListVO> voList = doctors.stream().map(doctor -> {
            DoctorListVO vo = new DoctorListVO();
            BeanUtils.copyProperties(doctor, vo);
            return vo;
        }).collect(Collectors.toList());

        // 获取分页信息
        PageInfo<Doctor> pageInfo = new PageInfo<>(doctors);
        PageResult<DoctorListVO> pageResult = new PageResult<>(
                pageInfo.getTotal(),
                voList
        );

        return Result.success(pageResult);
    }

    @Override
    public Result<DoctorDetailVO> getDoctorById(Integer doctorId) {
        if (doctorId == null) {
            return Result.error("医生ID不能为空");
        }

        Doctor doctor = doctorMapper.selectDoctorById(doctorId);
        if (doctor == null) {
            return Result.error("医生不存在");
        }

        DoctorDetailVO vo = new DoctorDetailVO();
        BeanUtils.copyProperties(doctor, vo);
        // 设置原始文件名（如果有）
        if (StringUtils.hasText(doctor.getAvatarUrl())) {
            vo.setAvatarOriginalName(extractFileName(doctor.getAvatarUrl()));
        }

        return Result.success(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> addDoctor(DoctorDTO doctorDTO) {
        // 校验医院是否存在
        int count = doctorMapper.checkHospitalExists(doctorDTO.getHospitalId());
        if (count == 0) {
            return Result.error("医院ID不存在");
        }

        Doctor doctor = new Doctor();
        BeanUtils.copyProperties(doctorDTO, doctor);
        doctor.setCreateTime(LocalDateTime.now());
        doctor.setUpdateTime(LocalDateTime.now());

        int result = doctorMapper.insertDoctor(doctor);
        if (result > 0) {
            return Result.success("添加成功", null);
        } else {
            return Result.error("添加失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateDoctor(DoctorDTO doctorDTO) {
        if (doctorDTO.getDoctorId() == null) {
            return Result.error("医生ID不能为空");
        }

        // 检查医生是否存在
        Doctor existDoctor = doctorMapper.selectDoctorById(doctorDTO.getDoctorId());
        if (existDoctor == null) {
            return Result.error("医生不存在");
        }

        // 校验医院是否存在
        int count = doctorMapper.checkHospitalExists(doctorDTO.getHospitalId());
        if (count == 0) {
            return Result.error("医院ID不存在");
        }

        Doctor doctor = new Doctor();
        BeanUtils.copyProperties(doctorDTO, doctor);
        doctor.setUpdateTime(LocalDateTime.now());

        int result = doctorMapper.updateDoctor(doctor);
        if (result > 0) {
            return Result.success("修改成功", null);
        } else {
            return Result.error("修改失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteDoctor(Integer doctorId) {
        if (doctorId == null) {
            return Result.error("医生ID不能为空");
        }

        Doctor doctor = doctorMapper.selectDoctorById(doctorId);
        if (doctor == null) {
            return Result.error("医生不存在");
        }

        int result = doctorMapper.deleteDoctor(doctorId);
        if (result > 0) {
            return Result.success("删除成功", null);
        } else {
            return Result.error("删除失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<AvatarUploadVO> uploadAvatar(MultipartFile file, Integer doctorId) {
        // 校验文件
        if (file == null || file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        // 校验文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("格式错误，上传失败");
        }

        // 校验文件大小（10MB）
        long fileSize = file.getSize();
        if (fileSize > 10 * 1024 * 1024) {
            return Result.error("图片过大，上传失败");
        }

        try {
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = "avatar_" + UUID.randomUUID().toString().replace("-", "") + extension;

            // 创建上传目录
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 保存文件
            Path filePath = uploadDir.resolve(fileName);
            Files.write(filePath, file.getBytes());

            // 生成访问路径
            String avatarUrl = "/uploads/doctors/" + fileName;

            // 如果提供了doctorId，更新医生信息
            if (doctorId != null) {
                Doctor doctor = doctorMapper.selectDoctorById(doctorId);
                if (doctor != null) {
                    doctor.setAvatarUrl(avatarUrl);
                    doctor.setAvatarFileSize((int) fileSize);
                    doctor.setAvatarFileType(contentType);
                    doctor.setAvatarUploadTime(LocalDateTime.now());
                    doctorMapper.updateDoctor(doctor);
                }
            }

            AvatarUploadVO vo = new AvatarUploadVO(avatarUrl, originalFilename, fileSize);
            return Result.success("上传成功", vo);

        } catch (IOException e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 从URL中提取文件名
     */
    private String extractFileName(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        int lastIndex = url.lastIndexOf("/");
        if (lastIndex >= 0 && lastIndex < url.length() - 1) {
            return url.substring(lastIndex + 1);
        }
        return url;
    }
}

