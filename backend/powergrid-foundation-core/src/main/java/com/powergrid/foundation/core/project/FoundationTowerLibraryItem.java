package com.powergrid.foundation.core.project;

public record FoundationTowerLibraryItem(
        String towerType,
        int nodeCount,
        int configuredCount,
        boolean ready
) {
}
