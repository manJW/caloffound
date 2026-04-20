package com.powergrid.foundation.core.calc;

import java.time.Instant;
import java.util.List;

public record FoundationCalcRecordSummary(
        String recordId,
        String projectId,
        String nodeId,
        String foundationType,
        String operation,
        String iterationMode,
        String title,
        Instant createdAt,
        List<FoundationCalcResultItem> items
) {
}
