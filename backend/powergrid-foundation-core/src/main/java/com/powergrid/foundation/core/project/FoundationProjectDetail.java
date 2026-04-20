package com.powergrid.foundation.core.project;

import java.util.List;

public record FoundationProjectDetail(
        String projectId,
        String projectName,
        String projectCode,
        String startAddress,
        String endAddress,
        String remarks,
        List<FoundationProjectNode> nodes
) {
}
