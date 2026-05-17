package com.knowledgeos.content.infrastructure.extractor;

import com.knowledgeos.content.domain.service.ContentExtractor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PdfContentExtractor implements ContentExtractor {

    @Override
    public boolean supports(String fileType, String sourceUrl) {
        return fileType != null && "application/pdf".equalsIgnoreCase(fileType);
    }

    @Override
    public String extract(byte[] fileBytes, String sourceUrl) throws IOException {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new IllegalArgumentException("PDF content bytes are empty or null.");
        }
        try (PDDocument document = Loader.loadPDF(fileBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}
