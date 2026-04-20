package com.powergrid.foundation.core.project;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public class FoundationTowerLegReplaceRequest {
    @NotNull
    private List<FoundationTowerLeg> legs;

    public List<FoundationTowerLeg> getLegs() {
        return legs;
    }

    public void setLegs(List<FoundationTowerLeg> legs) {
        this.legs = legs;
    }
}
