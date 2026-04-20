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
public class WritePdfRequest {
    private String outputPath;
    private String title;
    private List<Section> sections;
    private List<TableData> tables;
    private List<ImageData> images;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Section {
        private int level;
        private String title;
        private String content;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TableData {
        private String title;
        private List<String> headers;
        private List<List<String>> rows;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageData {
        private String imagePath;
        private String caption;
        private float width;
        private float height;
    }
}
