package com.powergrid.foundation.core.project;

public record FoundationLegRuleConfig(
        Double extractHeight,
        Double heightAllow,
        Double heightStep,
        Double minLzkt,
        Double maxLzkt,
        Integer dado
) {
}
