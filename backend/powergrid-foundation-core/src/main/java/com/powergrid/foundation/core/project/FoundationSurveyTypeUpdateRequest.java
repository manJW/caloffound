package com.powergrid.foundation.core.project;

import jakarta.validation.constraints.NotBlank;

public class FoundationSurveyTypeUpdateRequest {
    @NotBlank
    private String surveyType;

    public String getSurveyType() {
        return surveyType;
    }

    public void setSurveyType(String surveyType) {
        this.surveyType = surveyType;
    }
}
