package com.powergrid.foundation.core.calc;

import java.util.List;

public record FoundationCalcPreviewShape(
        String label,
        String type,
        String stroke,
        String fill,
        List<FoundationCalcPoint> points
) {
}
