package com.eiker.tools.file.pdf;

import com.eiker.tools.file.pdf.dto.ReadPdfRequest;
import com.eiker.tools.file.pdf.dto.ReadPdfResponse;
import com.eiker.tools.file.pdf.dto.WritePdfRequest;
import com.eiker.tools.file.pdf.dto.WritePdfResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PdfApiTest {

    private static final Logger log = LoggerFactory.getLogger(PdfApiTest.class);

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String TEST_DIR = "D:\\code\\eiker\\eiker-be\\xiaoyi-tools\\test";
    private static final String PDF_FILE_1 = TEST_DIR + "\\企业名称服务.pdf";
    private static final String PDF_FILE_2 = TEST_DIR + "\\艾奇钟凤娟日报202604.pdf";
    private static final String OUTPUT_PDF = TEST_DIR + "\\test_writepdf.pdf";

    @Test
    @DisplayName("API测试场景1: 调用/api/v1/readpdf - 读取企业名称服务.pdf")
    void testReadPdfApi_Scenario1_EnterpriseNameService() {
        log.info("=== API测试场景1: 调用/api/v1/readpdf读取企业名称服务.pdf ===");

        ReadPdfRequest request = ReadPdfRequest.builder()
                .filePath(PDF_FILE_1)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ReadPdfRequest> httpEntity = new HttpEntity<>(request, headers);

        log.info("调用 POST /api/v1/readpdf, filePath={}", PDF_FILE_1);

        ResponseEntity<ReadPdfResponse> responseEntity = restTemplate.postForEntity(
                "/api/v1/readpdf",
                httpEntity,
                ReadPdfResponse.class
        );

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful(), "HTTP状态码应为2xx");
        ReadPdfResponse response = responseEntity.getBody();

        assertNotNull(response, "响应不应为null");
        log.info("响应结果: success={}, message={}", response.isSuccess(), response.getMessage());

        if (response.isSuccess()) {
            log.info("PDF页数: {}", response.getPageCount());
            log.info("PDF文本长度: {}", response.getText() != null ? response.getText().length() : 0);
            if (response.getPageTexts() != null) {
                for (int i = 0; i < response.getPageTexts().size(); i++) {
                    log.info("第{}页文本长度: {}", i + 1, response.getPageTexts().get(i).length());
                }
            }
        }

        assertTrue(response.isSuccess(), "读取PDF应成功");
        assertNotNull(response.getText(), "PDF文本不应为null");
        assertTrue(response.getPageCount() > 0, "PDF页数应大于0");

        log.info("=== API测试场景1完成 ===");
    }

    @Test
    @DisplayName("API测试场景2: 调用/api/v1/readpdf - 读取艾奇钟凤娟日报202604.pdf")
    void testReadPdfApi_Scenario2_DailyReport() {
        log.info("=== API测试场景2: 调用/api/v1/readpdf读取艾奇钟凤娟日报202604.pdf ===");

        ReadPdfRequest request = ReadPdfRequest.builder()
                .filePath(PDF_FILE_2)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ReadPdfRequest> httpEntity = new HttpEntity<>(request, headers);

        log.info("调用 POST /api/v1/readpdf, filePath={}", PDF_FILE_2);

        ResponseEntity<ReadPdfResponse> responseEntity = restTemplate.postForEntity(
                "/api/v1/readpdf",
                httpEntity,
                ReadPdfResponse.class
        );

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful(), "HTTP状态码应为2xx");
        ReadPdfResponse response = responseEntity.getBody();

        assertNotNull(response, "响应不应为null");
        log.info("响应结果: success={}, message={}", response.isSuccess(), response.getMessage());

        if (response.isSuccess()) {
            log.info("PDF页数: {}", response.getPageCount());
            log.info("PDF文本长度: {}", response.getText() != null ? response.getText().length() : 0);
            log.info("PDF内容预览: {}", response.getText() != null ?
                    (response.getText().length() > 200 ? response.getText().substring(0, 200) + "..." : response.getText()) : "null");
        }

        assertTrue(response.isSuccess(), "读取PDF应成功");
        assertNotNull(response.getText(), "PDF文本不应为null");
        assertTrue(response.getPageCount() > 0, "PDF页数应大于0");

        log.info("=== API测试场景2完成 ===");
    }

    @Test
    @DisplayName("API测试场景3: 调用/api/v1/writepdf - 生成包含标题、表格、图片的PDF")
    void testWritePdfApi_Scenario1_GeneratePdf() {
        log.info("=== API测试场景3: 调用/api/v1/writepdf生成PDF文件 ===");

        List<WritePdfRequest.Section> sections = new ArrayList<>();
        sections.add(WritePdfRequest.Section.builder()
                .level(1)
                .title("1. Project Overview")
                .content("This project aims to implement PDF file reading and generation functionality.\nUsing Apache PDFBox library for PDF operations,\nand providing service interfaces through Dapr sidecar.")
                .build());
        sections.add(WritePdfRequest.Section.builder()
                .level(2)
                .title("1.1 Function Description")
                .content("Main functions include:\n1. Read existing PDF files and extract text content\n2. Generate new PDF files, supporting titles, paragraphs, tables, images")
                .build());
        sections.add(WritePdfRequest.Section.builder()
                .level(2)
                .title("1.2 Technical Architecture")
                .content("Using Spring Boot to provide REST API interfaces,\nthrough Dapr for service discovery and invocation.")
                .build());

        List<String> headers = List.of("No.", "Module", "Status", "Owner");
        List<List<String>> rows = List.of(
                List.of("1", "PDF Reading", "Completed", "Zhang San"),
                List.of("2", "PDF Generation", "Completed", "Li Si"),
                List.of("3", "API Testing", "In Progress", "Wang Wu"),
                List.of("4", "Documentation", "Pending", "Zhao Liu")
        );

        List<WritePdfRequest.TableData> tables = new ArrayList<>();
        tables.add(WritePdfRequest.TableData.builder()
                .title("2. Project Schedule")
                .headers(headers)
                .rows(rows)
                .build());

        WritePdfRequest request = WritePdfRequest.builder()
                .outputPath(OUTPUT_PDF)
                .title("PDF Tools Service Test Report")
                .sections(sections)
                .tables(tables)
                .build();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<WritePdfRequest> httpEntity = new HttpEntity<>(request, httpHeaders);

        log.info("调用 POST /api/v1/writepdf, outputPath={}", OUTPUT_PDF);

        ResponseEntity<WritePdfResponse> responseEntity = restTemplate.postForEntity(
                "/api/v1/writepdf",
                httpEntity,
                WritePdfResponse.class
        );

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful(), "HTTP状态码应为2xx");
        WritePdfResponse response = responseEntity.getBody();

        assertNotNull(response, "响应不应为null");
        log.info("响应结果: success={}, message={}", response.isSuccess(), response.getMessage());

        if (response.isSuccess()) {
            log.info("输出文件路径: {}", response.getOutputPath());
            log.info("文件大小: {} bytes", response.getFileSize());
        }

        assertTrue(response.isSuccess(), "生成PDF应成功");
        assertEquals(OUTPUT_PDF, response.getOutputPath(), "输出路径应匹配");
        assertTrue(response.getFileSize() > 0, "文件大小应大于0");

        log.info("=== API测试场景3完成 ===");
    }

    @Test
    @DisplayName("API测试场景4: 调用/api/v1/readpdf - 读取不存在的PDF文件")
    void testReadPdfApi_Scenario4_FileNotFound() {
        log.info("=== API测试场景4: 调用/api/v1/readpdf读取不存在的PDF文件 ===");

        String nonExistentPath = TEST_DIR + "\\non_existent.pdf";
        ReadPdfRequest request = ReadPdfRequest.builder()
                .filePath(nonExistentPath)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ReadPdfRequest> httpEntity = new HttpEntity<>(request, headers);

        log.info("调用 POST /api/v1/readpdf, filePath={}", nonExistentPath);

        ResponseEntity<ReadPdfResponse> responseEntity = restTemplate.postForEntity(
                "/api/v1/readpdf",
                httpEntity,
                ReadPdfResponse.class
        );

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful(), "HTTP状态码应为2xx");
        ReadPdfResponse response = responseEntity.getBody();

        assertNotNull(response, "响应不应为null");
        log.info("响应结果: success={}, message={}", response.isSuccess(), response.getMessage());

        assertFalse(response.isSuccess(), "读取不存在的文件应失败");
        assertNotNull(response.getMessage(), "错误消息不应为null");

        log.info("=== API测试场景4完成 ===");
    }
}
