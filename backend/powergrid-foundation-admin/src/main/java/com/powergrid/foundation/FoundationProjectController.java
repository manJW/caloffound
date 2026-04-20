package com.powergrid.foundation;

import java.util.List;

import com.powergrid.foundation.common.ApiResponse;
import com.powergrid.foundation.core.project.FoundationNodeDetail;
import com.powergrid.foundation.core.project.FoundationProjectDetail;
import com.powergrid.foundation.core.project.FoundationRegressionSnapshot;
import com.powergrid.foundation.core.project.FoundationRegressionService;
import com.powergrid.foundation.core.project.FoundationProjectService;
import com.powergrid.foundation.core.project.FoundationProjectSummary;
import com.powergrid.foundation.core.project.FoundationProjectNodeUpsertRequest;
import com.powergrid.foundation.core.project.FoundationProjectUpsertRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/foundation/projects")
public class FoundationProjectController {
    private final FoundationProjectService foundationProjectService;
    private final FoundationRegressionService foundationRegressionService;

    public FoundationProjectController(
            FoundationProjectService foundationProjectService,
            FoundationRegressionService foundationRegressionService
    ) {
        this.foundationProjectService = foundationProjectService;
        this.foundationRegressionService = foundationRegressionService;
    }

    @GetMapping
    public ApiResponse<List<FoundationProjectSummary>> listProjects() {
        return ApiResponse.ok(
                "Loaded foundation projects",
                foundationProjectService.listProjects()
        );
    }

    @GetMapping("/{projectId}")
    public ApiResponse<FoundationProjectDetail> getProject(@PathVariable String projectId) {
        return ApiResponse.ok(
                "Loaded foundation project detail",
                foundationProjectService.getProject(projectId)
        );
    }

    @GetMapping("/{projectId}/nodes/{nodeId}")
    public ApiResponse<FoundationNodeDetail> getNode(
            @PathVariable String projectId,
            @PathVariable String nodeId
    ) {
        return ApiResponse.ok(
                "Loaded foundation node detail",
                foundationProjectService.getNode(projectId, nodeId)
        );
    }

    @GetMapping("/{projectId}/nodes/{nodeId}/regression-snapshot")
    public ApiResponse<FoundationRegressionSnapshot> getRegressionSnapshot(
            @PathVariable String projectId,
            @PathVariable String nodeId
    ) {
        return ApiResponse.ok(
                "Loaded foundation regression snapshot",
                foundationRegressionService.buildSnapshot(projectId, nodeId)
        );
    }

    @PostMapping("/{projectId}/nodes")
    public ApiResponse<FoundationProjectDetail> createNode(
            @PathVariable String projectId,
            @Valid @RequestBody FoundationProjectNodeUpsertRequest request
    ) {
        return ApiResponse.ok(
                "Created foundation node",
                foundationProjectService.createNode(projectId, request)
        );
    }

    @PutMapping("/{projectId}/nodes/{nodeId}")
    public ApiResponse<FoundationProjectDetail> updateNode(
            @PathVariable String projectId,
            @PathVariable String nodeId,
            @Valid @RequestBody FoundationProjectNodeUpsertRequest request
    ) {
        return ApiResponse.ok(
                "Updated foundation node",
                foundationProjectService.updateNode(projectId, nodeId, request)
        );
    }

    @DeleteMapping("/{projectId}/nodes/{nodeId}")
    public ApiResponse<Void> deleteNode(
            @PathVariable String projectId,
            @PathVariable String nodeId
    ) {
        foundationProjectService.deleteNode(projectId, nodeId);
        return ApiResponse.ok("Deleted foundation node", null);
    }

    @PostMapping
    public ApiResponse<FoundationProjectDetail> createProject(
            @Valid @RequestBody FoundationProjectUpsertRequest request
    ) {
        return ApiResponse.ok(
                "Created foundation project",
                foundationProjectService.createProject(request)
        );
    }

    @PutMapping("/{projectId}")
    public ApiResponse<FoundationProjectDetail> updateProject(
            @PathVariable String projectId,
            @Valid @RequestBody FoundationProjectUpsertRequest request
    ) {
        return ApiResponse.ok(
                "Updated foundation project",
                foundationProjectService.updateProject(projectId, request)
        );
    }

    @DeleteMapping("/{projectId}")
    public ApiResponse<Void> deleteProject(@PathVariable String projectId) {
        foundationProjectService.deleteProject(projectId);
        return ApiResponse.ok("Deleted foundation project", null);
    }
}
