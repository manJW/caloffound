package com.powergrid.foundation.core.project;

import java.util.List;

public record FoundationTerrainResource(
        String projectId,
        String nodeId,
        String towerNo,
        String stake,
        String surveyType,
        List<String> surveyPoints,
        boolean hasTifBlob,
        String sceneStatus
) {
}
