package com.powergrid.foundation.core.project;

import java.util.List;

public record FoundationSurveyPointDocument(
        String projectId,
        String nodeId,
        String surveyType,
        List<String> lines
) {
}
