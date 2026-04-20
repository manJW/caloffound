package com.powergrid.foundation.core.project;

import java.util.List;

import com.powergrid.foundation.core.calc.FoundationCalcRecordDetail;
import com.powergrid.foundation.core.scene.FoundationScenePayload;
import com.powergrid.foundation.core.scene.FoundationSceneSummary;

public record FoundationRegressionSnapshot(
        String persistenceMode,
        String projectId,
        String nodeId,
        FoundationNodeDetail node,
        FoundationTerrainResource terrainResource,
        FoundationSurveyPointDocument surveyDocument,
        FoundationTowerAssignmentItem towerAssignment,
        List<FoundationTowerLeg> towerLegs,
        FoundationLegRuleConfig legRuleConfig,
        FoundationCalcRecordDetail latestCalculation,
        FoundationSceneSummary sceneSummary,
        FoundationScenePayload scenePayload,
        List<FoundationImportHistoryEntry> recentImportHistory
) {
}
