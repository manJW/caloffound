package com.powergrid.foundation.core;

import java.util.List;

public record FoundationSmokeSnapshot(
        String persistenceMode,
        int projectCount,
        int nodeCount,
        int readySceneCount,
        int blockedSceneCount,
        List<String> notes
) {
}
