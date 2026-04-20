package com.powergrid.foundation.core.scene;

import java.util.List;

public record FoundationScenePayload(
        String projectId,
        String nodeId,
        String towerNo,
        String stake,
        String surveyType,
        String sceneStatus,
        String terrainSource,
        boolean launchable,
        int terrainPointCount,
        int legCount,
        String recommendedView,
        FoundationScenePoint cameraPosition,
        FoundationScenePoint cameraTarget,
        List<FoundationScenePoint> terrainPoints,
        FoundationScenePoint towerPoint,
        List<FoundationScenePoint> legPoints,
        List<FoundationSceneLayer> layers,
        List<String> blockers
) {
}
