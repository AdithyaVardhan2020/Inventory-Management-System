package com.inventory.backend_java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.inventory.backend_java.service.DatasetImportService;

@SpringBootApplication
public class BackendJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendJavaApplication.class, args);
        new DatasetImportService().importIfNeeded();
    }
}
