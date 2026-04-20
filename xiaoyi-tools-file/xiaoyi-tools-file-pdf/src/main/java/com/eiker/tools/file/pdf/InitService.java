package com.eiker.tools.file.pdf;

import org.springframework.boot.SpringApplication;

public class InitService {

    public static void main(String[] args) {
        System.setProperty("spring.application.name", "javapdf");
        
        for (int i = 0; i < args.length; i++) {
            if ("-p".equals(args[i]) && i + 1 < args.length) {
                System.setProperty("server.port", args[i + 1]);
                break;
            }
        }
        
        SpringApplication.run(PdfToolsApplication.class, args);
    }
}
