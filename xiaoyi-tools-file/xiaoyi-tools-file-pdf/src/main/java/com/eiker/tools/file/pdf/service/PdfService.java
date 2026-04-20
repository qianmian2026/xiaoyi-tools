package com.eiker.tools.file.pdf.service;

import com.eiker.tools.file.pdf.dto.ReadPdfRequest;
import com.eiker.tools.file.pdf.dto.ReadPdfResponse;
import com.eiker.tools.file.pdf.dto.WritePdfRequest;
import com.eiker.tools.file.pdf.dto.WritePdfResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PdfService {

    private static final float PAGE_MARGIN = 50f;
    private static final float PAGE_WIDTH = 595f;
    private static final float PAGE_HEIGHT = 842f;
    private static final float TITLE_FONT_SIZE = 20f;
    private static final float HEADING1_FONT_SIZE = 18f;
    private static final float HEADING2_FONT_SIZE = 16f;
    private static final float NORMAL_FONT_SIZE = 12f;
    private static final float LEADING = 18f;

    private static final PDType1Font FONT_HELVETICA = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDType1Font FONT_HELVETICA_BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private static final PDType1Font FONT_HELVETICA_OBLIQUE = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

    public ReadPdfResponse readPdf(ReadPdfRequest request) {
        String filePath = request.getFilePath();
        log.info("Reading PDF from: {}", filePath);

        try (PDDocument document = Loader.loadPDF(new File(filePath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            String fullText = stripper.getText(document);

            int pageCount = document.getNumberOfPages();
            List<String> pageTexts = new ArrayList<>();

            for (int i = 1; i <= pageCount; i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                pageTexts.add(stripper.getText(document));
            }

            return ReadPdfResponse.builder()
                    .success(true)
                    .message("PDF读取成功")
                    .text(fullText)
                    .pageCount(pageCount)
                    .pageTexts(pageTexts)
                    .build();

        } catch (IOException e) {
            log.error("Failed to read PDF: {}", e.getMessage(), e);
            return ReadPdfResponse.builder()
                    .success(false)
                    .message("读取PDF失败: " + e.getMessage())
                    .build();
        }
    }

    public WritePdfResponse writePdf(WritePdfRequest request) {
        String outputPath = request.getOutputPath();
        log.info("Writing PDF to: {}", outputPath);

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            float currentY = PAGE_HEIGHT - PAGE_MARGIN;

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                if (request.getTitle() != null && !request.getTitle().isEmpty()) {
                    currentY = drawTitle(contentStream, request.getTitle(), currentY);
                    currentY -= LEADING;
                }

                if (request.getSections() != null && !request.getSections().isEmpty()) {
                    for (WritePdfRequest.Section section : request.getSections()) {
                        currentY = drawSection(contentStream, document, section, currentY);
                        currentY -= LEADING / 2;
                    }
                }

                if (request.getTables() != null && !request.getTables().isEmpty()) {
                    for (WritePdfRequest.TableData table : request.getTables()) {
                        currentY = drawTable(contentStream, document, table, currentY);
                        currentY -= LEADING;
                    }
                }

                if (request.getImages() != null && !request.getImages().isEmpty()) {
                    for (WritePdfRequest.ImageData image : request.getImages()) {
                        currentY = drawImage(contentStream, document, image, currentY);
                        currentY -= LEADING;
                    }
                }
            }

            document.save(outputPath);

            File outputFile = new File(outputPath);
            log.info("PDF written successfully to: {}", outputPath);

            return WritePdfResponse.builder()
                    .success(true)
                    .message("PDF生成成功")
                    .outputPath(outputPath)
                    .fileSize(outputFile.length())
                    .build();

        } catch (IOException e) {
            log.error("Failed to write PDF: {}", e.getMessage(), e);
            return WritePdfResponse.builder()
                    .success(false)
                    .message("生成PDF失败: " + e.getMessage())
                    .build();
        }
    }

    private float drawTitle(PDPageContentStream contentStream, String title, float y) throws IOException {
        contentStream.setFont(FONT_HELVETICA_BOLD, TITLE_FONT_SIZE);
        contentStream.beginText();
        contentStream.newLineAtOffset(PAGE_MARGIN, y);
        contentStream.showText(title);
        contentStream.endText();
        return y - TITLE_FONT_SIZE - LEADING;
    }

    private float drawSection(PDPageContentStream contentStream, PDDocument document, WritePdfRequest.Section section, float y) throws IOException {
        float fontSize = section.getLevel() == 1 ? HEADING1_FONT_SIZE : HEADING2_FONT_SIZE;

        contentStream.setFont(FONT_HELVETICA_BOLD, fontSize);
        contentStream.beginText();
        contentStream.newLineAtOffset(PAGE_MARGIN, y);
        contentStream.showText(section.getTitle());
        contentStream.endText();
        y -= fontSize + LEADING / 2;

        if (section.getContent() != null && !section.getContent().isEmpty()) {
            String[] lines = section.getContent().split("\n");
            for (String line : lines) {
                contentStream.setFont(FONT_HELVETICA, NORMAL_FONT_SIZE);
                contentStream.beginText();
                contentStream.newLineAtOffset(PAGE_MARGIN, y);
                contentStream.showText(line);
                contentStream.endText();
                y -= NORMAL_FONT_SIZE + LEADING / 2;
            }
        }

        return y;
    }

    private float drawTable(PDPageContentStream contentStream, PDDocument document, WritePdfRequest.TableData table, float y) throws IOException {
        if (table.getTitle() != null && !table.getTitle().isEmpty()) {
            contentStream.setFont(FONT_HELVETICA_BOLD, HEADING2_FONT_SIZE);
            contentStream.beginText();
            contentStream.newLineAtOffset(PAGE_MARGIN, y);
            contentStream.showText(table.getTitle());
            contentStream.endText();
            y -= HEADING2_FONT_SIZE + LEADING / 2;
        }

        List<String> headers = table.getHeaders();
        List<List<String>> rows = table.getRows();

        if (headers == null || headers.isEmpty()) {
            return y;
        }

        int numCols = headers.size();
        float colWidth = (PAGE_WIDTH - 2 * PAGE_MARGIN) / numCols;
        float cellHeight = 30f;

        contentStream.setFont(FONT_HELVETICA_BOLD, NORMAL_FONT_SIZE);
        float headerY = y;

        for (int i = 0; i < headers.size(); i++) {
            float x = PAGE_MARGIN + i * colWidth;
            contentStream.beginText();
            contentStream.newLineAtOffset(x + 5, headerY - 10);
            contentStream.showText(headers.get(i));
            contentStream.endText();
        }

        y -= cellHeight;

        if (rows != null) {
            contentStream.setFont(FONT_HELVETICA, NORMAL_FONT_SIZE);
            for (List<String> row : rows) {
                for (int i = 0; i < Math.min(row.size(), numCols); i++) {
                    float x = PAGE_MARGIN + i * colWidth;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(x + 5, y - 10);
                    contentStream.showText(row.get(i));
                    contentStream.endText();
                }
                y -= cellHeight;
            }
        }

        return y;
    }

    private float drawImage(PDPageContentStream contentStream, PDDocument document, WritePdfRequest.ImageData image, float y) throws IOException {
        String imagePath = image.getImagePath();
        File imageFile = new File(imagePath);

        if (!imageFile.exists()) {
            log.warn("Image file not found: {}", imagePath);
            if (image.getCaption() != null && !image.getCaption().isEmpty()) {
                contentStream.setFont(FONT_HELVETICA, NORMAL_FONT_SIZE);
                contentStream.beginText();
                contentStream.newLineAtOffset(PAGE_MARGIN, y);
                contentStream.showText("[图片不存在: " + imagePath + "]");
                contentStream.endText();
                y -= NORMAL_FONT_SIZE + LEADING / 2;
            }
            return y;
        }

        float width = image.getWidth() > 0 ? image.getWidth() : 400f;
        float height = image.getHeight() > 0 ? image.getHeight() : 300f;

        float x = PAGE_MARGIN;
        y -= height;

        PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath, document);
        contentStream.drawImage(pdImage, x, y, width, height);

        y -= 20f;

        if (image.getCaption() != null && !image.getCaption().isEmpty()) {
            contentStream.setFont(FONT_HELVETICA_OBLIQUE, NORMAL_FONT_SIZE);
            contentStream.beginText();
            contentStream.newLineAtOffset(PAGE_MARGIN, y);
            contentStream.showText(image.getCaption());
            contentStream.endText();
            y -= NORMAL_FONT_SIZE;
        }

        return y;
    }
}
