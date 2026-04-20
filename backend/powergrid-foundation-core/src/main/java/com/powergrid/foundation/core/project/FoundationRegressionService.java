package com.powergrid.foundation.core.project;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.powergrid.foundation.core.FoundationPersistenceMode;
import com.powergrid.foundation.core.calc.FoundationCalcRecordDetail;
import com.powergrid.foundation.core.calc.FoundationCalcRecordSummary;
import com.powergrid.foundation.core.calc.FoundationCalcService;
import com.powergrid.foundation.core.scene.FoundationScenePayload;
import com.powergrid.foundation.core.scene.FoundationSceneService;
import com.powergrid.foundation.core.scene.FoundationSceneSummary;

@Service
public class FoundationRegressionService {
    private final FoundationPersistenceMode persistenceMode;
    private final FoundationProjectService foundationProjectService;
    private final FoundationCalcService foundationCalcService;
    private final FoundationSceneService foundationSceneService;

    public FoundationRegressionService(
            @Value("${foundation.persistence.mode:db}") String persistenceMode,
            FoundationProjectService foundationProjectService,
            FoundationCalcService foundationCalcService,
            FoundationSceneService foundationSceneService
    ) {
        this.persistenceMode = FoundationPersistenceMode.from(persistenceMode);
        this.foundationProjectService = foundationProjectService;
        this.foundationCalcService = foundationCalcService;
        this.foundationSceneService = foundationSceneService;
    }

    public FoundationRegressionSnapshot buildSnapshot(String projectId, String nodeId) {
        FoundationNodeDetail node = foundationProjectService.getNode(projectId, nodeId);
        FoundationTerrainResource terrainResource = foundationProjectService.listTerrainResources(projectId).stream()
                .filter(item -> item.nodeId().equals(nodeId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Terrain resource was not found: " + nodeId));
        FoundationSurveyPointDocument surveyDocument = foundationProjectService.getSurveyPoints(projectId, nodeId);
        FoundationTowerAssignmentItem towerAssignment = foundationProjectService
                .listTowerAssignments(projectId, node.towerType() == null ? "UNKNOWN" : node.towerType()).stream()
                .filter(item -> item.nodeId().equals(nodeId))
                .findFirst()
                .orElse(null);
        List<FoundationTowerLeg> towerLegs = foundationProjectService.listTowerLegs(projectId, nodeId);
        FoundationLegRuleConfig legRuleConfig = foundationProjectService.getLegRuleConfig(projectId);

        List<FoundationCalcRecordSummary> calcHistory = foundationCalcService.listRecords(projectId, nodeId);
        FoundationCalcRecordDetail latestCalculation = calcHistory.isEmpty()
                ? null
                : foundationCalcService.getRecordDetail(calcHistory.get(0).recordId());

        FoundationSceneSummary sceneSummary = foundationSceneService.listScenes(projectId).stream()
                .filter(item -> item.nodeId().equals(nodeId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Scene summary was not found: " + nodeId));
        FoundationScenePayload scenePayload = foundationSceneService.loadScene(projectId, nodeId);

        return new FoundationRegressionSnapshot(
                persistenceMode.value(),
                projectId,
                nodeId,
                node,
                terrainResource,
                surveyDocument,
                towerAssignment,
                towerLegs,
                legRuleConfig,
                latestCalculation,
                sceneSummary,
                scenePayload,
                foundationProjectService.listImportHistory(projectId)
        );
    }
}
