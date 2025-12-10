package com.example.hello.dto;

import java.util.List;

/**
 * 医生推荐VO
 */
public class DoctorRecommendationVO {

    private String disease;
    private List<String> searchKeywords;
    private Long total;
    private List<DoctorListVO> rows;

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public List<String> getSearchKeywords() {
        return searchKeywords;
    }

    public void setSearchKeywords(List<String> searchKeywords) {
        this.searchKeywords = searchKeywords;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<DoctorListVO> getRows() {
        return rows;
    }

    public void setRows(List<DoctorListVO> rows) {
        this.rows = rows;
    }
}

