package com.powergrid.foundation.core.project;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class FoundationSurveyBatchImportRequest {
    @Valid
    @NotNull
    private List<FoundationSurveyBatchImportRequestItem> items;

    public List<FoundationSurveyBatchImportRequestItem> getItems() {
        return items;
    }

    public void setItems(List<FoundationSurveyBatchImportRequestItem> items) {
        this.items = items;
    }
}
