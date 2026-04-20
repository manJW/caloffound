package com.powergrid.foundation.core.project;

import jakarta.validation.constraints.NotBlank;

public record FoundationTifUploadRequest(
        String fileName,
        Double resolution,
        @NotBlank(message = "TIF content is required")
        String contentBase64
) {
}
