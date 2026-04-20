package com.powergrid.foundation.core.persistence.model;

public class FoundationTowerLegRow {
    private String nodeId;
    private String legName;
    private String legReduce;
    private Double legLength;
    private Double exposedHeight;
    private Double topElevation;
    private Double slope;
    private String mark;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getLegName() {
        return legName;
    }

    public void setLegName(String legName) {
        this.legName = legName;
    }

    public String getLegReduce() {
        return legReduce;
    }

    public void setLegReduce(String legReduce) {
        this.legReduce = legReduce;
    }

    public Double getLegLength() {
        return legLength;
    }

    public void setLegLength(Double legLength) {
        this.legLength = legLength;
    }

    public Double getExposedHeight() {
        return exposedHeight;
    }

    public void setExposedHeight(Double exposedHeight) {
        this.exposedHeight = exposedHeight;
    }

    public Double getTopElevation() {
        return topElevation;
    }

    public void setTopElevation(Double topElevation) {
        this.topElevation = topElevation;
    }

    public Double getSlope() {
        return slope;
    }

    public void setSlope(Double slope) {
        this.slope = slope;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
