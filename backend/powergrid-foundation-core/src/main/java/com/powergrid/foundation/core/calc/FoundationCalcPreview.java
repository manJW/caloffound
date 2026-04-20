package com.powergrid.foundation.core.calc;

import java.util.List;

public record FoundationCalcPreview(
        String foundationType,
        String operation,
        String iterationMode,
        List<String> missingFields,
        List<String> geometryHints,
        List<FoundationCalcPreviewShape> shapes
) {
}
