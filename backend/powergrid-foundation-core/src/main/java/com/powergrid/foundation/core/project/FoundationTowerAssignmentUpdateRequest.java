package com.powergrid.foundation.core.project;

public class FoundationTowerAssignmentUpdateRequest {
    private String towerBodyId;
    private Double positionAdjust;

    public String getTowerBodyId() {
        return towerBodyId;
    }

    public void setTowerBodyId(String towerBodyId) {
        this.towerBodyId = towerBodyId;
    }

    public Double getPositionAdjust() {
        return positionAdjust;
    }

    public void setPositionAdjust(Double positionAdjust) {
        this.positionAdjust = positionAdjust;
    }
}
