package com.powergrid.foundation.core.calc;

import java.util.Map;

public record FoundationCalcTemplate(
        String templateName,
        Map<String, String> values
) {
}
