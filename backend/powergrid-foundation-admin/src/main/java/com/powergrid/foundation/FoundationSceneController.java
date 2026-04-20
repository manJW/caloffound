package com.powergrid.foundation;

import java.util.List;

import com.powergrid.foundation.common.ApiResponse;
import com.powergrid.foundation.core.scene.FoundationScenePayload;
import com.powergrid.foundation.core.scene.FoundationSceneService;
import com.powergrid.foundation.core.scene.FoundationSceneSummary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/foundation/projects/{projectId}/scene")
public class FoundationSceneController {
    private final FoundationSceneService foundationSceneService;

    public FoundationSceneController(FoundationSceneService foundationSceneService) {
        this.foundationSceneService = foundationSceneService;
    }

    @GetMapping("/nodes")
    public ApiResponse<List<FoundationSceneSummary>> listNodes(@PathVariable String projectId) {
        return ApiResponse.ok(
                "Loaded scene-ready nodes",
                foundationSceneService.listScenes(projectId)
        );
    }

    @GetMapping("/nodes/{nodeId}")
    public ApiResponse<FoundationScenePayload> loadNodeScene(
            @PathVariable String projectId,
            @PathVariable String nodeId
    ) {
        return ApiResponse.ok(
                "Loaded node scene payload",
                foundationSceneService.loadScene(projectId, nodeId)
        );
    }
}
