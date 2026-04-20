package com.powergrid.foundation.core.project;

public record FoundationLegRuleUpsertRequest(
        Double extractHeight,
        Double heightAllow,
        Double heightStep,
        Double minLzkt,
        Double maxLzkt,
        Integer dado
) {
}
