package com.dev.sagar.services.documentanalysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class DocumentController {
    private final InvoiceAnalysis invoiceAnalysis;
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentController.class);

    public DocumentController(InvoiceAnalysis invoiceAnalysis) {
        this.invoiceAnalysis = invoiceAnalysis;
    }

    @PostMapping("/fileupload")
    public InvoiceRecord handleFileUpload(@RequestParam("file") MultipartFile file) {
        LOGGER.info("Received file: {}", file.getOriginalFilename());
        try {
            return invoiceAnalysis.analyzeInvoice(file);
        } catch (IOException e) {
            LOGGER.error("Error analyzing document", e);
        }
        return null;
    }
}
