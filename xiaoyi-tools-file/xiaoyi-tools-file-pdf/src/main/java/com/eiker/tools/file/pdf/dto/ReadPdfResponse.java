package com.eiker.tools.file.pdf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadPdfResponse {
    private boolean success;
    private String message;
    private String text;
    private int pageCount;
    private List<String> pageTexts;
}
