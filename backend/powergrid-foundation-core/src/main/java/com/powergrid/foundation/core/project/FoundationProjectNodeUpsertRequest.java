package com.powergrid.foundation.core.project;

import jakarta.validation.constraints.NotBlank;

public record FoundationProjectNodeUpsertRequest(
        @NotBlank(message = "Tower number is required")
        String towerNo,
        @NotBlank(message = "Stake is required")
        String stake,
        Integer nodeOrder
) {
}
