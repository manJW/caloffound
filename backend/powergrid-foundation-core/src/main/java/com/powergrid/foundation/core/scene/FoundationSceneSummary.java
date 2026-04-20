package com.powergrid.foundation.core.scene;

public record FoundationSceneSummary(
        String nodeId,
        String towerNo,
        String stake,
        String surveyType,
        String sceneStatus,
        String terrainSource,
        boolean launchable,
        int terrainPointCount,
        int legCount
) {
}
