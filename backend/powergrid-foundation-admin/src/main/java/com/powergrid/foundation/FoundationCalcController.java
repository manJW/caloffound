package com.powergrid.foundation;

import java.util.List;

import com.powergrid.foundation.common.ApiResponse;
import com.powergrid.foundation.core.calc.FoundationCalcBatchExecuteRequest;
import com.powergrid.foundation.core.calc.FoundationCalcBatchResult;
import com.powergrid.foundation.core.calc.FoundationCalcExecuteRequest;
import com.powergrid.foundation.core.calc.FoundationCalcExportDocument;
import com.powergrid.foundation.core.calc.FoundationCalcPreview;
import com.powergrid.foundation.core.calc.FoundationCalcRecordDetail;
import com.powergrid.foundation.core.calc.FoundationCalcRecordSummary;
import com.powergrid.foundation.core.calc.FoundationCalcSchema;
import com.powergrid.foundation.core.calc.FoundationCalcService;
import com.powergrid.foundation.core.calc.FoundationCalcTemplate;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/foundation/calc")
public class FoundationCalcController {
    private final FoundationCalcService foundationCalcService;

    public FoundationCalcController(FoundationCalcService foundationCalcService) {
        this.foundationCalcService = foundationCalcService;
    }

    @GetMapping("/schema")
    public ApiResponse<FoundationCalcSchema> loadSchema(
            @RequestParam String foundationType,
            @RequestParam String operation,
            @RequestParam String iterationMode
    ) {
        return ApiResponse.ok(
                "Loaded calculation schema",
                foundationCalcService.loadSchema(foundationType, operation, iterationMode)
        );
    }

    @GetMapping("/template")
    public ApiResponse<FoundationCalcTemplate> loadTemplate(@RequestParam String foundationType) {
        return ApiResponse.ok(
                "Loaded calculation template",
                foundationCalcService.loadTemplate(foundationType)
        );
    }

    @PostMapping("/preview")
    public ApiResponse<FoundationCalcPreview> preview(@Valid @RequestBody FoundationCalcExecuteRequest request) {
        return ApiResponse.ok(
                "Loaded calculation preview",
                foundationCalcService.preview(request)
        );
    }

    @PostMapping("/execute")
    public ApiResponse<FoundationCalcRecordSummary> execute(@Valid @RequestBody FoundationCalcExecuteRequest request) {
        return ApiResponse.ok(
                "Executed foundation calculation",
                foundationCalcService.execute(request)
        );
    }

    @PostMapping("/execute-batch")
    public ApiResponse<FoundationCalcBatchResult> executeBatch(
            @Valid @RequestBody FoundationCalcBatchExecuteRequest request
    ) {
        return ApiResponse.ok(
                "Executed foundation calculation batch",
                foundationCalcService.executeBatch(request)
        );
    }

    @GetMapping("/records")
    public ApiResponse<List<FoundationCalcRecordSummary>> listRecords(
            @RequestParam String projectId,
            @RequestParam String nodeId
    ) {
        return ApiResponse.ok(
                "Loaded calculation history",
                foundationCalcService.listRecords(projectId, nodeId)
        );
    }

    @GetMapping("/records/{recordId}")
    public ApiResponse<FoundationCalcRecordSummary> getRecord(@PathVariable String recordId) {
        return ApiResponse.ok(
                "Loaded calculation record",
                foundationCalcService.getRecord(recordId)
        );
    }

    @GetMapping("/records/{recordId}/detail")
    public ApiResponse<FoundationCalcRecordDetail> getRecordDetail(@PathVariable String recordId) {
        return ApiResponse.ok(
                "Loaded calculation record detail",
                foundationCalcService.getRecordDetail(recordId)
        );
    }

    @GetMapping("/records/{recordId}/export")
    public ApiResponse<FoundationCalcExportDocument> exportRecord(
            @PathVariable String recordId,
            @RequestParam(defaultValue = "json") String format
    ) {
        return ApiResponse.ok(
                "Exported calculation record",
                foundationCalcService.exportRecord(recordId, format)
        );
    }
}
