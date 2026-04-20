package com.powergrid.foundation.core.calc;

import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class FoundationCalcBatchExecuteRequest {
    @NotBlank
    private String projectId;
    @NotEmpty
    private List<String> nodeIds;
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

    public List<String> getNodeIds() {
        return nodeIds;
    }

    public void setNodeIds(List<String> nodeIds) {
        this.nodeIds = nodeIds;
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
