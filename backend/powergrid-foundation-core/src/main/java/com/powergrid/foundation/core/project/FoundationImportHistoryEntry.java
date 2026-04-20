package com.powergrid.foundation.core.project;

public record FoundationImportHistoryEntry(
        String occurredAt,
        String eventType,
        String status,
        String target,
        String message
) {
}
