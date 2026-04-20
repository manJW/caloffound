package com.powergrid.foundation.core.calc;

public record FoundationCalcExportDocument(
        String format,
        String fileName,
        String contentType,
        String content
) {
}
