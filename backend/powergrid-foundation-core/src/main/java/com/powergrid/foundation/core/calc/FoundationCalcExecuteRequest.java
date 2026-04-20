package com.powergrid.foundation.core.calc;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FoundationCalcExecuteRequest {
    @NotBlank
    private String projectId;
    @NotBlank
    private String nodeId;
    @NotBlank
    private String foundationType;
    @NotBlank
    private String operation;
    @NotBlank
    private String iterationMode;
    @NotNull
    private Map<String, String> values;

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

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }
}
