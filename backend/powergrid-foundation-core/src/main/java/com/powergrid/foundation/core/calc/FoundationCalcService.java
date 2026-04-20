package com.powergrid.foundation.core.calc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.powergrid.foundation.core.FoundationPersistenceMode;
import com.powergrid.foundation.core.persistence.mapper.FoundationCalcRecordMapper;
import com.powergrid.foundation.core.persistence.model.FoundationCalcRecordRow;

@Service
public class FoundationCalcService {
    private static final TypeReference<Map<String, String>> INPUT_VALUES_TYPE = new TypeReference<>() {
    };
    private static final TypeReference<List<FoundationCalcResultItem>> RESULT_ITEMS_TYPE = new TypeReference<>() {
    };
    private static final TypeReference<List<FoundationCalcGraphicLayer>> GRAPHICS_TYPE = new TypeReference<>() {
    };

    private final FoundationPersistenceMode persistenceMode;
    private final FoundationCalcRecordMapper foundationCalcRecordMapper;
    private final ObjectMapper objectMapper;
    private final Map<String, FoundationCalcRecordSummary> records = new LinkedHashMap<>();
    private final Map<String, FoundationCalcRecordDetail> recordDetails = new LinkedHashMap<>();

    public FoundationCalcService(
            @Value("${foundation.persistence.mode:db}") String persistenceMode,
            FoundationCalcRecordMapper foundationCalcRecordMapper,
            ObjectMapper objectMapper
    ) {
        this.persistenceMode = FoundationPersistenceMode.from(persistenceMode);
        this.foundationCalcRecordMapper = foundationCalcRecordMapper;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    void ensureSchema() {
        if (useDatabase()) {
            foundationCalcRecordMapper.ensureCalcRecordTable();
        }
    }

    public FoundationCalcSchema loadSchema(String foundationType, String operation, String iterationMode) {
        List<FoundationCalcField> fields = new ArrayList<>(List.of(
                new FoundationCalcField("b0", "Column width b0", "Core Geometry", "number", true, "1.0", false, List.of()),
                new FoundationCalcField("B", "Foundation width B", "Core Geometry", "number", true, "3.0", false, List.of()),
                new FoundationCalcField("z", "Buried depth z", "Core Geometry", "number", true, "2.5", false, List.of()),
                new FoundationCalcField("h0", "Exposed height h0", "Core Geometry", "number", true, "0.5", false, List.of()),
                new FoundationCalcField("h", "Total height h", "Core Geometry", "number", true, "2.5", false, List.of()),
                new FoundationCalcField("concreteGrade", "Concrete grade", "Load & Material", "select", true, "C35", false, List.of("C30", "C35", "C40")),
                new FoundationCalcField("rebarGrade", "Rebar grade", "Load & Material", "select", true, "HRB400", false, List.of("HRB400", "HRB500")),
                new FoundationCalcField("bearing", "Bearing capacity", "Soil & Boundary", "number", true, "180", false, List.of()),
                new FoundationCalcField("remark", "Remark", "Advanced", "text", false, "", true, List.of())
        ));
        if ("series".equalsIgnoreCase(iterationMode)) {
            fields.add(new FoundationCalcField("seriesValues", "Series h0 values", "Batch Settings", "text", true, "0.5,0.7,0.9", false, List.of()));
        }
        if ("pile".equalsIgnoreCase(foundationType)) {
            fields.add(new FoundationCalcField("pileLength", "Pile length", "Special", "number", true, "12", false, List.of()));
        } else {
            fields.add(new FoundationCalcField("slope", "Slope ratio", "Special", "number", false, "1.00", false, List.of()));
        }
        return new FoundationCalcSchema(foundationType, operation, iterationMode, fields);
    }

    public FoundationCalcTemplate loadTemplate(String foundationType) {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("b0", "1.0");
        values.put("B", "3.0");
        values.put("z", "2.5");
        values.put("h0", "0.5");
        values.put("h", "2.5");
        values.put("concreteGrade", "C35");
        values.put("rebarGrade", "HRB400");
        values.put("bearing", "180");
        if ("pile".equalsIgnoreCase(foundationType)) {
            values.put("pileLength", "12");
        } else {
            values.put("slope", "1.00");
        }
        return new FoundationCalcTemplate("Default template", values);
    }

    public FoundationCalcPreview preview(FoundationCalcExecuteRequest request) {
        FoundationCalcSchema schema = loadSchema(
                request.getFoundationType(),
                request.getOperation(),
                request.getIterationMode()
        );
        List<String> missing = schema.fields().stream()
                .filter(FoundationCalcField::required)
                .filter(field -> request.getValues() == null || blank(request.getValues().get(field.fieldKey())))
                .map(FoundationCalcField::caption)
                .toList();
        List<String> geometryHints = List.of(
                "2D preview is generated from b0, B, h0 and h.",
                "The special section changes with the selected foundation type.",
                "Series mode keeps one execution history record per node."
        );
        return new FoundationCalcPreview(
                request.getFoundationType(),
                request.getOperation(),
                request.getIterationMode(),
                missing,
                geometryHints,
                buildShapes(request)
        );
    }

    public FoundationCalcRecordSummary execute(FoundationCalcExecuteRequest request) {
        String recordId = "CALC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        List<FoundationCalcResultItem> items = new ArrayList<>();
        items.add(new FoundationCalcResultItem("Geometry", "Foundation type", request.getFoundationType()));
        items.add(new FoundationCalcResultItem("Geometry", "Foundation width B", valueOf(request, "B")));
        items.add(new FoundationCalcResultItem("Geometry", "Exposed height h0", valueOf(request, "h0")));
        items.add(new FoundationCalcResultItem("Bearing", "Bearing capacity", valueOf(request, "bearing")));
        items.add(new FoundationCalcResultItem("Material", "Concrete grade", valueOf(request, "concreteGrade")));
        items.add(new FoundationCalcResultItem("Material", "Rebar grade", valueOf(request, "rebarGrade")));
        if ("pile".equalsIgnoreCase(request.getFoundationType())) {
            items.add(new FoundationCalcResultItem("Pile", "Pile length", valueOf(request, "pileLength")));
        } else {
            items.add(new FoundationCalcResultItem("Special", "Slope ratio", valueOf(request, "slope")));
        }
        items.add(new FoundationCalcResultItem("Validation", "Execution mode", request.getOperation() + " / " + request.getIterationMode()));

        FoundationCalcRecordSummary summary = new FoundationCalcRecordSummary(
                recordId,
                request.getProjectId(),
                request.getNodeId(),
                request.getFoundationType(),
                request.getOperation(),
                request.getIterationMode(),
                request.getFoundationType() + " - " + request.getNodeId(),
                Instant.now(),
                items
        );
        records.put(recordId, summary);
        Map<String, String> inputValues = new LinkedHashMap<>();
        if (request.getValues() != null) {
            inputValues.putAll(request.getValues());
        }
        List<FoundationCalcResultItem> derivedItems = buildDerivedItems(request);
        List<FoundationCalcGraphicLayer> graphics = buildGraphics(request);
        recordDetails.put(recordId, new FoundationCalcRecordDetail(
                summary,
                inputValues,
                derivedItems,
                graphics,
                buildJson(summary)
        ));
        if (useDatabase()) {
            foundationCalcRecordMapper.insertCalcRecord(toRow(summary, inputValues, derivedItems, graphics));
        }
        return summary;
    }

    public FoundationCalcBatchResult executeBatch(FoundationCalcBatchExecuteRequest request) {
        List<FoundationCalcRecordSummary> summaries = new ArrayList<>();
        List<String> messages = new ArrayList<>();
        for (String nodeId : request.getNodeIds()) {
            if (nodeId == null || nodeId.isBlank()) {
                messages.add("Skipped blank node id.");
                continue;
            }
            FoundationCalcExecuteRequest singleRequest = new FoundationCalcExecuteRequest();
            singleRequest.setProjectId(request.getProjectId());
            singleRequest.setNodeId(nodeId.trim());
            singleRequest.setFoundationType(request.getFoundationType());
            singleRequest.setOperation(request.getOperation());
            singleRequest.setIterationMode(request.getIterationMode());
            singleRequest.setValues(request.getValues());
            try {
                summaries.add(execute(singleRequest));
            } catch (RuntimeException ex) {
                messages.add("Failed node " + nodeId.trim() + ": " + ex.getMessage());
            }
        }
        int total = request.getNodeIds().size();
        int success = summaries.size();
        return new FoundationCalcBatchResult(
                request.getProjectId(),
                total,
                success,
                total - success,
                summaries,
                messages
        );
    }

    public List<FoundationCalcRecordSummary> listRecords(String projectId, String nodeId) {
        if (useDatabase()) {
            return foundationCalcRecordMapper.selectCalcRecords(projectId, nodeId).stream()
                    .map(this::toSummary)
                    .toList();
        }
        return records.values().stream()
                .filter(item -> item.projectId().equals(projectId) && item.nodeId().equals(nodeId))
                .sorted((left, right) -> right.createdAt().compareTo(left.createdAt()))
                .toList();
    }

    public FoundationCalcRecordSummary getRecord(String recordId) {
        if (useDatabase()) {
            FoundationCalcRecordRow row = foundationCalcRecordMapper.selectCalcRecord(recordId);
            if (row == null) {
                throw new NoSuchElementException("Calculation record was not found: " + recordId);
            }
            return toSummary(row);
        }
        FoundationCalcRecordSummary record = records.get(recordId);
        if (record == null) {
            throw new NoSuchElementException("Calculation record was not found: " + recordId);
        }
        return record;
    }

    public FoundationCalcRecordDetail getRecordDetail(String recordId) {
        if (useDatabase()) {
            FoundationCalcRecordRow row = foundationCalcRecordMapper.selectCalcRecord(recordId);
            if (row == null) {
                throw new NoSuchElementException("Calculation record was not found: " + recordId);
            }
            return toDetail(row);
        }
        FoundationCalcRecordDetail detail = recordDetails.get(recordId);
        if (detail == null) {
            FoundationCalcRecordSummary summary = getRecord(recordId);
            return new FoundationCalcRecordDetail(summary, Map.of(), List.of(), List.of(), buildJson(summary));
        }
        return detail;
    }

    public FoundationCalcExportDocument exportRecord(String recordId, String format) {
        FoundationCalcRecordDetail detail = getRecordDetail(recordId);
        FoundationCalcRecordSummary record = detail.summary();
        String normalizedFormat = format == null ? "json" : format.trim().toLowerCase();
        if ("csv".equals(normalizedFormat)) {
            return new FoundationCalcExportDocument(
                    "csv",
                    record.recordId() + ".csv",
                    "text/csv",
                    buildCsv(record)
            );
        }
        return new FoundationCalcExportDocument(
                "json",
                record.recordId() + ".json",
                "application/json",
                detail.rawJson()
        );
    }

    private FoundationCalcRecordRow toRow(
            FoundationCalcRecordSummary summary,
            Map<String, String> inputValues,
            List<FoundationCalcResultItem> derivedItems,
            List<FoundationCalcGraphicLayer> graphics
    ) {
        FoundationCalcRecordRow row = new FoundationCalcRecordRow();
        row.setRecordId(summary.recordId());
        row.setProjectId(summary.projectId());
        row.setNodeId(summary.nodeId());
        row.setFoundationType(summary.foundationType());
        row.setOperation(summary.operation());
        row.setIterationMode(summary.iterationMode());
        row.setTitle(summary.title());
        row.setCreatedAt(LocalDateTime.ofInstant(summary.createdAt(), ZoneId.systemDefault()));
        row.setInputValuesJson(writeJson(inputValues));
        row.setResultItemsJson(writeJson(summary.items()));
        row.setDerivedItemsJson(writeJson(derivedItems));
        row.setGraphicsJson(writeJson(graphics));
        row.setExportJson(buildJson(summary));
        return row;
    }

    private FoundationCalcRecordSummary toSummary(FoundationCalcRecordRow row) {
        return new FoundationCalcRecordSummary(
                row.getRecordId(),
                row.getProjectId(),
                row.getNodeId(),
                row.getFoundationType(),
                row.getOperation(),
                row.getIterationMode(),
                row.getTitle(),
                row.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant(),
                readJson(row.getResultItemsJson(), RESULT_ITEMS_TYPE)
        );
    }

    private FoundationCalcRecordDetail toDetail(FoundationCalcRecordRow row) {
        return new FoundationCalcRecordDetail(
                toSummary(row),
                readJson(row.getInputValuesJson(), INPUT_VALUES_TYPE),
                readJson(row.getDerivedItemsJson(), RESULT_ITEMS_TYPE),
                readJson(row.getGraphicsJson(), GRAPHICS_TYPE),
                row.getExportJson()
        );
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize calculation record", ex);
        }
    }

    private <T> T readJson(String json, TypeReference<T> type) {
        try {
            return objectMapper.readValue(json == null || json.isBlank() ? "{}" : json, type);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to deserialize calculation record", ex);
        }
    }

    private List<FoundationCalcPreviewShape> buildShapes(FoundationCalcExecuteRequest request) {
        double foundationWidth = numericValue(request, "B", 3.0);
        double columnWidth = numericValue(request, "b0", 1.0);
        double exposedHeight = numericValue(request, "h0", 0.5);
        double foundationHeight = numericValue(request, "h", 2.5);
        double slope = numericValue(request, "slope", 1.0);
        double pileLength = numericValue(request, "pileLength", 12.0) / 6.0;
        double columnLeft = Math.max((foundationWidth - columnWidth) / 2.0, 0.0);
        double columnRight = columnLeft + columnWidth;
        double bottom = exposedHeight + foundationHeight;

        List<FoundationCalcPreviewShape> shapes = new ArrayList<>();
        shapes.add(new FoundationCalcPreviewShape(
                "ground",
                "polyline",
                "#8c8c8c",
                "none",
                List.of(
                        new FoundationCalcPoint(-0.8, 0.0),
                        new FoundationCalcPoint(foundationWidth + 0.8, 0.0)
                )
        ));
        shapes.add(new FoundationCalcPreviewShape(
                "column",
                "polygon",
                "#1f4e79",
                "rgba(74, 144, 226, 0.25)",
                List.of(
                        new FoundationCalcPoint(columnLeft, 0.0),
                        new FoundationCalcPoint(columnRight, 0.0),
                        new FoundationCalcPoint(columnRight, exposedHeight),
                        new FoundationCalcPoint(columnLeft, exposedHeight)
                )
        ));
        shapes.add(new FoundationCalcPreviewShape(
                "footing",
                "polygon",
                "#226d45",
                "rgba(87, 171, 90, 0.28)",
                List.of(
                        new FoundationCalcPoint(0.0, exposedHeight),
                        new FoundationCalcPoint(foundationWidth, exposedHeight),
                        new FoundationCalcPoint(foundationWidth, bottom),
                        new FoundationCalcPoint(0.0, bottom)
                )
        ));

        if ("pile".equalsIgnoreCase(request.getFoundationType())) {
            double[] centers = {
                    foundationWidth * 0.2,
                    foundationWidth * 0.5,
                    foundationWidth * 0.8
            };
            for (int index = 0; index < centers.length; index++) {
                double center = centers[index];
                shapes.add(new FoundationCalcPreviewShape(
                        "pile-" + index,
                        "polyline",
                        "#7a4b13",
                        "none",
                        List.of(
                                new FoundationCalcPoint(center, bottom),
                                new FoundationCalcPoint(center, bottom + pileLength)
                        )
                ));
            }
        } else {
            double offset = Math.max(foundationHeight * slope * 0.15, 0.2);
            shapes.add(new FoundationCalcPreviewShape(
                    "left-slope",
                    "polyline",
                    "#b26a00",
                    "none",
                    List.of(
                            new FoundationCalcPoint(0.0, exposedHeight),
                            new FoundationCalcPoint(-offset, bottom)
                    )
            ));
            shapes.add(new FoundationCalcPreviewShape(
                    "right-slope",
                    "polyline",
                    "#b26a00",
                    "none",
                    List.of(
                            new FoundationCalcPoint(foundationWidth, exposedHeight),
                            new FoundationCalcPoint(foundationWidth + offset, bottom)
                    )
            ));
        }

        return shapes;
    }

    private List<FoundationCalcResultItem> buildDerivedItems(FoundationCalcExecuteRequest request) {
        double foundationWidth = numericValue(request, "B", 3.0);
        double exposedHeight = numericValue(request, "h0", 0.5);
        double foundationHeight = numericValue(request, "h", 2.5);
        double buriedDepth = numericValue(request, "z", 2.5);
        double bearing = numericValue(request, "bearing", 180.0);
        double footprintArea = foundationWidth * foundationWidth;
        double concreteVolume = foundationWidth * foundationWidth * foundationHeight;
        double governingDepth = buriedDepth + exposedHeight + foundationHeight;
        double nominalBearingDemand = footprintArea == 0 ? 0.0 : (concreteVolume * 25.0) / footprintArea;
        double reserveBearing = bearing - nominalBearingDemand;

        return List.of(
                new FoundationCalcResultItem("Derived", "Footprint area", formatDouble(footprintArea)),
                new FoundationCalcResultItem("Derived", "Concrete volume", formatDouble(concreteVolume)),
                new FoundationCalcResultItem("Derived", "Governing depth", formatDouble(governingDepth)),
                new FoundationCalcResultItem("Derived", "Nominal bearing demand", formatDouble(nominalBearingDemand)),
                new FoundationCalcResultItem("Derived", "Bearing reserve", formatDouble(reserveBearing))
        );
    }

    private List<FoundationCalcGraphicLayer> buildGraphics(FoundationCalcExecuteRequest request) {
        double foundationWidth = numericValue(request, "B", 3.0);
        double columnWidth = numericValue(request, "b0", 1.0);
        double exposedHeight = numericValue(request, "h0", 0.5);
        double foundationHeight = numericValue(request, "h", 2.5);
        double buriedDepth = numericValue(request, "z", 2.5);
        double bearingZoneOffset = Math.max(foundationWidth * 0.15, 0.5);
        double columnLeft = Math.max((foundationWidth - columnWidth) / 2.0, 0.0);
        double columnRight = columnLeft + columnWidth;
        double bottom = exposedHeight + foundationHeight;
        double base = bottom + buriedDepth * 0.2;

        List<FoundationCalcGraphicLayer> layers = new ArrayList<>();
        layers.add(new FoundationCalcGraphicLayer(
                "bearing-zone",
                "Bearing zone",
                "polygon",
                "#f59e0b",
                "rgba(245, 158, 11, 0.16)",
                List.of(
                        new FoundationCalcPoint(-bearingZoneOffset, exposedHeight),
                        new FoundationCalcPoint(foundationWidth + bearingZoneOffset, exposedHeight),
                        new FoundationCalcPoint(foundationWidth + bearingZoneOffset, base),
                        new FoundationCalcPoint(-bearingZoneOffset, base)
                )
        ));
        layers.add(new FoundationCalcGraphicLayer(
                "footing",
                "Footing outline",
                "polygon",
                "#226d45",
                "rgba(87, 171, 90, 0.22)",
                List.of(
                        new FoundationCalcPoint(0.0, exposedHeight),
                        new FoundationCalcPoint(foundationWidth, exposedHeight),
                        new FoundationCalcPoint(foundationWidth, bottom),
                        new FoundationCalcPoint(0.0, bottom)
                )
        ));
        layers.add(new FoundationCalcGraphicLayer(
                "column",
                "Column",
                "polygon",
                "#1f4e79",
                "rgba(74, 144, 226, 0.25)",
                List.of(
                        new FoundationCalcPoint(columnLeft, 0.0),
                        new FoundationCalcPoint(columnRight, 0.0),
                        new FoundationCalcPoint(columnRight, exposedHeight),
                        new FoundationCalcPoint(columnLeft, exposedHeight)
                )
        ));
        layers.add(new FoundationCalcGraphicLayer(
                "ground",
                "Ground line",
                "polyline",
                "#6b7280",
                "none",
                List.of(
                        new FoundationCalcPoint(-1.2, 0.0),
                        new FoundationCalcPoint(foundationWidth + 1.2, 0.0)
                )
        ));

        if ("pile".equalsIgnoreCase(request.getFoundationType())) {
            double[] pileCenters = {
                    foundationWidth * 0.2,
                    foundationWidth * 0.5,
                    foundationWidth * 0.8
            };
            double pileLength = numericValue(request, "pileLength", 12.0) / 6.0;
            for (int index = 0; index < pileCenters.length; index++) {
                double center = pileCenters[index];
                layers.add(new FoundationCalcGraphicLayer(
                        "pile-" + index,
                        "Pile " + (index + 1),
                        "polyline",
                        "#7a4b13",
                        "none",
                        List.of(
                                new FoundationCalcPoint(center, bottom),
                                new FoundationCalcPoint(center, bottom + pileLength)
                        )
                ));
            }
        } else {
            double slope = numericValue(request, "slope", 1.0);
            double offset = Math.max(foundationHeight * slope * 0.15, 0.2);
            layers.add(new FoundationCalcGraphicLayer(
                    "left-slope",
                    "Left slope",
                    "polyline",
                    "#b26a00",
                    "none",
                    List.of(
                            new FoundationCalcPoint(0.0, exposedHeight),
                            new FoundationCalcPoint(-offset, bottom)
                    )
            ));
            layers.add(new FoundationCalcGraphicLayer(
                    "right-slope",
                    "Right slope",
                    "polyline",
                    "#b26a00",
                    "none",
                    List.of(
                            new FoundationCalcPoint(foundationWidth, exposedHeight),
                            new FoundationCalcPoint(foundationWidth + offset, bottom)
                    )
            ));
        }

        return layers;
    }

    private String valueOf(FoundationCalcExecuteRequest request, String key) {
        if (request.getValues() == null) {
            return "-";
        }
        String value = request.getValues().get(key);
        return blank(value) ? "-" : value;
    }

    private double numericValue(FoundationCalcExecuteRequest request, String key, double fallback) {
        if (request.getValues() == null) {
            return fallback;
        }
        String value = request.getValues().get(key);
        if (blank(value)) {
            return fallback;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException ignore) {
            return fallback;
        }
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private String formatDouble(double value) {
        return String.format("%.2f", value);
    }

    private String buildCsv(FoundationCalcRecordSummary record) {
        StringBuilder builder = new StringBuilder();
        builder.append("section,label,value\n");
        for (FoundationCalcResultItem item : record.items()) {
            builder.append(csvValue(item.section())).append(',')
                    .append(csvValue(item.label())).append(',')
                    .append(csvValue(item.value())).append('\n');
        }
        return builder.toString();
    }

    private String buildJson(FoundationCalcRecordSummary record) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n")
                .append("  \"recordId\": \"").append(jsonValue(record.recordId())).append("\",\n")
                .append("  \"projectId\": \"").append(jsonValue(record.projectId())).append("\",\n")
                .append("  \"nodeId\": \"").append(jsonValue(record.nodeId())).append("\",\n")
                .append("  \"foundationType\": \"").append(jsonValue(record.foundationType())).append("\",\n")
                .append("  \"operation\": \"").append(jsonValue(record.operation())).append("\",\n")
                .append("  \"iterationMode\": \"").append(jsonValue(record.iterationMode())).append("\",\n")
                .append("  \"title\": \"").append(jsonValue(record.title())).append("\",\n")
                .append("  \"createdAt\": \"").append(jsonValue(record.createdAt().toString())).append("\",\n")
                .append("  \"items\": [\n");
        for (int index = 0; index < record.items().size(); index++) {
            FoundationCalcResultItem item = record.items().get(index);
            builder.append("    {")
                    .append("\"section\": \"").append(jsonValue(item.section())).append("\", ")
                    .append("\"label\": \"").append(jsonValue(item.label())).append("\", ")
                    .append("\"value\": \"").append(jsonValue(item.value())).append("\"}")
                    .append(index == record.items().size() - 1 ? '\n' : ',')
                    .append(index == record.items().size() - 1 ? "" : "\n");
        }
        builder.append("  ]\n")
                .append("}\n");
        return builder.toString();
    }

    private String csvValue(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private String jsonValue(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private boolean useDatabase() {
        return persistenceMode.isDatabase();
    }
}
