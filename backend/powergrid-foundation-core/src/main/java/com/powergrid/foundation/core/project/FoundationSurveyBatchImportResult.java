package com.powergrid.foundation.core.project;

import java.util.List;

public record FoundationSurveyBatchImportResult(
        int importedCount,
        int skippedCount,
        List<String> messages
) {
}
