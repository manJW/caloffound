package com.powergrid.foundation;

import java.util.List;

import com.powergrid.foundation.common.ApiResponse;
import com.powergrid.foundation.core.project.FoundationProjectService;
import com.powergrid.foundation.core.project.FoundationTowerAssignmentItem;
import com.powergrid.foundation.core.project.FoundationTowerAssignmentUpdateRequest;
import com.powergrid.foundation.core.project.FoundationTowerBodySuggestion;
import com.powergrid.foundation.core.project.FoundationLegRuleConfig;
import com.powergrid.foundation.core.project.FoundationLegRuleUpsertRequest;
import com.powergrid.foundation.core.project.FoundationTowerLeg;
import com.powergrid.foundation.core.project.FoundationTowerLegReplaceRequest;
import com.powergrid.foundation.core.project.FoundationTowerLibraryItem;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/foundation/projects/{projectId}/tower-library")
public class FoundationTowerController {
    private final FoundationProjectService foundationProjectService;

    public FoundationTowerController(FoundationProjectService foundationProjectService) {
        this.foundationProjectService = foundationProjectService;
    }

    @GetMapping
    public ApiResponse<List<FoundationTowerLibraryItem>> listTowerLibrary(@PathVariable String projectId) {
        return ApiResponse.ok(
                "Loaded tower library",
                foundationProjectService.listTowerLibrary(projectId)
        );
    }

    @GetMapping("/assignments")
    public ApiResponse<List<FoundationTowerAssignmentItem>> listAssignments(
            @PathVariable String projectId,
            @RequestParam String towerType
    ) {
        return ApiResponse.ok(
                "Loaded tower assignments",
                foundationProjectService.listTowerAssignments(projectId, towerType)
        );
    }

    @GetMapping("/default-body")
    public ApiResponse<FoundationTowerBodySuggestion> getDefaultBodySuggestion(
            @PathVariable String projectId,
            @RequestParam String towerType
    ) {
        return ApiResponse.ok(
                "Loaded default tower body suggestion",
                foundationProjectService.getDefaultTowerBodySuggestion(projectId, towerType)
        );
    }

    @PutMapping("/default-body")
    public ApiResponse<List<FoundationTowerAssignmentItem>> applyDefaultBody(
            @PathVariable String projectId,
            @RequestParam String towerType
    ) {
        return ApiResponse.ok(
                "Applied default tower body",
                foundationProjectService.applyDefaultTowerBody(projectId, towerType)
        );
    }

    @GetMapping("/leg-rule")
    public ApiResponse<FoundationLegRuleConfig> getLegRule(
            @PathVariable String projectId
    ) {
        return ApiResponse.ok(
                "Loaded leg rule config",
                foundationProjectService.getLegRuleConfig(projectId)
        );
    }

    @PutMapping("/leg-rule")
    public ApiResponse<FoundationLegRuleConfig> updateLegRule(
            @PathVariable String projectId,
            @RequestBody FoundationLegRuleUpsertRequest request
    ) {
        return ApiResponse.ok(
                "Updated leg rule config",
                foundationProjectService.updateLegRuleConfig(projectId, request)
        );
    }

    @PutMapping("/nodes/{nodeId}/assignment")
    public ApiResponse<FoundationTowerAssignmentItem> updateAssignment(
            @PathVariable String projectId,
            @PathVariable String nodeId,
            @RequestBody FoundationTowerAssignmentUpdateRequest request
    ) {
        return ApiResponse.ok(
                "Updated tower assignment",
                foundationProjectService.updateTowerAssignment(projectId, nodeId, request.getTowerBodyId(), request.getPositionAdjust())
        );
    }

    @GetMapping("/nodes/{nodeId}/legs")
    public ApiResponse<List<FoundationTowerLeg>> listLegs(
            @PathVariable String projectId,
            @PathVariable String nodeId
    ) {
        return ApiResponse.ok(
                "Loaded tower legs",
                foundationProjectService.listTowerLegs(projectId, nodeId)
        );
    }

    @PutMapping("/nodes/{nodeId}/legs")
    public ApiResponse<List<FoundationTowerLeg>> replaceLegs(
            @PathVariable String projectId,
            @PathVariable String nodeId,
            @Valid @RequestBody FoundationTowerLegReplaceRequest request
    ) {
        return ApiResponse.ok(
                "Updated tower legs",
                foundationProjectService.replaceTowerLegs(projectId, nodeId, request.getLegs())
        );
    }

    @PutMapping("/nodes/{nodeId}/legs/auto-fill")
    public ApiResponse<List<FoundationTowerLeg>> autoFillLegs(
            @PathVariable String projectId,
            @PathVariable String nodeId
    ) {
        return ApiResponse.ok(
                "Auto-filled tower legs",
                foundationProjectService.autoFillTowerLegs(projectId, nodeId)
        );
    }
}
