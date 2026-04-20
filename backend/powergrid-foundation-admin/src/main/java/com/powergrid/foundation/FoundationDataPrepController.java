package com.powergrid.foundation;

import java.util.List;

import com.powergrid.foundation.common.ApiResponse;
import com.powergrid.foundation.core.project.FoundationSurveyBatchImportItem;
import com.powergrid.foundation.core.project.FoundationSurveyBatchImportRequest;
import com.powergrid.foundation.core.project.FoundationSurveyBatchImportResult;
import com.powergrid.foundation.core.project.FoundationProjectService;
import com.powergrid.foundation.core.project.FoundationSurveyPointDocument;
import com.powergrid.foundation.core.project.FoundationSurveyPointReplaceRequest;
import com.powergrid.foundation.core.project.FoundationSurveyTypeUpdateRequest;
import com.powergrid.foundation.core.project.FoundationTerrainResource;
import com.powergrid.foundation.core.project.FoundationTifUploadRequest;
import com.powergrid.foundation.core.project.FoundationImportHistoryEntry;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/foundation/projects/{projectId}")
public class FoundationDataPrepController {
    private final FoundationProjectService foundationProjectService;

    public FoundationDataPrepController(FoundationProjectService foundationProjectService) {
        this.foundationProjectService = foundationProjectService;
    }

    @GetMapping("/terrain-resources")
    public ApiResponse<List<FoundationTerrainResource>> listTerrainResources(@PathVariable String projectId) {
        return ApiResponse.ok(
                "Loaded terrain resources",
                foundationProjectService.listTerrainResources(projectId)
        );
    }

    @GetMapping("/import-history")
    public ApiResponse<List<FoundationImportHistoryEntry>> listImportHistory(@PathVariable String projectId) {
        return ApiResponse.ok(
                "Loaded import history",
                foundationProjectService.listImportHistory(projectId)
        );
    }

    @GetMapping("/nodes/{nodeId}/survey-points")
    public ApiResponse<FoundationSurveyPointDocument> getSurveyPoints(
            @PathVariable String projectId,
            @PathVariable String nodeId
    ) {
        return ApiResponse.ok(
                "Loaded survey points",
                foundationProjectService.getSurveyPoints(projectId, nodeId)
        );
    }

    @PutMapping("/nodes/{nodeId}/survey-type")
    public ApiResponse<FoundationTerrainResource> updateSurveyType(
            @PathVariable String projectId,
            @PathVariable String nodeId,
            @Valid @RequestBody FoundationSurveyTypeUpdateRequest request
    ) {
        return ApiResponse.ok(
                "Updated survey type",
                foundationProjectService.updateSurveyType(projectId, nodeId, request.getSurveyType())
        );
    }

    @PutMapping("/nodes/{nodeId}/survey-points")
    public ApiResponse<FoundationSurveyPointDocument> replaceSurveyPoints(
            @PathVariable String projectId,
            @PathVariable String nodeId,
            @Valid @RequestBody FoundationSurveyPointReplaceRequest request
    ) {
        return ApiResponse.ok(
                "Updated survey points",
                foundationProjectService.replaceSurveyPoints(projectId, nodeId, request.getSurveyType(), request.getLines())
        );
    }

    @PutMapping("/survey-points/batch")
    public ApiResponse<FoundationSurveyBatchImportResult> importSurveyBatch(
            @PathVariable String projectId,
            @Valid @RequestBody FoundationSurveyBatchImportRequest request
    ) {
        return ApiResponse.ok(
                "Imported survey points in batch",
                foundationProjectService.importSurveyBatch(
                        projectId,
                        request.getItems().stream()
                                .map(item -> new FoundationSurveyBatchImportItem(
                                        item.getNodeId(),
                                        item.getSurveyType(),
                                        item.getSourceName(),
                                        item.getLines()
                                ))
                                .toList()
                )
        );
    }

    @PutMapping("/nodes/{nodeId}/tif")
    public ApiResponse<FoundationTerrainResource> uploadNodeTif(
            @PathVariable String projectId,
            @PathVariable String nodeId,
            @Valid @RequestBody FoundationTifUploadRequest request
    ) {
        return ApiResponse.ok(
                "Uploaded node tif",
                foundationProjectService.uploadNodeTif(
                        projectId,
                        nodeId,
                        null,
                        request.contentBase64(),
                        request.resolution()
                )
        );
    }

    @DeleteMapping("/nodes/{nodeId}/tif")
    public ApiResponse<FoundationTerrainResource> clearNodeTif(
            @PathVariable String projectId,
            @PathVariable String nodeId
    ) {
        return ApiResponse.ok(
                "Cleared node tif",
                foundationProjectService.clearNodeTif(projectId, nodeId)
        );
    }
}
