package com.powergrid.foundation.core.persistence.model;

public class FoundationTowerAssignmentRow {
    private String projectId;
    private String nodeId;
    private String towerNo;
    private String stake;
    private String towerType;
    private String towerBodyId;
    private Double posHeightAdjust;
    private Double lon;
    private Double lat;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getTowerNo() {
        return towerNo;
    }

    public void setTowerNo(String towerNo) {
        this.towerNo = towerNo;
    }

    public String getStake() {
        return stake;
    }

    public void setStake(String stake) {
        this.stake = stake;
    }

    public String getTowerType() {
        return towerType;
    }

    public void setTowerType(String towerType) {
        this.towerType = towerType;
    }

    public String getTowerBodyId() {
        return towerBodyId;
    }

    public void setTowerBodyId(String towerBodyId) {
        this.towerBodyId = towerBodyId;
    }

    public Double getPosHeightAdjust() {
        return posHeightAdjust;
    }

    public void setPosHeightAdjust(Double posHeightAdjust) {
        this.posHeightAdjust = posHeightAdjust;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }
}
