package com.example.hello.service.impl;

import com.example.hello.common.Result;
import com.example.hello.common.ObjectStorageProperties;
import com.example.hello.dto.AvatarUploadVO;
import com.example.hello.dto.DoctorDTO;
import com.example.hello.dto.DoctorDetailVO;
import com.example.hello.dto.DoctorListVO;
import com.example.hello.dto.DoctorQueryDTO;
import com.example.hello.dto.DoctorRecommendationVO;
import com.example.hello.dto.PageResult;
import com.example.hello.entity.Doctor;
import com.example.hello.mapper.DoctorMapper;
import com.example.hello.service.DoctorService;
import com.example.hello.service.ObjectStorageService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.Collectors;

/**
 * 医生服务实现类
 */
@Service
public class DoctorServiceImpl implements DoctorService {

    private static final Set<String> ALLOWED_TYPES = Set.of("image/png", "image/jpeg", "image/jpg");
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("png", "jpg", "jpeg");
    private static final Map<String, List<String>> DISEASE_KEYWORDS = Map.of(
        "depression", List.of("抑郁"),
        "schizophrenia", List.of("分裂"),
        "anxiety", List.of("焦虑"),
        "insomnia", List.of("失眠", "睡眠"),
        "obsessive", List.of("强迫")
    );

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private ObjectStorageService objectStorageService;

    @Autowired
    private ObjectStorageProperties storageProperties;

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

        // 删除头像文件
        deleteAvatarObjectIfExists(doctor.getAvatarUrl());

        int result = doctorMapper.deleteDoctor(doctorId);
        if (result > 0) {
            return Result.success("删除成功", null);
        } else {
            return Result.error("删除失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<AvatarUploadVO> uploadAvatar(MultipartFile file) {
        Result<AvatarUploadVO> validationError = validateUploadParams(file);
        if (validationError != null) {
            return validationError;
        }

        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        String extension = resolveExtension(originalFilename);
        long fileSize = file.getSize();

        String objectKey = buildAvatarObjectKey(extension);

        try (InputStream inputStream = file.getInputStream()) {
            String avatarUrl = objectStorageService.upload(objectKey, inputStream, fileSize, contentType);
            AvatarUploadVO vo = new AvatarUploadVO(avatarUrl, originalFilename, fileSize, contentType);
            return Result.success("上传成功", vo);
        } catch (IllegalStateException e) {
            return Result.error("存储服务异常，请稍后重试");
        } catch (Exception e) {
            return Result.error("上传失败，请重试");
        }
    }

    @Override
    public Result<DoctorRecommendationVO> recommendByDisease(String disease, Integer page, Integer pageSize) {
        if (disease == null || disease.isBlank()) {
            return Result.error("病症类型不能为空");
        }
        List<String> keywords = DISEASE_KEYWORDS.get(disease.toLowerCase(Locale.ROOT));
        if (keywords == null || keywords.isEmpty()) {
            return Result.error("不支持的病症类型");
        }
        int pageNum = page == null || page < 1 ? 1 : page;
        int size = pageSize == null || pageSize < 1 ? 10 : pageSize;
        PageHelper.startPage(pageNum, size);
        List<Doctor> doctors = doctorMapper.selectBySpecialtyKeywords(keywords);
        List<DoctorListVO> rows = doctors.stream().map(doctor -> {
            DoctorListVO vo = new DoctorListVO();
            BeanUtils.copyProperties(doctor, vo);
            return vo;
        }).collect(Collectors.toList());
        PageInfo<Doctor> pageInfo = new PageInfo<>(doctors);

        DoctorRecommendationVO vo = new DoctorRecommendationVO();
        vo.setDisease(disease);
        vo.setSearchKeywords(keywords);
        vo.setTotal(pageInfo.getTotal());
        vo.setRows(rows);
        return Result.success(vo);
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

    private Result<AvatarUploadVO> validateUploadParams(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.error("请选择图片文件");
        }
        String contentType = file.getContentType();
        if (contentType == null || ALLOWED_TYPES.stream()
                .noneMatch(allowed -> allowed.equalsIgnoreCase(contentType))) {
            return Result.error("格式错误，上传失败");
        }
        long fileSize = file.getSize();
        if (fileSize > 2 * 1024 * 1024L) {
            return Result.error("图片过大，上传失败");
        }
        if (!isAllowedExtension(file.getOriginalFilename())) {
            return Result.error("格式错误，上传失败");
        }
        return null;
    }

    private String buildAvatarObjectKey(String extension) {
        long timestamp = System.currentTimeMillis() / 1000;
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        return "doctor_avatar_" + timestamp + "_" + randomPart + extension;
    }

    private String resolveExtension(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            return ".jpg";
        }
        String ext = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            return ".jpg";
        }
        return "." + ext;
    }

    private boolean isAllowedExtension(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            return false;
        }
        String ext = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        return ALLOWED_EXTENSIONS.contains(ext);
    }

    private void deleteAvatarObjectIfExists(String avatarUrl) {
        String objectKey = extractObjectKey(avatarUrl);
        if (StringUtils.hasText(objectKey)) {
            objectStorageService.delete(objectKey);
        }
    }

    private String extractObjectKey(String avatarUrl) {
        if (!StringUtils.hasText(avatarUrl)) {
            return null;
        }
        String marker = "/" + storageProperties.getBucket() + "/";
        int idx = avatarUrl.indexOf(marker);
        if (idx >= 0) {
            return avatarUrl.substring(idx + marker.length());
        }
        if (avatarUrl.startsWith("http")) {
            int schemaIdx = avatarUrl.indexOf("://");
            if (schemaIdx > 0) {
                int pathIdx = avatarUrl.indexOf('/', schemaIdx + 3);
                if (pathIdx > 0 && pathIdx < avatarUrl.length() - 1) {
                    return avatarUrl.substring(pathIdx + 1);
                }
            }
        }
        return avatarUrl.replaceFirst("^/+", "");
    }
}

