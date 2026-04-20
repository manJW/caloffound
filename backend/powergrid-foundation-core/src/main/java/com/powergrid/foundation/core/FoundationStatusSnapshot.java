package com.powergrid.foundation.core;

import java.util.List;

public record FoundationStatusSnapshot(
        String moduleName,
        String version,
        String frontendStack,
        String backendStack,
        String renderStack,
        String stage,
        String persistenceMode,
        List<FoundationModuleDescriptor> modules
) {
}
