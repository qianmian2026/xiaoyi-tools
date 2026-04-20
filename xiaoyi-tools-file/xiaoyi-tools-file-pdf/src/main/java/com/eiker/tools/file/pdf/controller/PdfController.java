package com.eiker.tools.file.pdf.controller;

import com.eiker.tools.file.pdf.dto.ReadPdfRequest;
import com.eiker.tools.file.pdf.dto.ReadPdfResponse;
import com.eiker.tools.file.pdf.dto.WritePdfRequest;
import com.eiker.tools.file.pdf.dto.WritePdfResponse;
import com.eiker.tools.file.pdf.service.PdfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PdfController {

    private final PdfService pdfService;

    @PostMapping("/readpdf")
    public ResponseEntity<ReadPdfResponse> readPdf(@RequestBody ReadPdfRequest request) {
        log.info("Received readpdf request: filePath={}", request.getFilePath());
        ReadPdfResponse response = pdfService.readPdf(request);
        log.info("Readpdf response: success={}, message={}", response.isSuccess(), response.getMessage());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/writepdf")
    public ResponseEntity<WritePdfResponse> writePdf(@RequestBody WritePdfRequest request) {
        log.info("Received writepdf request: outputPath={}", request.getOutputPath());
        WritePdfResponse response = pdfService.writePdf(request);
        log.info("Writepdf response: success={}, message={}", response.isSuccess(), response.getMessage());
        return ResponseEntity.ok(response);
    }
}
