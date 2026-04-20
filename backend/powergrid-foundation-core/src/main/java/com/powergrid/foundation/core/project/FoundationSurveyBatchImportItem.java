package com.powergrid.foundation.core.project;

import java.util.List;

public record FoundationSurveyBatchImportItem(
        String nodeId,
        String surveyType,
        String sourceName,
        List<String> lines
) {
}
