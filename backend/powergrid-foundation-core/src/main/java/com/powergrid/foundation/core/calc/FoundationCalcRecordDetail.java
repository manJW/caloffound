package com.powergrid.foundation.core.calc;

import java.util.List;
import java.util.Map;

public record FoundationCalcRecordDetail(
        FoundationCalcRecordSummary summary,
        Map<String, String> inputValues,
        List<FoundationCalcResultItem> derivedItems,
        List<FoundationCalcGraphicLayer> graphics,
        String rawJson
) {
}
