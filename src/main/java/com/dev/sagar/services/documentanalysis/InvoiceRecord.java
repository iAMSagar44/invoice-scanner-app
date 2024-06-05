package com.dev.sagar.services.documentanalysis;

import java.time.LocalDate;

public record InvoiceRecord(String customerName, String vendorName,
                            LocalDate invoiceData, String invoiceNumber,
                            Double invoiceTotal) {
}
