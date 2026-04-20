package com.powergrid.foundation.core.project;

public record FoundationNodeDetail(
        String projectId,
        String nodeId,
        String towerNo,
        String stake,
        String surveyType,
        String towerType,
        String sceneStatus,
        String positionDescription
) {
}
