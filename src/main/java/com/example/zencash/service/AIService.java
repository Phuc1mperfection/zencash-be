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
    "parts": [{
      "text": "From the invoice text below, extract:\n- The total amount (detect and return correct number for both VND and USD. USD might look like $50,00 or $1,200.50. VND might look like 8.000.000đ or 8,000,000 VND).\n- A short note.\n- The date if available (in dd/MM/yyyy or any common format).\n\nUser: %s\nInvoice:\n%s"
    }]
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
