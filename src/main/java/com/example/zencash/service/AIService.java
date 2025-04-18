package com.example.zencash.service;

import com.example.zencash.exception.AppException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.example.zencash.utils.ErrorCode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String AI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    private final RestTemplate restTemplate = new RestTemplate();

    public String sendMessageToAI(String text, String email) {
        // Chuẩn bị body JSON
        String jsonBody = String.format("""
            {
              "contents": [{
                "parts":[{"text": "Extract amount, date, and a short note from the following invoice text for user %s:\\n%s"}]
              }]
            }
            """, email, text.replace("\"", "\\\""));

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        // Tạo URL có key
        String urlWithKey = AI_API_URL + "?key=" + apiKey;

        // Gửi request
        ResponseEntity<String> response = restTemplate.exchange(urlWithKey, HttpMethod.POST, entity, String.class);

        // Trích xuất kết quả từ response JSON
        return extractTextFromGeminiResponse(response.getBody());
    }

    private String extractTextFromGeminiResponse(String responseBody) {
        // Đơn giản dùng regex để lấy đoạn text sinh ra bởi Gemini
        Pattern pattern = Pattern.compile("\"text\":\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(responseBody);
        if (matcher.find()) {
            return matcher.group(1).replace("\\n", "\n");
        }
        throw new AppException(ErrorCode.AI_RESPONSE_INVALID);
    }
}
