package com.powergrid.foundation.core.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.powergrid.foundation.core.project.FoundationProjectDetail;
import com.powergrid.foundation.core.project.FoundationProjectNode;
import com.powergrid.foundation.core.project.FoundationProjectService;
import com.powergrid.foundation.core.project.FoundationSurveyPointDocument;
import com.powergrid.foundation.core.project.FoundationTerrainResource;
import com.powergrid.foundation.core.project.FoundationTowerLeg;

@Service
public class FoundationSceneService {
    private final FoundationProjectService foundationProjectService;

    public FoundationSceneService(FoundationProjectService foundationProjectService) {
        this.foundationProjectService = foundationProjectService;
    }

    public List<FoundationSceneSummary> listScenes(String projectId) {
        FoundationProjectDetail detail = foundationProjectService.getProject(projectId);
        List<FoundationTerrainResource> terrainResources = foundationProjectService.listTerrainResources(projectId);
        return detail.nodes().stream()
                .map(node -> {
                    FoundationTerrainResource resource = findTerrainResource(terrainResources, node.nodeId());
                    FoundationSurveyPointDocument survey = foundationProjectService.getSurveyPoints(projectId, node.nodeId());
                    List<FoundationTowerLeg> legs = foundationProjectService.listTowerLegs(projectId, node.nodeId());
                    String terrainSource = terrainSource(resource, survey);
                    int terrainPointCount = buildTerrainPoints(survey).size();
                    boolean launchable = !"blocked".equalsIgnoreCase(node.sceneStatus())
                            && !"missing".equals(terrainSource)
                            && !legs.isEmpty();
                    return new FoundationSceneSummary(
                            node.nodeId(),
                            node.towerNo(),
                            node.stake(),
                            node.surveyType(),
                            node.sceneStatus(),
                            terrainSource,
                            launchable,
                            terrainPointCount,
                            legs.size()
                    );
                })
                .toList();
    }

    public FoundationScenePayload loadScene(String projectId, String nodeId) {
        FoundationProjectDetail detail = foundationProjectService.getProject(projectId);
        FoundationProjectNode node = detail.nodes().stream()
                .filter(item -> item.nodeId().equals(nodeId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Node was not found: " + nodeId));
        FoundationTerrainResource resource = findTerrainResource(foundationProjectService.listTerrainResources(projectId), nodeId);
        FoundationSurveyPointDocument survey = foundationProjectService.getSurveyPoints(projectId, nodeId);
        List<FoundationTowerLeg> legs = foundationProjectService.listTowerLegs(projectId, nodeId);

        List<FoundationScenePoint> terrain = buildTerrainPoints(survey);
        List<FoundationScenePoint> legPoints = buildLegPoints(legs);
        String terrainSource = terrainSource(resource, survey);
        FoundationScenePoint towerPoint = new FoundationScenePoint(0, 0, terrain.isEmpty() ? 0 : terrain.get(0).z(), "Tower");

        List<String> blockers = new ArrayList<>();
        if ("blocked".equalsIgnoreCase(node.sceneStatus())) {
            blockers.add("Node scene status is blocked.");
        }
        if ("missing".equals(terrainSource)) {
            blockers.add("No terrain source is available for this node.");
        }
        if (legs.isEmpty()) {
            blockers.add("Tower legs are not configured.");
        }

        List<FoundationSceneLayer> layers = buildSceneLayers(terrain, towerPoint, legPoints);

        return new FoundationScenePayload(
                projectId,
                nodeId,
                node.towerNo(),
                node.stake(),
                node.surveyType(),
                node.sceneStatus(),
                terrainSource,
                blockers.isEmpty(),
                terrain.size(),
                legPoints.size(),
                blockers.isEmpty() ? "isometric" : "inspection",
                new FoundationScenePoint(18, 18, 18, "Camera"),
                new FoundationScenePoint(0, 0, 0, "Target"),
                terrain,
                towerPoint,
                legPoints,
                layers,
                blockers
        );
    }

    private List<FoundationScenePoint> buildTerrainPoints(FoundationSurveyPointDocument survey) {
        List<FoundationScenePoint> terrain = new ArrayList<>();
        if (survey.lines().isEmpty()) {
            terrain.add(new FoundationScenePoint(-12, -12, 0, "P1"));
            terrain.add(new FoundationScenePoint(12, -12, 0.5, "P2"));
            terrain.add(new FoundationScenePoint(12, 12, 0.2, "P3"));
            terrain.add(new FoundationScenePoint(-12, 12, -0.1, "P4"));
            return terrain;
        }

        for (int index = 0; index < survey.lines().size(); index++) {
            String[] values = survey.lines().get(index).split(",");
            double x = values.length > 0 ? parse(values[0]) : 0.0;
            double y = values.length > 1 ? parse(values[1]) : 0.0;
            double z = values.length > 2 ? parse(values[2]) : 0.0;
            terrain.add(new FoundationScenePoint(x, y, z, "PT-" + (index + 1)));
        }
        return terrain;
    }

    private List<FoundationScenePoint> buildLegPoints(List<FoundationTowerLeg> legs) {
        List<FoundationScenePoint> legPoints = new ArrayList<>();
        double[][] coords = {
                {-4, -4}, {4, -4}, {4, 4}, {-4, 4}
        };
        for (int index = 0; index < legs.size() && index < coords.length; index++) {
            FoundationTowerLeg leg = legs.get(index);
            legPoints.add(new FoundationScenePoint(
                    coords[index][0],
                    coords[index][1],
                    leg.topElevation(),
                    leg.leg()
            ));
        }
        return legPoints;
    }

    private List<FoundationSceneLayer> buildSceneLayers(
            List<FoundationScenePoint> terrain,
            FoundationScenePoint towerPoint,
            List<FoundationScenePoint> legPoints
    ) {
        List<FoundationSceneLayer> layers = new ArrayList<>();
        layers.add(new FoundationSceneLayer(
                "terrain",
                "Terrain points",
                "points",
                "#8bbf7a",
                terrain
        ));
        layers.add(new FoundationSceneLayer(
                "tower",
                "Tower body",
                "marker",
                "#3a6ea5",
                List.of(towerPoint)
        ));
        layers.add(new FoundationSceneLayer(
                "legs",
                "Tower legs",
                "points",
                "#a95d2d",
                legPoints
        ));
        layers.add(new FoundationSceneLayer(
                "helpers",
                "Tower footprint",
                "polyline",
                "#9ca3af",
                List.of(
                        new FoundationScenePoint(-4, -4, 0, "A"),
                        new FoundationScenePoint(4, -4, 0, "B"),
                        new FoundationScenePoint(4, 4, 0, "C"),
                        new FoundationScenePoint(-4, 4, 0, "D"),
                        new FoundationScenePoint(-4, -4, 0, "A")
                )
        ));
        return layers;
    }

    private FoundationTerrainResource findTerrainResource(List<FoundationTerrainResource> resources, String nodeId) {
        return resources.stream()
                .filter(item -> item.nodeId().equals(nodeId))
                .findFirst()
                .orElse(new FoundationTerrainResource(null, nodeId, null, null, null, List.of(), false, "planned"));
    }

    private String terrainSource(FoundationTerrainResource resource, FoundationSurveyPointDocument survey) {
        if (!survey.lines().isEmpty()) {
            return "survey-points";
        }
        if (resource.hasTifBlob()) {
            return "tif-blob";
        }
        if (resource.surveyType() != null && !resource.surveyType().isBlank()) {
            return "fallback-terrain";
        }
        return "missing";
    }

    private double parse(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }
}
