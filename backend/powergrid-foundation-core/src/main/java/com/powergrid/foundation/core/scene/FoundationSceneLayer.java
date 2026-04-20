package com.powergrid.foundation.core.scene;

import java.util.List;

public record FoundationSceneLayer(
        String layerKey,
        String title,
        String type,
        String color,
        List<FoundationScenePoint> points
) {
}
