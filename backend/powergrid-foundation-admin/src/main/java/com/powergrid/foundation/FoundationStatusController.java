package com.powergrid.foundation;

import com.powergrid.foundation.common.ApiResponse;
import com.powergrid.foundation.core.FoundationSmokeSnapshot;
import com.powergrid.foundation.core.FoundationStatusService;
import com.powergrid.foundation.core.FoundationStatusSnapshot;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/foundation")
public class FoundationStatusController {
    private final FoundationStatusService foundationStatusService;

    public FoundationStatusController(FoundationStatusService foundationStatusService) {
        this.foundationStatusService = foundationStatusService;
    }

    @GetMapping("/status")
    public ApiResponse<FoundationStatusSnapshot> status() {
        return ApiResponse.ok(
                "Foundation BS renovation skeleton is active",
                foundationStatusService.snapshot()
        );
    }

    @GetMapping("/status/smoke")
    public ApiResponse<FoundationSmokeSnapshot> smoke() {
        return ApiResponse.ok(
                "Foundation smoke snapshot computed from the active persistence mode",
                foundationStatusService.smoke()
        );
    }
}
