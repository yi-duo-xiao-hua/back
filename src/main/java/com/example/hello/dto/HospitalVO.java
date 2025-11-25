package com.example.hello.dto;

/**
 * 医院VO
 */
public class HospitalVO {
    private Integer hospitalId;
    private String name;

    public HospitalVO() {
    }

    public HospitalVO(Integer hospitalId, String name) {
        this.hospitalId = hospitalId;
        this.name = name;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

