package com.powergrid.foundation.core.calc;

import java.util.List;

public record FoundationCalcSchema(
        String foundationType,
        String operation,
        String iterationMode,
        List<FoundationCalcField> fields
) {
}
