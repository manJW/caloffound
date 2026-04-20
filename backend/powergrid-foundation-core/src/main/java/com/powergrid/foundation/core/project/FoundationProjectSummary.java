package com.powergrid.foundation.core.project;

public record FoundationProjectSummary(
        String projectId,
        String projectName,
        String projectCode,
        String startAddress,
        String endAddress,
        int nodeCount
) {
}
