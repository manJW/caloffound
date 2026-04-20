package com.powergrid.foundation.core.project;

record FoundationTowerLegFillContext(
        String towerType,
        String towerBodyId,
        Double positionAdjust,
        Double baseTopElevation
) {
}
