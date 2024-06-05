package com.dev.sagar.services.documentanalysis;

import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClient;
import com.azure.ai.formrecognizer.documentanalysis.models.*;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Context;
import com.azure.core.util.polling.SyncPoller;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Endpoint
@AnonymousAllowed
@Service
public class InvoiceAnalysis {
    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceAnalysis.class);
    private static final String MODEL_ID = "prebuilt-invoice";
    private final DocumentAnalysisClient analysisClient;

    public InvoiceAnalysis(DocumentAnalysisClient analysisClient) {
        this.analysisClient = analysisClient;
    }

    public InvoiceRecord analyzeInvoice(MultipartFile file) throws IOException {

        LOGGER.info("Beginning analyzing invoice... {}", file.getOriginalFilename());

        final SyncPoller<OperationResult, AnalyzeResult> analyzeInvoicePoller =
                analysisClient.beginAnalyzeDocument(MODEL_ID, BinaryData.fromBytes(file.getBytes()),
                        new AnalyzeDocumentOptions().setPages(List.of("1")), Context.NONE);

        final var analyzeInvoiceResult = analyzeInvoicePoller.getFinalResult();

        LOGGER.info("Analysis completed...");

        LOGGER.info("Documents analyzed: {}", analyzeInvoiceResult.getDocuments().size());

//        analyzeInvoiceResult.getDocuments().stream()
//                .map(AnalyzedDocument::getFields)
//                .forEach(fields -> fields.forEach((key, value) -> LOGGER.debug("Field : {} -> Value : {}", key, value.getValue())));

        final var invoiceRecord = retrieveFields(analyzeInvoiceResult);
        LOGGER.info("Invoice Record created successfully...{}", invoiceRecord);
        return invoiceRecord;
    }

    private InvoiceRecord retrieveFields(AnalyzeResult analyzeInvoiceResult) {
        for (var analyzedDocument: analyzeInvoiceResult.getDocuments()) {
            final var invoiceFields = analyzedDocument.getFields();
            Map<String, Object> fields = new HashMap<>(5);

            //Retrieve Vendor Name
            final var vendorName = invoiceFields.get("VendorName");
            if(vendorName != null) {
                if (DocumentFieldType.STRING == vendorName.getType()) {
                    LOGGER.debug("Vendor Name : {}", vendorName.getValueAsString());
                    fields.put("VendorName", vendorName.getValueAsString());
                }
            }

            //Retrieve Invoice Date
            final var invoiceDate = invoiceFields.get("InvoiceDate");
            if(invoiceDate != null) {
                if (DocumentFieldType.DATE == invoiceDate.getType()) {
                    LOGGER.debug("Invoice Date : {}", invoiceDate.getValueAsDate());
                    fields.put("InvoiceDate", invoiceDate.getValueAsDate());
                }
            }

            //Retrieve Customer Name
            final var customerName = invoiceFields.get("CustomerName");
            if(customerName != null) {
                if (DocumentFieldType.STRING == customerName.getType()) {
                    LOGGER.debug("Customer Name : {}", customerName.getValueAsString());
                    fields.put("CustomerName", customerName.getValueAsString());
                }
            }

            //Retrieve Invoice Number
            final var invoiceNumber = invoiceFields.get("InvoiceId");
            if(invoiceNumber != null) {
                if (DocumentFieldType.STRING == invoiceNumber.getType()) {
                    LOGGER.debug("Invoice Id : {}", invoiceNumber.getValueAsString());
                    fields.put("InvoiceId", invoiceNumber.getValueAsString());
                }
            }

            // Retrieve Invoice Total
            final var invoiceTotal = invoiceFields.get("InvoiceTotal");
            if(invoiceTotal != null) {
                if (DocumentFieldType.CURRENCY == invoiceTotal.getType()) {
                    LOGGER.debug("Invoice Total : {}{}", invoiceTotal.getValueAsCurrency().getSymbol(),invoiceTotal.getValueAsCurrency().getAmount());
                    fields.put("InvoiceTotal", invoiceTotal.getValueAsCurrency().getAmount());
                }
            }

            return new InvoiceRecord(fields.getOrDefault("CustomerName", "Not Detected").toString(),
                    fields.getOrDefault("VendorName", "Not Detected").toString(),
                    (LocalDate) fields.getOrDefault("InvoiceDate", LocalDate.now()),
                    fields.getOrDefault("InvoiceId", "Not Detected").toString(),
                    (Double) fields.getOrDefault("InvoiceTotal", 0.0));

        }
        return null;
    }

}
