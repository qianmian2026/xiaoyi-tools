package com.eiker.tools.file.pdf.service;

import com.eiker.tools.file.pdf.dto.ReadPdfRequest;
import com.eiker.tools.file.pdf.dto.ReadPdfResponse;
import com.eiker.tools.file.pdf.dto.WritePdfRequest;
import com.eiker.tools.file.pdf.dto.WritePdfResponse;

public interface PdfService {

    ReadPdfResponse readPdf(ReadPdfRequest request);

    WritePdfResponse writePdf(WritePdfRequest request);
}
