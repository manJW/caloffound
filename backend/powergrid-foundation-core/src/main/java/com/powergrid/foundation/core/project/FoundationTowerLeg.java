package com.powergrid.foundation.core.project;

public record FoundationTowerLeg(
        String leg,
        String reduce,
        double length,
        double exposed,
        double topElevation,
        double slope,
        String mark
) {
}
