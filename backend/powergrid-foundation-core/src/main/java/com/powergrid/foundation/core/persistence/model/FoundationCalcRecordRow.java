package com.powergrid.foundation.core.persistence.model;

import java.time.LocalDateTime;

public class FoundationCalcRecordRow {
    private String recordId;
    private String projectId;
    private String nodeId;
    private String foundationType;
    private String operation;
    private String iterationMode;
    private String title;
    private LocalDateTime createdAt;
    private String inputValuesJson;
    private String resultItemsJson;
    private String derivedItemsJson;
    private String graphicsJson;
    private String exportJson;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

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

    public String getFoundationType() {
        return foundationType;
    }

    public void setFoundationType(String foundationType) {
        this.foundationType = foundationType;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getIterationMode() {
        return iterationMode;
    }

    public void setIterationMode(String iterationMode) {
        this.iterationMode = iterationMode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getInputValuesJson() {
        return inputValuesJson;
    }

    public void setInputValuesJson(String inputValuesJson) {
        this.inputValuesJson = inputValuesJson;
    }

    public String getResultItemsJson() {
        return resultItemsJson;
    }

    public void setResultItemsJson(String resultItemsJson) {
        this.resultItemsJson = resultItemsJson;
    }

    public String getDerivedItemsJson() {
        return derivedItemsJson;
    }

    public void setDerivedItemsJson(String derivedItemsJson) {
        this.derivedItemsJson = derivedItemsJson;
    }

    public String getGraphicsJson() {
        return graphicsJson;
    }

    public void setGraphicsJson(String graphicsJson) {
        this.graphicsJson = graphicsJson;
    }

    public String getExportJson() {
        return exportJson;
    }

    public void setExportJson(String exportJson) {
        this.exportJson = exportJson;
    }
}
