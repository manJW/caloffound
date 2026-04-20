package com.powergrid.foundation.core.project;

public record FoundationTowerAssignmentItem(
        String nodeId,
        String towerNo,
        String stake,
        String towerBodyId,
        Double positionAdjust,
        int legCount,
        String sceneStatus
) {
}
