package com.powergrid.foundation.core;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.powergrid.foundation.core.project.FoundationProjectDetail;
import com.powergrid.foundation.core.project.FoundationProjectService;

@Service
public class FoundationStatusService {
    private final FoundationPersistenceMode persistenceMode;
    private final FoundationProjectService foundationProjectService;

    public FoundationStatusService(
            @Value("${foundation.persistence.mode:db}") String persistenceMode,
            FoundationProjectService foundationProjectService
    ) {
        this.persistenceMode = FoundationPersistenceMode.from(persistenceMode);
        this.foundationProjectService = foundationProjectService;
    }

    public FoundationStatusSnapshot snapshot() {
        return new FoundationStatusSnapshot(
                "foundation",
                "0.1.0",
                "Vue2 + Element UI",
                "Spring Boot 3 + MyBatis",
                "Three.js",
                "renovation-in-progress",
                persistenceMode.value(),
                listDescriptors()
        );
    }

    public List<FoundationModuleDescriptor> listDescriptors() {
        return List.of(
                new FoundationModuleDescriptor("project", "active", "database-backed-project-and-node-management"),
                new FoundationModuleDescriptor("data-prep", "active", "survey-import-and-database-write-chain"),
                new FoundationModuleDescriptor("tower", "active", "tower-body-and-leg-database-write-chain"),
                new FoundationModuleDescriptor("calc", "active", "schema-driven-calculation-and-export"),
                new FoundationModuleDescriptor("scene", "active", "threejs-scene-payload")
        );
    }

    public FoundationSmokeSnapshot smoke() {
        List<FoundationProjectDetail> projects = foundationProjectService.listProjects().stream()
                .map(project -> foundationProjectService.getProject(project.projectId()))
                .toList();
        int nodeCount = projects.stream()
                .mapToInt(project -> project.nodes().size())
                .sum();
        int readySceneCount = (int) projects.stream()
                .flatMap(project -> project.nodes().stream())
                .filter(node -> "ready".equalsIgnoreCase(node.sceneStatus()))
                .count();
        int blockedSceneCount = (int) projects.stream()
                .flatMap(project -> project.nodes().stream())
                .filter(node -> "blocked".equalsIgnoreCase(node.sceneStatus()))
                .count();

        return new FoundationSmokeSnapshot(
                persistenceMode.value(),
                projects.size(),
                nodeCount,
                readySceneCount,
                blockedSceneCount,
                List.of(
                        "Smoke snapshot is computed from live project and node queries.",
                        "Use this endpoint after changing datasource settings to confirm the BS chain still reads the legacy MySQL schema."
                )
        );
    }
}
