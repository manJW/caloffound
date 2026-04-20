package com.powergrid.foundation.core.calc;

import java.util.List;

public record FoundationCalcField(
        String fieldKey,
        String caption,
        String section,
        String controlType,
        boolean required,
        String defaultValue,
        boolean advanced,
        List<String> options
) {
}
