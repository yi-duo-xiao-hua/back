package com.example.hello.entity;

/**
 * 医院实体类
 */
public class Hospital {
    private Integer hospitalId;
    private String name;

    public Hospital() {
    }

    public Hospital(Integer hospitalId, String name) {
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

