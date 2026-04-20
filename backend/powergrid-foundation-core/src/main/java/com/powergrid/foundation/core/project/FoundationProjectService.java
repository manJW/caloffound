package com.powergrid.foundation.core.project;

import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.powergrid.foundation.core.FoundationPersistenceMode;
import com.powergrid.foundation.core.persistence.mapper.FoundationProjectMapper;
import com.powergrid.foundation.core.persistence.mapper.FoundationLegRuleMapper;
import com.powergrid.foundation.core.persistence.mapper.FoundationSurveyMapper;
import com.powergrid.foundation.core.persistence.mapper.FoundationTowerMapper;
import com.powergrid.foundation.core.persistence.mapper.FoundationTowerReferenceMapper;
import com.powergrid.foundation.core.persistence.model.FoundationProjectNodeRow;
import com.powergrid.foundation.core.persistence.model.FoundationProjectSummaryRow;
import com.powergrid.foundation.core.persistence.model.FoundationSurveyPointRow;
import com.powergrid.foundation.core.persistence.model.FoundationTowerAssignmentRow;
import com.powergrid.foundation.core.persistence.model.FoundationTowerLegRow;

@Service
public class FoundationProjectService {
    private final FoundationPersistenceMode persistenceMode;
    private final FoundationProjectMapper foundationProjectMapper;
    private final FoundationLegRuleMapper foundationLegRuleMapper;
    private final FoundationSurveyMapper foundationSurveyMapper;
    private final FoundationTowerMapper foundationTowerMapper;
    private final FoundationTowerReferenceMapper foundationTowerReferenceMapper;
    private final FoundationProjectMemoryStore memoryStore;

    public FoundationProjectService(
            @Value("${foundation.persistence.mode:db}") String persistenceMode,
            FoundationProjectMapper foundationProjectMapper,
            FoundationLegRuleMapper foundationLegRuleMapper,
            FoundationSurveyMapper foundationSurveyMapper,
            FoundationTowerMapper foundationTowerMapper,
            FoundationTowerReferenceMapper foundationTowerReferenceMapper
    ) {
        this.persistenceMode = FoundationPersistenceMode.from(persistenceMode);
        this.foundationProjectMapper = foundationProjectMapper;
        this.foundationLegRuleMapper = foundationLegRuleMapper;
        this.foundationSurveyMapper = foundationSurveyMapper;
        this.foundationTowerMapper = foundationTowerMapper;
        this.foundationTowerReferenceMapper = foundationTowerReferenceMapper;
        this.memoryStore = new FoundationProjectMemoryStore(!useDatabase());
    }

    public List<FoundationProjectSummary> listProjects() {
        if (useDatabase()) {
            return foundationProjectMapper.selectProjectSummaries().stream()
                    .map(this::toSummary)
                    .toList();
        }
        return memoryStore.listProjectSummaries();
    }

    public FoundationProjectDetail getProject(String projectId) {
        if (useDatabase()) {
            FoundationProjectSummaryRow project = foundationProjectMapper.selectProjectSummary(projectId);
            if (project == null) {
                throw new NoSuchElementException("Project was not found: " + projectId);
            }
            List<FoundationProjectNode> nodes = foundationProjectMapper.selectProjectNodes(projectId).stream()
                    .map(this::toProjectNode)
                    .toList();
            return new FoundationProjectDetail(
                    project.getProjectId(),
                    project.getProjectName(),
                    project.getProjectCode(),
                    project.getStartAddress(),
                    project.getEndAddress(),
                    null,
                    nodes
            );
        }
        return memoryStore.getProjectDetail(projectId);
    }

    public FoundationProjectDetail createProject(FoundationProjectUpsertRequest request) {
        String projectId = "PRJ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        if (useDatabase()) {
            foundationProjectMapper.insertProject(
                    projectId,
                    request.getProjectCode(),
                    request.getProjectName(),
                    blankToNull(request.getStartAddress()),
                    blankToNull(request.getEndAddress())
            );
            return getProject(projectId);
        }
        return memoryStore.createProject(projectId, request);
    }

    public FoundationProjectDetail updateProject(String projectId, FoundationProjectUpsertRequest request) {
        if (useDatabase()) {
            foundationProjectMapper.updateProject(
                    projectId,
                    request.getProjectCode(),
                    request.getProjectName(),
                    blankToNull(request.getStartAddress()),
                    blankToNull(request.getEndAddress())
            );
            return getProject(projectId);
        }
        return memoryStore.updateProject(projectId, request);
    }

    public void deleteProject(String projectId) {
        if (useDatabase()) {
            foundationProjectMapper.deleteProject(projectId);
            return;
        }
        memoryStore.deleteProject(projectId);
    }

    public FoundationNodeDetail getNode(String projectId, String nodeId) {
        if (useDatabase()) {
            FoundationProjectNodeRow row = foundationProjectMapper.selectProjectNodes(projectId).stream()
                    .filter(item -> item.getNodeId().equals(nodeId))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Node was not found: " + nodeId));
            return new FoundationNodeDetail(
                    row.getProjectId(),
                    row.getNodeId(),
                    row.getTowerNo(),
                    row.getStake(),
                    normalizeBlank(row.getSurveyType()),
                    normalizeBlank(row.getTowerType()),
                    deriveSceneStatus(row.getSurveyType()),
                    "Loaded from database"
            );
        }
        return memoryStore.getNodeDetail(projectId, nodeId);
    }

    @Transactional
    public FoundationProjectDetail createNode(String projectId, FoundationProjectNodeUpsertRequest request) {
        if (useDatabase()) {
            int nodeOrder = request.nodeOrder() != null
                    ? request.nodeOrder()
                    : foundationProjectMapper.selectMaxNodeOrder(projectId) + 1;
            foundationProjectMapper.insertProjectNode(
                    "NODE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    projectId,
                    request.towerNo().trim(),
                    request.stake().trim(),
                    nodeOrder
            );
            return getProject(projectId);
        }
        return memoryStore.createNode(projectId, request);
    }

    @Transactional
    public FoundationProjectDetail updateNode(String projectId, String nodeId, FoundationProjectNodeUpsertRequest request) {
        if (useDatabase()) {
            FoundationProjectNodeRow row = findProjectNodeRow(projectId, nodeId);
            foundationProjectMapper.updateProjectNode(
                    nodeId,
                    request.towerNo().trim(),
                    request.stake().trim(),
                    request.nodeOrder() != null ? request.nodeOrder() : row.getNodeOrder()
            );
            return getProject(projectId);
        }
        return memoryStore.updateNode(projectId, nodeId, request);
    }

    @Transactional
    public void deleteNode(String projectId, String nodeId) {
        if (useDatabase()) {
            String surveyId = foundationSurveyMapper.selectSurveyIdByNodeId(nodeId);
            if (surveyId != null && !surveyId.isBlank()) {
                foundationSurveyMapper.deleteSurveyPoints(surveyId);
                foundationSurveyMapper.deleteSurveyHeader(surveyId);
            }
            foundationTowerMapper.deleteTowerLegs(nodeId);
            foundationTowerMapper.deleteTowerAssignment(nodeId);
            foundationProjectMapper.deleteProjectNode(nodeId);
            return;
        }
        memoryStore.deleteNode(projectId, nodeId);
    }

    public List<FoundationTerrainResource> listTerrainResources(String projectId) {
        if (useDatabase()) {
            return foundationProjectMapper.selectProjectNodes(projectId).stream()
                    .map(this::toTerrainResource)
                    .toList();
        }
        return memoryStore.listTerrainResources(projectId);
    }

    public FoundationSurveyPointDocument getSurveyPoints(String projectId, String nodeId) {
        if (useDatabase()) {
            String surveyId = foundationSurveyMapper.selectSurveyIdByNodeId(nodeId);
            List<String> lines = surveyId == null || surveyId.isBlank()
                    ? List.of()
                    : foundationSurveyMapper.selectSurveyPoints(surveyId).stream()
                            .map(this::toSurveyLine)
                            .toList();
            return new FoundationSurveyPointDocument(
                    projectId,
                    nodeId,
                    normalizeBlank(foundationSurveyMapper.selectSurveyTypeByNodeId(nodeId)),
                    lines
            );
        }
        return memoryStore.getSurveyPoints(projectId, nodeId);
    }

    public FoundationTerrainResource updateSurveyType(String projectId, String nodeId, String surveyType) {
        if (useDatabase()) {
            String surveyId = ensureSurveyId(nodeId, surveyType);
            foundationSurveyMapper.updateSurveyHeader(surveyId, blankToNull(surveyType), "Updated from BS");
            addImportHistory(projectId, "survey-type", "success", nodeId, "Survey type updated");
            return listTerrainResources(projectId).stream()
                    .filter(item -> item.nodeId().equals(nodeId))
                    .findFirst()
                    .orElse(new FoundationTerrainResource(projectId, nodeId, null, null, blankToNull(surveyType), List.of(), false, deriveSceneStatus(surveyType)));
        }
        return memoryStore.updateSurveyType(projectId, nodeId, surveyType);
    }

    @Transactional
    public FoundationSurveyPointDocument replaceSurveyPoints(
            String projectId,
            String nodeId,
            String surveyType,
            List<String> surveyPoints
    ) {
        if (useDatabase()) {
            String surveyId = ensureSurveyId(nodeId, surveyType);
            foundationSurveyMapper.updateSurveyHeader(surveyId, blankToNull(surveyType), "Updated from BS");
            foundationSurveyMapper.deleteSurveyPoints(surveyId);
            List<String> cleaned = surveyPoints.stream()
                    .map(String::trim)
                    .filter(item -> !item.isBlank())
                    .toList();
            for (String line : cleaned) {
                double[] point = parseSurveyLine(line);
                foundationSurveyMapper.insertSurveyPoint(
                        UUID.randomUUID().toString().replace("-", "").toUpperCase(),
                        surveyId,
                        point[0],
                        point[1],
                        point[2]
                );
            }
            addImportHistory(projectId, "survey-points", "success", nodeId, "Survey points replaced");
            return getSurveyPoints(projectId, nodeId);
        }
        return memoryStore.replaceSurveyPoints(projectId, nodeId, surveyType, surveyPoints);
    }

    @Transactional
    public FoundationSurveyBatchImportResult importSurveyBatch(
            String projectId,
            List<FoundationSurveyBatchImportItem> items
    ) {
        if (useDatabase()) {
            int importedCount = 0;
            int skippedCount = 0;
            List<String> messages = new ArrayList<>();
            for (FoundationSurveyBatchImportItem item : items) {
                try {
                    replaceSurveyPoints(projectId, item.nodeId(), item.surveyType(), item.lines());
                    importedCount++;
                    messages.add("Imported " + item.sourceName() + " -> " + item.nodeId());
                    addImportHistory(projectId, "survey-batch", "success", item.nodeId(), "Imported " + item.sourceName());
                } catch (NoSuchElementException ex) {
                    skippedCount++;
                    messages.add("Skipped " + item.sourceName() + ": " + ex.getMessage());
                    addImportHistory(projectId, "survey-batch", "skipped", item.nodeId(), "Skipped " + item.sourceName() + ": " + ex.getMessage());
                }
            }
            return new FoundationSurveyBatchImportResult(importedCount, skippedCount, messages);
        }
        return memoryStore.importSurveyBatch(projectId, items);
    }

    @Transactional
    public FoundationTerrainResource uploadNodeTif(
            String projectId,
            String nodeId,
            String surveyType,
            String contentBase64,
            Double resolution
    ) {
        if (useDatabase()) {
            String surveyId = ensureSurveyId(nodeId, surveyType);
            foundationSurveyMapper.updateSurveyTif(
                    surveyId,
                    Base64.getDecoder().decode(contentBase64),
                    resolution
            );
            addImportHistory(projectId, "tif-upload", "success", nodeId, "Uploaded tif content");
            return listTerrainResources(projectId).stream()
                    .filter(item -> item.nodeId().equals(nodeId))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Node was not found: " + nodeId));
        }
        return memoryStore.uploadNodeTif(projectId, nodeId, surveyType);
    }

    @Transactional
    public FoundationTerrainResource clearNodeTif(String projectId, String nodeId) {
        if (useDatabase()) {
            String surveyId = foundationSurveyMapper.selectSurveyIdByNodeId(nodeId);
            if (surveyId != null && !surveyId.isBlank()) {
                foundationSurveyMapper.clearSurveyTif(surveyId);
            }
            addImportHistory(projectId, "tif-clear", "success", nodeId, "Cleared tif content");
            return listTerrainResources(projectId).stream()
                    .filter(item -> item.nodeId().equals(nodeId))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Node was not found: " + nodeId));
        }
        return memoryStore.clearNodeTif(projectId, nodeId);
    }

    public List<FoundationImportHistoryEntry> listImportHistory(String projectId) {
        return memoryStore.importHistory(projectId);
    }

    public List<FoundationTowerLibraryItem> listTowerLibrary(String projectId) {
        if (useDatabase()) {
            List<FoundationTowerAssignmentRow> assignments = foundationTowerMapper.selectTowerAssignments(projectId);
            Map<String, List<FoundationTowerAssignmentRow>> grouped = new LinkedHashMap<>();
            for (FoundationTowerAssignmentRow row : assignments) {
                grouped.computeIfAbsent(defaultTowerType(row.getTowerType()), ignored -> new ArrayList<>()).add(row);
            }
            return grouped.entrySet().stream()
                    .map(entry -> {
                        List<FoundationTowerAssignmentRow> rows = entry.getValue();
                        long configured = rows.stream()
                                .filter(item -> item.getTowerBodyId() != null && !item.getTowerBodyId().isBlank())
                                .count();
                        return new FoundationTowerLibraryItem(
                                entry.getKey(),
                                rows.size(),
                                (int) configured,
                                configured == rows.size()
                        );
                    })
                    .toList();
        }
        return memoryStore.listTowerLibrary(projectId);
    }

    public List<FoundationTowerAssignmentItem> listTowerAssignments(String projectId, String towerType) {
        if (useDatabase()) {
            return foundationTowerMapper.selectTowerAssignments(projectId).stream()
                    .filter(row -> defaultTowerType(row.getTowerType()).equals(towerType))
                    .map(row -> new FoundationTowerAssignmentItem(
                            row.getNodeId(),
                            row.getTowerNo(),
                            row.getStake(),
                            normalizeBlank(row.getTowerBodyId()),
                            row.getPosHeightAdjust(),
                            foundationTowerMapper.selectTowerLegs(row.getNodeId()).size(),
                            deriveTowerSceneStatus(row)
                    ))
                    .toList();
        }
        return memoryStore.listTowerAssignments(projectId, towerType);
    }

    public FoundationTowerAssignmentItem updateTowerAssignment(
            String projectId,
            String nodeId,
            String towerBodyId,
            Double positionAdjust
    ) {
        if (useDatabase()) {
            int updated = foundationTowerMapper.updateTowerAssignment(nodeId, blankToNull(towerBodyId), positionAdjust);
            if (updated == 0) {
                FoundationProjectNodeRow nodeRow = findProjectNodeRow(projectId, nodeId);
                foundationTowerMapper.insertTowerAssignment(
                        UUID.randomUUID().toString().replace("-", "").toUpperCase(),
                        nodeId,
                        blankToNull(nodeRow.getTowerType()),
                        nodeRow.getPosHeight(),
                        null,
                        nodeRow.getLon(),
                        nodeRow.getLat(),
                        "Created from BS",
                        blankToNull(towerBodyId),
                        positionAdjust
                );
            }
            return listTowerAssignments(projectId, defaultTowerType(findTowerType(projectId, nodeId))).stream()
                    .filter(item -> item.nodeId().equals(nodeId))
                    .findFirst()
                    .orElse(new FoundationTowerAssignmentItem(nodeId, null, null, blankToNull(towerBodyId), positionAdjust, 0, "planned"));
        }
        return memoryStore.updateTowerAssignment(projectId, nodeId, towerBodyId, positionAdjust);
    }

    public FoundationTowerBodySuggestion getDefaultTowerBodySuggestion(String projectId, String towerType) {
        if (useDatabase()) {
            String towerTypeId = foundationTowerReferenceMapper.selectProjectTowerTypeId(projectId, towerType);
            if (towerTypeId != null && !towerTypeId.isBlank()) {
                String towerBodyId = foundationTowerReferenceMapper.selectDefaultTowerBodyId(towerTypeId);
                return new FoundationTowerBodySuggestion(
                        towerType,
                        normalizeBlank(towerBodyId),
                        towerBodyId != null && !towerBodyId.isBlank()
                );
            }
            return new FoundationTowerBodySuggestion(towerType, null, false);
        }
        return memoryStore.getDefaultTowerBodySuggestion(towerType);
    }

    @Transactional
    public List<FoundationTowerAssignmentItem> applyDefaultTowerBody(String projectId, String towerType) {
        FoundationTowerBodySuggestion suggestion = getDefaultTowerBodySuggestion(projectId, towerType);
        if (!suggestion.available() || suggestion.towerBodyId() == null || suggestion.towerBodyId().isBlank()) {
            return listTowerAssignments(projectId, towerType);
        }
        List<FoundationTowerAssignmentItem> assignments = listTowerAssignments(projectId, towerType);
        for (FoundationTowerAssignmentItem item : assignments) {
            if (item.towerBodyId() == null || item.towerBodyId().isBlank()) {
                updateTowerAssignment(projectId, item.nodeId(), suggestion.towerBodyId(), item.positionAdjust());
            }
        }
        return listTowerAssignments(projectId, towerType);
    }

    public List<FoundationTowerLeg> listTowerLegs(String projectId, String nodeId) {
        if (useDatabase()) {
            return foundationTowerMapper.selectTowerLegs(nodeId).stream()
                    .map(this::toTowerLeg)
                    .toList();
        }
        return memoryStore.listTowerLegs(projectId, nodeId);
    }

    @Transactional
    public List<FoundationTowerLeg> replaceTowerLegs(
            String projectId,
            String nodeId,
            List<FoundationTowerLeg> legs
    ) {
        if (useDatabase()) {
            foundationTowerMapper.deleteTowerLegs(nodeId);
            for (FoundationTowerLeg leg : legs) {
                foundationTowerMapper.insertTowerLeg(
                        UUID.randomUUID().toString().replace("-", "").toUpperCase(),
                        nodeId,
                        leg.leg(),
                        blankToNull(leg.reduce()),
                        leg.length(),
                        leg.exposed(),
                        leg.topElevation(),
                        leg.slope(),
                        blankToNull(leg.mark())
                );
            }
            return listTowerLegs(projectId, nodeId);
        }
        return memoryStore.replaceTowerLegs(projectId, nodeId, legs);
    }

    public FoundationLegRuleConfig getLegRuleConfig(String projectId) {
        if (useDatabase()) {
            var row = foundationLegRuleMapper.selectLegRule(projectId);
            return row == null
                    ? new FoundationLegRuleConfig(6.0, 0.3, 0.5, 0.5, 3.0, 0)
                    : new FoundationLegRuleConfig(
                            row.getExtractHeight(),
                            row.getHeightAllow(),
                            row.getHeightStep(),
                            row.getMinLzkt(),
                            row.getMaxLzkt(),
                            row.getDado()
                    );
        }
        return memoryStore.getLegRuleConfig(projectId);
    }

    @Transactional
    public FoundationLegRuleConfig updateLegRuleConfig(String projectId, FoundationLegRuleUpsertRequest request) {
        if (useDatabase()) {
            String ruleId = UUID.randomUUID().toString().replace("-", "").toUpperCase();
            var existing = foundationLegRuleMapper.selectLegRule(projectId);
            if (existing != null && existing.getRuleId() != null && !existing.getRuleId().isBlank()) {
                ruleId = existing.getRuleId();
            }
            foundationLegRuleMapper.replaceLegRule(
                    ruleId,
                    projectId,
                    request.extractHeight(),
                    request.heightAllow(),
                    request.heightStep(),
                    request.minLzkt(),
                    request.maxLzkt(),
                    request.dado()
            );
            return getLegRuleConfig(projectId);
        }
        return memoryStore.updateLegRuleConfig(projectId, request);
    }

    @Transactional
    public List<FoundationTowerLeg> autoFillTowerLegs(String projectId, String nodeId) {
        FoundationLegRuleConfig rule = getLegRuleConfig(projectId);
        FoundationTowerLegFillContext context = getLegFillContext(projectId, nodeId);

        double extractHeight = rule.extractHeight() != null ? rule.extractHeight() : 6.0;
        double heightAllow = rule.heightAllow() != null ? rule.heightAllow() : 0.3;
        double heightStep = rule.heightStep() != null ? rule.heightStep() : 0.5;
        double minLzkt = rule.minLzkt() != null ? rule.minLzkt() : 0.5;
        double maxLzkt = rule.maxLzkt() != null ? rule.maxLzkt() : 3.0;
        double dadoFactor = rule.dado() != null ? rule.dado() * 0.05 : 0.0;
        String reduce = context.towerBodyId() != null && !context.towerBodyId().isBlank()
                ? context.towerBodyId()
                : context.towerType();

        List<FoundationTowerLeg> legs = new ArrayList<>();
        String[] legNames = {"A", "B", "C", "D"};
        for (int index = 0; index < legNames.length; index++) {
            double length = extractHeight + (index * heightStep);
            double exposed = Math.min(maxLzkt, minLzkt + (index * heightAllow));
            double topElevation = context.baseTopElevation() + context.positionAdjust() + dadoFactor + (index * 0.1);
            double slope = 1.0 + (index * 0.02);
            legs.add(new FoundationTowerLeg(
                    legNames[index],
                    reduce,
                    round(length),
                    round(exposed),
                    round(topElevation),
                    round(slope),
                    "Auto rule fill"
            ));
        }

        List<FoundationTowerLeg> saved = replaceTowerLegs(projectId, nodeId, legs);
        addImportHistory(projectId, "auto-leg-fill", "success", nodeId, "Auto-filled four legs from project rule");
        return saved;
    }

    private FoundationTowerLegFillContext getLegFillContext(String projectId, String nodeId) {
        if (!useDatabase()) {
            return memoryStore.getLegFillContext(projectId, nodeId);
        }
        FoundationTowerAssignmentRow assignment = foundationTowerMapper.selectTowerAssignment(nodeId);
        FoundationProjectNodeRow nodeRow = findProjectNodeRow(projectId, nodeId);
        String towerType = assignment != null && assignment.getTowerType() != null && !assignment.getTowerType().isBlank()
                ? assignment.getTowerType()
                : nodeRow.getTowerType();
        return new FoundationTowerLegFillContext(
                defaultTowerType(towerType),
                assignment != null ? normalizeBlank(assignment.getTowerBodyId()) : null,
                assignment != null && assignment.getPosHeightAdjust() != null ? assignment.getPosHeightAdjust() : 0.0,
                nodeRow.getPosHeight() != null ? nodeRow.getPosHeight() : 100.0
        );
    }

    private FoundationProjectSummary toSummary(FoundationProjectSummaryRow row) {
        return new FoundationProjectSummary(
                row.getProjectId(),
                row.getProjectName(),
                row.getProjectCode(),
                row.getStartAddress(),
                row.getEndAddress(),
                row.getNodeCount() == null ? 0 : row.getNodeCount()
        );
    }

    private FoundationProjectNode toProjectNode(FoundationProjectNodeRow row) {
        return new FoundationProjectNode(
                row.getNodeId(),
                row.getTowerNo(),
                row.getStake(),
                normalizeBlank(row.getSurveyType()),
                normalizeBlank(row.getTowerType()),
                deriveSceneStatus(row.getSurveyType())
        );
    }

    private FoundationTerrainResource toTerrainResource(FoundationProjectNodeRow row) {
        FoundationSurveyPointDocument document = getSurveyPoints(row.getProjectId(), row.getNodeId());
        return new FoundationTerrainResource(
                row.getProjectId(),
                row.getNodeId(),
                row.getTowerNo(),
                row.getStake(),
                normalizeBlank(row.getSurveyType()),
                document.lines(),
                Boolean.TRUE.equals(row.getHasTifBlob()),
                deriveSceneStatus(row.getSurveyType())
        );
    }

    private FoundationTowerLeg toTowerLeg(FoundationTowerLegRow row) {
        return new FoundationTowerLeg(
                row.getLegName(),
                normalizeBlank(row.getLegReduce()),
                row.getLegLength() == null ? 0.0 : row.getLegLength(),
                row.getExposedHeight() == null ? 0.0 : row.getExposedHeight(),
                row.getTopElevation() == null ? 0.0 : row.getTopElevation(),
                row.getSlope() == null ? 0.0 : row.getSlope(),
                normalizeBlank(row.getMark())
        );
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String normalizeBlank(String value) {
        return blankToNull(value);
    }

    private String deriveSceneStatus(String surveyType) {
        return surveyType == null || surveyType.isBlank() ? "planned" : "ready";
    }

    private String deriveTowerSceneStatus(FoundationTowerAssignmentRow row) {
        return row.getTowerBodyId() == null || row.getTowerBodyId().isBlank() ? "planned" : "ready";
    }

    private String defaultTowerType(String towerType) {
        return towerType == null || towerType.isBlank() ? "UNKNOWN" : towerType;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private void addImportHistory(String projectId, String eventType, String status, String target, String message) {
        memoryStore.addImportHistory(projectId, eventType, status, target, message);
    }

    private String toSurveyLine(FoundationSurveyPointRow row) {
        return row.getPosX() + "," + row.getPosY() + "," + row.getPosZ();
    }

    private boolean useDatabase() {
        return persistenceMode.isDatabase();
    }

    private String ensureSurveyId(String nodeId, String surveyType) {
        String surveyId = foundationSurveyMapper.selectSurveyIdByNodeId(nodeId);
        if (surveyId == null || surveyId.isBlank()) {
            surveyId = UUID.randomUUID().toString().replace("-", "").toUpperCase();
            foundationSurveyMapper.insertSurveyHeader(surveyId, nodeId, blankToNull(surveyType), "Created from BS");
        }
        return surveyId;
    }

    private double[] parseSurveyLine(String line) {
        String[] values = line.split(",");
        double x = values.length > 0 ? parseDouble(values[0]) : 0.0;
        double y = values.length > 1 ? parseDouble(values[1]) : 0.0;
        double z = values.length > 2 ? parseDouble(values[2]) : 0.0;
        return new double[] {x, y, z};
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    private String findTowerType(String projectId, String nodeId) {
        FoundationTowerAssignmentRow assignment = foundationTowerMapper.selectTowerAssignment(nodeId);
        if (assignment != null && assignment.getTowerType() != null && !assignment.getTowerType().isBlank()) {
            return assignment.getTowerType();
        }
        return findProjectNodeRow(projectId, nodeId).getTowerType();
    }

    private FoundationProjectNodeRow findProjectNodeRow(String projectId, String nodeId) {
        return foundationProjectMapper.selectProjectNodes(projectId).stream()
                .filter(item -> item.getNodeId().equals(nodeId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Node was not found: " + nodeId));
    }

}

