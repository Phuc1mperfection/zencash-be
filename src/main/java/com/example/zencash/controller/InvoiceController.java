package com.example.zencash.controller;

import com.example.zencash.dto.TransactionResponse;
import com.example.zencash.entity.Transaction;
import com.example.zencash.service.OCRService;
import com.example.zencash.service.TransactionService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final OCRService ocrService;
    private final TransactionService transactionService;

    public InvoiceController(OCRService ocrService, TransactionService transactionService) {
        this.ocrService = ocrService;
        this.transactionService = transactionService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadInvoice(@RequestParam("file") MultipartFile file,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded");
        }

        // Tạo đường dẫn thư mục lưu ảnh hóa đơn
        String INVOICE_DIR = System.getProperty("user.dir") + "/image/invoice/";
        File folder = new File(INVOICE_DIR);


        // Tạo tên file duy nhất
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File targetFile = new File(INVOICE_DIR + filename);

        try {
            file.transferTo(targetFile); // Lưu file vào ổ cứng

            // OCR để lấy text
            String extractedText = ocrService.extractTextFromImage(targetFile); // Lưu ý: OCR từ File chứ không phải MultipartFile

            // Parse text → transaction
            TransactionResponse transaction = transactionService.createFromInvoiceText(extractedText, userDetails.getUsername());

            return ResponseEntity.ok(transaction);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Upload or OCR failed: " + e.getMessage());
        }
    }
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getInvoiceImage(@PathVariable String filename) {
        File file = new File(System.getProperty("user.dir") + "/image/invoice/" + filename);
        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Resource resource = new FileSystemResource(file);
            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) contentType = "application/octet-stream";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

}
