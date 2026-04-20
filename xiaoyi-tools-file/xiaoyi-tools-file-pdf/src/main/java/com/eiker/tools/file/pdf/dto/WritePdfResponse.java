package com.eiker.tools.file.pdf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WritePdfResponse {
    private boolean success;
    private String message;
    private String outputPath;
    private long fileSize;
}
