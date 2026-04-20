package com.powergrid.foundation.core.project;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

final class FoundationProjectMemoryStore {
    private final Map<String, ProjectState> projects = new LinkedHashMap<>();
    private final Map<String, List<FoundationImportHistoryEntry>> importHistory = new LinkedHashMap<>();
    private final Map<String, FoundationLegRuleConfig> legRules = new LinkedHashMap<>();

    FoundationProjectMemoryStore(boolean seedDemoData) {
        if (seedDemoData) {
            seed();
        }
    }

    Map<String, ProjectState> projects() {
        return projects;
    }

    Map<String, FoundationLegRuleConfig> legRules() {
        return legRules;
    }

    List<FoundationImportHistoryEntry> importHistory(String projectId) {
        List<FoundationImportHistoryEntry> entries = importHistory.get(projectId);
        if (entries == null) {
            return List.of();
        }
        return List.copyOf(entries);
    }

    void addImportHistory(String projectId, String eventType, String status, String target, String message) {
        List<FoundationImportHistoryEntry> entries = importHistory.computeIfAbsent(projectId, ignored -> new ArrayList<>());
        entries.add(0, new FoundationImportHistoryEntry(
                OffsetDateTime.now().toString(),
                eventType,
                status,
                target,
                message
        ));
        if (entries.size() > 20) {
            entries.remove(entries.size() - 1);
        }
    }

    ProjectState requireProject(String projectId) {
        ProjectState state = projects.get(projectId);
        if (state == null) {
            throw new NoSuchElementException("Project was not found: " + projectId);
        }
        return state;
    }

    NodeState requireNode(ProjectState state, String nodeId) {
        return state.nodes.stream()
                .filter(item -> item.nodeId.equals(nodeId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Node was not found: " + nodeId));
    }

    List<FoundationProjectSummary> listProjectSummaries() {
        return projects.values().stream()
                .sorted((left, right) -> left.projectName().compareTo(right.projectName()))
                .map(this::toSummary)
                .toList();
    }

    FoundationProjectDetail getProjectDetail(String projectId) {
        return toDetail(requireProject(projectId));
    }

    FoundationProjectDetail createProject(String projectId, FoundationProjectUpsertRequest request) {
        ProjectState state = new ProjectState(
                projectId,
                request.getProjectName(),
                request.getProjectCode(),
                blankToNull(request.getStartAddress()),
                blankToNull(request.getEndAddress()),
                blankToNull(request.getRemarks()),
                new ArrayList<>()
        );
        projects.put(projectId, state);
        return toDetail(state);
    }

    FoundationProjectDetail updateProject(String projectId, FoundationProjectUpsertRequest request) {
        ProjectState state = requireProject(projectId);
        state.projectName = request.getProjectName();
        state.projectCode = request.getProjectCode();
        state.startAddress = blankToNull(request.getStartAddress());
        state.endAddress = blankToNull(request.getEndAddress());
        state.remarks = blankToNull(request.getRemarks());
        return toDetail(state);
    }

    void deleteProject(String projectId) {
        if (projects.remove(projectId) == null) {
            throw new NoSuchElementException("Project was not found: " + projectId);
        }
    }

    FoundationNodeDetail getNodeDetail(String projectId, String nodeId) {
        ProjectState state = requireProject(projectId);
        NodeState node = requireNode(state, nodeId);
        return new FoundationNodeDetail(
                state.projectId,
                node.nodeId,
                node.towerNo,
                node.stake,
                node.surveyType,
                node.towerType,
                node.sceneStatus,
                node.positionDescription
        );
    }

    FoundationProjectDetail createNode(String projectId, FoundationProjectNodeUpsertRequest request) {
        ProjectState state = requireProject(projectId);
        state.nodes.add(new NodeState(
                "NODE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                request.towerNo().trim(),
                request.stake().trim(),
                null,
                "UNKNOWN",
                "planned",
                "Created from BS",
                List.of(),
                false,
                null,
                null,
                List.of()
        ));
        return toDetail(state);
    }

    FoundationProjectDetail updateNode(String projectId, String nodeId, FoundationProjectNodeUpsertRequest request) {
        ProjectState state = requireProject(projectId);
        NodeState node = requireNode(state, nodeId);
        node.towerNo = request.towerNo().trim();
        node.stake = request.stake().trim();
        return toDetail(state);
    }

    void deleteNode(String projectId, String nodeId) {
        ProjectState state = requireProject(projectId);
        boolean removed = state.nodes.removeIf(item -> item.nodeId.equals(nodeId));
        if (!removed) {
            throw new NoSuchElementException("Node was not found: " + nodeId);
        }
    }

    List<FoundationTerrainResource> listTerrainResources(String projectId) {
        ProjectState state = requireProject(projectId);
        return state.nodes.stream()
                .map(node -> toTerrainResource(state.projectId, node))
                .toList();
    }

    FoundationSurveyPointDocument getSurveyPoints(String projectId, String nodeId) {
        ProjectState state = requireProject(projectId);
        NodeState node = requireNode(state, nodeId);
        return new FoundationSurveyPointDocument(
                state.projectId,
                node.nodeId,
                node.surveyType,
                node.surveyPoints
        );
    }

    FoundationTerrainResource updateSurveyType(String projectId, String nodeId, String surveyType) {
        ProjectState state = requireProject(projectId);
        NodeState node = requireNode(state, nodeId);
        node.surveyType = blankToNull(surveyType);
        addImportHistory(projectId, "survey-type", "success", nodeId, "Survey type updated");
        return toTerrainResource(projectId, node);
    }

    FoundationSurveyPointDocument replaceSurveyPoints(String projectId, String nodeId, String surveyType, List<String> surveyPoints) {
        ProjectState state = requireProject(projectId);
        NodeState node = requireNode(state, nodeId);
        node.surveyType = blankToNull(surveyType);
        node.surveyPoints = surveyPoints.stream()
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .toList();
        node.sceneStatus = node.surveyPoints.isEmpty() ? "planned" : "ready";
        addImportHistory(projectId, "survey-points", "success", nodeId, "Survey points replaced");
        return new FoundationSurveyPointDocument(
                state.projectId,
                node.nodeId,
                node.surveyType,
                node.surveyPoints
        );
    }

    FoundationSurveyBatchImportResult importSurveyBatch(String projectId, List<FoundationSurveyBatchImportItem> items) {
        ProjectState state = requireProject(projectId);
        int importedCount = 0;
        int skippedCount = 0;
        List<String> messages = new ArrayList<>();

        for (FoundationSurveyBatchImportItem item : items) {
            NodeState node = state.nodes.stream()
                    .filter(candidate -> candidate.nodeId.equals(item.nodeId()))
                    .findFirst()
                    .orElse(null);
            if (node == null) {
                skippedCount++;
                messages.add("Skipped " + item.sourceName() + ": node was not found.");
                continue;
            }
            node.surveyType = blankToNull(item.surveyType());
            node.surveyPoints = item.lines().stream()
                    .map(String::trim)
                    .filter(line -> !line.isBlank())
                    .toList();
            node.sceneStatus = node.surveyPoints.isEmpty() ? "planned" : "ready";
            importedCount++;
            messages.add("Imported " + item.sourceName() + " -> " + node.towerNo + " / " + node.stake);
            addImportHistory(projectId, "survey-batch", "success", node.nodeId, "Imported " + item.sourceName());
        }

        return new FoundationSurveyBatchImportResult(importedCount, skippedCount, messages);
    }

    FoundationTerrainResource uploadNodeTif(String projectId, String nodeId, String surveyType) {
        ProjectState state = requireProject(projectId);
        NodeState node = requireNode(state, nodeId);
        node.surveyType = blankToNull(surveyType);
        node.hasTifBlob = true;
        if (node.sceneStatus == null || node.sceneStatus.isBlank() || "planned".equalsIgnoreCase(node.sceneStatus)) {
            node.sceneStatus = "ready";
        }
        addImportHistory(projectId, "tif-upload", "success", nodeId, "Uploaded tif content");
        return toTerrainResource(projectId, node);
    }

    FoundationTerrainResource clearNodeTif(String projectId, String nodeId) {
        ProjectState state = requireProject(projectId);
        NodeState node = requireNode(state, nodeId);
        node.hasTifBlob = false;
        addImportHistory(projectId, "tif-clear", "success", nodeId, "Cleared tif content");
        return toTerrainResource(projectId, node);
    }


    List<FoundationTowerLibraryItem> listTowerLibrary(String projectId) {
        ProjectState state = requireProject(projectId);
        Map<String, List<NodeState>> grouped = new LinkedHashMap<>();
        for (NodeState node : state.nodes) {
            grouped.computeIfAbsent(defaultTowerType(node.towerType), ignored -> new ArrayList<>()).add(node);
        }
        return grouped.entrySet().stream()
                .map(entry -> {
                    List<NodeState> nodes = entry.getValue();
                    long configured = nodes.stream()
                            .filter(item -> item.towerBodyId != null && !item.towerBodyId.isBlank())
                            .count();
                    return new FoundationTowerLibraryItem(
                            entry.getKey(),
                            nodes.size(),
                            (int) configured,
                            configured == nodes.size()
                    );
                })
                .toList();
    }

    List<FoundationTowerAssignmentItem> listTowerAssignments(String projectId, String towerType) {
        ProjectState state = requireProject(projectId);
        return state.nodes.stream()
                .filter(node -> defaultTowerType(node.towerType).equals(towerType))
                .map(node -> new FoundationTowerAssignmentItem(
                        node.nodeId,
                        node.towerNo,
                        node.stake,
                        node.towerBodyId,
                        node.positionAdjust,
                        node.legs.size(),
                        node.sceneStatus
                ))
                .toList();
    }

    FoundationTowerAssignmentItem updateTowerAssignment(String projectId, String nodeId, String towerBodyId, Double positionAdjust) {
        ProjectState state = requireProject(projectId);
        NodeState node = requireNode(state, nodeId);
        node.towerBodyId = blankToNull(towerBodyId);
        node.positionAdjust = positionAdjust;
        return new FoundationTowerAssignmentItem(
                node.nodeId,
                node.towerNo,
                node.stake,
                node.towerBodyId,
                node.positionAdjust,
                node.legs.size(),
                node.sceneStatus
        );
    }

    FoundationTowerBodySuggestion getDefaultTowerBodySuggestion(String towerType) {
        String normalizedTowerType = defaultTowerType(towerType);
        return new FoundationTowerBodySuggestion(normalizedTowerType, "BODY-" + normalizedTowerType, true);
    }

    List<FoundationTowerLeg> listTowerLegs(String projectId, String nodeId) {
        ProjectState state = requireProject(projectId);
        NodeState node = requireNode(state, nodeId);
        return node.legs;
    }

    List<FoundationTowerLeg> replaceTowerLegs(String projectId, String nodeId, List<FoundationTowerLeg> legs) {
        ProjectState state = requireProject(projectId);
        NodeState node = requireNode(state, nodeId);
        node.legs = legs;
        return node.legs;
    }

    FoundationLegRuleConfig getLegRuleConfig(String projectId) {
        return legRules.getOrDefault(projectId, new FoundationLegRuleConfig(6.0, 0.3, 0.5, 0.5, 3.0, 0));
    }

    FoundationLegRuleConfig updateLegRuleConfig(String projectId, FoundationLegRuleUpsertRequest request) {
        FoundationLegRuleConfig config = new FoundationLegRuleConfig(
                request.extractHeight(),
                request.heightAllow(),
                request.heightStep(),
                request.minLzkt(),
                request.maxLzkt(),
                request.dado()
        );
        legRules.put(projectId, config);
        return config;
    }

    FoundationTowerLegFillContext getLegFillContext(String projectId, String nodeId) {
        ProjectState state = requireProject(projectId);
        NodeState node = requireNode(state, nodeId);
        return new FoundationTowerLegFillContext(
                defaultTowerType(node.towerType),
                node.towerBodyId,
                node.positionAdjust != null ? node.positionAdjust : 0.0,
                100.0
        );
    }

    private void seed() {
        ProjectState northLine = new ProjectState(
                "PRJ-DEMO-001",
                "North line foundation review",
                "CF-001",
                "North line origin",
                "North line destination",
                "Desktop migration baseline sample",
                new ArrayList<>(List.of(
                        new NodeState(
                                "NODE-001",
                                "T001",
                                "K0+120",
                                "survey-section",
                                "ZM1",
                                "ready",
                                "Mountain node with complete survey points",
                                List.of("0,0,102.5", "12,8,103.1", "-8,6,101.7"),
                                false,
                                "BODY-ZM1-A",
                                0.10,
                                defaultLegs("A")
                        ),
                        new NodeState(
                                "NODE-002",
                                "T002",
                                "K0+360",
                                "lidar",
                                "ZM1",
                                "blocked",
                                "Missing tif terrain mesh",
                                List.of("0,0,98.2", "10,0,98.9"),
                                true,
                                null,
                                null,
                                defaultLegs("B")
                        )
                ))
        );
        ProjectState westLine = new ProjectState(
                "PRJ-DEMO-002",
                "West line tower leg tuning",
                "CF-002",
                "West line origin",
                "West line destination",
                "Tower-leg and 3D linkage sample",
                new ArrayList<>(List.of(
                        new NodeState(
                                "NODE-101",
                                "W101",
                                "K2+015",
                                "lidar",
                                "ZS2",
                                "ready",
                                "Lidar and leg config are ready",
                                List.of("0,0,126.5", "8,5,127.0", "-7,-3,125.8"),
                                true,
                                "BODY-ZS2-C",
                                -0.15,
                                defaultLegs("C")
                        ),
                        new NodeState(
                                "NODE-102",
                                "W102",
                                "K2+245",
                                "survey-section",
                                "ZS3",
                                "planned",
                                "Waiting for automatic leg fill rule",
                                List.of(),
                                false,
                                null,
                                null,
                                defaultLegs("D")
                        )
                ))
        );
        projects.put(northLine.projectId, northLine);
        projects.put(westLine.projectId, westLine);
    }

    private List<FoundationTowerLeg> defaultLegs(String suffix) {
        return List.of(
                new FoundationTowerLeg("A", "R-" + suffix, 5.2, 1.3, 102.5, 1.00, "Default leg A"),
                new FoundationTowerLeg("B", "R-" + suffix, 5.0, 1.2, 102.2, 1.05, "Default leg B"),
                new FoundationTowerLeg("C", "R-" + suffix, 4.9, 1.1, 102.0, 0.98, "Default leg C"),
                new FoundationTowerLeg("D", "R-" + suffix, 5.1, 1.4, 102.7, 1.02, "Default leg D")
        );
    }

    private FoundationProjectSummary toSummary(ProjectState state) {
        return new FoundationProjectSummary(
                state.projectId,
                state.projectName,
                state.projectCode,
                state.startAddress,
                state.endAddress,
                state.nodes.size()
        );
    }

    private FoundationProjectDetail toDetail(ProjectState state) {
        return new FoundationProjectDetail(
                state.projectId,
                state.projectName,
                state.projectCode,
                state.startAddress,
                state.endAddress,
                state.remarks,
                state.nodes.stream()
                        .map(node -> new FoundationProjectNode(
                                node.nodeId,
                                node.towerNo,
                                node.stake,
                                node.surveyType,
                                node.towerType,
                                node.sceneStatus
                        ))
                        .toList()
        );
    }

    private FoundationTerrainResource toTerrainResource(String projectId, NodeState node) {
        return new FoundationTerrainResource(
                projectId,
                node.nodeId,
                node.towerNo,
                node.stake,
                node.surveyType,
                node.surveyPoints,
                node.hasTifBlob,
                node.sceneStatus
        );
    }

    private String defaultTowerType(String towerType) {
        return towerType == null || towerType.isBlank() ? "UNKNOWN" : towerType;
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    static final class ProjectState {
        final String projectId;
        String projectName;
        String projectCode;
        String startAddress;
        String endAddress;
        String remarks;
        final List<NodeState> nodes;

        ProjectState(
                String projectId,
                String projectName,
                String projectCode,
                String startAddress,
                String endAddress,
                String remarks,
                List<NodeState> nodes
        ) {
            this.projectId = projectId;
            this.projectName = projectName;
            this.projectCode = projectCode;
            this.startAddress = startAddress;
            this.endAddress = endAddress;
            this.remarks = remarks;
            this.nodes = nodes;
        }

        String projectName() {
            return projectName;
        }
    }

    static final class NodeState {
        final String nodeId;
        String towerNo;
        String stake;
        String surveyType;
        final String towerType;
        String sceneStatus;
        final String positionDescription;
        List<String> surveyPoints;
        boolean hasTifBlob;
        String towerBodyId;
        Double positionAdjust;
        List<FoundationTowerLeg> legs;

        NodeState(
                String nodeId,
                String towerNo,
                String stake,
                String surveyType,
                String towerType,
                String sceneStatus,
                String positionDescription,
                List<String> surveyPoints,
                boolean hasTifBlob,
                String towerBodyId,
                Double positionAdjust,
                List<FoundationTowerLeg> legs
        ) {
            this.nodeId = nodeId;
            this.towerNo = towerNo;
            this.stake = stake;
            this.surveyType = surveyType;
            this.towerType = towerType;
            this.sceneStatus = sceneStatus;
            this.positionDescription = positionDescription;
            this.surveyPoints = surveyPoints;
            this.hasTifBlob = hasTifBlob;
            this.towerBodyId = towerBodyId;
            this.positionAdjust = positionAdjust;
            this.legs = legs;
        }
    }
}

