package com.powergrid.foundation.core.calc;

import java.util.List;

public record FoundationCalcGraphicLayer(
        String layerKey,
        String title,
        String type,
        String stroke,
        String fill,
        List<FoundationCalcPoint> points
) {
}
