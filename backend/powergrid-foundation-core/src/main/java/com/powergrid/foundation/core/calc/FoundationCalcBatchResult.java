package com.powergrid.foundation.core.calc;

import java.util.List;

public record FoundationCalcBatchResult(
        String projectId,
        int totalCount,
        int successCount,
        int failedCount,
        List<FoundationCalcRecordSummary> records,
        List<String> messages
) {
}
