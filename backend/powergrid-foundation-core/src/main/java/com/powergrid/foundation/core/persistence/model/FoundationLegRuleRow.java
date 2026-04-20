package com.powergrid.foundation.core.persistence.model;

public class FoundationLegRuleRow {
    private String ruleId;
    private String projectId;
    private Double extractHeight;
    private Double heightAllow;
    private Double heightStep;
    private Double minLzkt;
    private Double maxLzkt;
    private Integer dado;

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Double getExtractHeight() {
        return extractHeight;
    }

    public void setExtractHeight(Double extractHeight) {
        this.extractHeight = extractHeight;
    }

    public Double getHeightAllow() {
        return heightAllow;
    }

    public void setHeightAllow(Double heightAllow) {
        this.heightAllow = heightAllow;
    }

    public Double getHeightStep() {
        return heightStep;
    }

    public void setHeightStep(Double heightStep) {
        this.heightStep = heightStep;
    }

    public Double getMinLzkt() {
        return minLzkt;
    }

    public void setMinLzkt(Double minLzkt) {
        this.minLzkt = minLzkt;
    }

    public Double getMaxLzkt() {
        return maxLzkt;
    }

    public void setMaxLzkt(Double maxLzkt) {
        this.maxLzkt = maxLzkt;
    }

    public Integer getDado() {
        return dado;
    }

    public void setDado(Integer dado) {
        this.dado = dado;
    }
}
