package com.example.zencash.controller;

import com.example.zencash.dto.InvoiceExtractedDataResponse;
import com.example.zencash.dto.InvoiceTransactionRequest;
import com.example.zencash.dto.TransactionResponse;
import com.example.zencash.service.OCRService;
import com.example.zencash.service.TransactionService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> uploadInvoiceForExtraction(@RequestParam("file") MultipartFile file,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded");
        }

        try {
            // Tạo đường dẫn thư mục lưu ảnh hóa đơn
            String INVOICE_DIR = System.getProperty("user.dir") + "/image/invoice/";
            File folder = new File(INVOICE_DIR);
            if (!folder.exists()) folder.mkdirs();

            // Tạo tên file duy nhất
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File targetFile = new File(INVOICE_DIR + filename);

            // Lưu file vào ổ cứng
            file.transferTo(targetFile);

            // Trích xuất văn bản từ ảnh
            String extractedText = ocrService.extractTextFromImage(targetFile);

            // Gửi văn bản OCR cho AI để phân tích và trích xuất dữ liệu
            InvoiceExtractedDataResponse extractedData = transactionService.extractInvoiceData(
                    extractedText, userDetails.getUsername()
            );

            // Trả về dữ liệu để client xác nhận
            return ResponseEntity.ok(extractedData);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Upload or OCR failed: " + e.getMessage());
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<TransactionResponse> confirmExtractedInvoice(
            @RequestBody InvoiceTransactionRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            // Gắn email từ token vào request để đảm bảo tính xác thực
            request.setEmail(userDetails.getUsername());

            // Gọi service tạo transaction từ thông tin hóa đơn đã xác nhận
            TransactionResponse response = transactionService.createTransactionFromInvoice(request);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
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
