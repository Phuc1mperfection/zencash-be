package com.example.zencash.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.zencash.dto.FeedbackRequest;
import com.example.zencash.service.MailService;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final MailService mailService;

    public FeedbackController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping
    public ResponseEntity<String> sendFeedback(@RequestBody FeedbackRequest request) {
        mailService.sendFeedbackToAdmin(request.getUserEmail(), request.getMessage());
        return ResponseEntity.ok("Phản hồi đã được gửi đến admin.");
    }
}
