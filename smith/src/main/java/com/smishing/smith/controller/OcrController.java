package com.smishing.smith.controller;

import com.smishing.smith.dto.SmishingRequest;
import com.smishing.smith.dto.SmishingResponse;
import com.smishing.smith.service.OcrService;
import com.smishing.smith.service.SmishingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/ocr")
public class OcrController {

    private final OcrService ocrService;
    private final SmishingService smishingService;

    public OcrController(OcrService ocrService, SmishingService smishingService) {
        this.ocrService = ocrService;
        this.smishingService = smishingService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> analyzeImages(@RequestParam("file") MultipartFile[] files) {
        try {
            // 1. 이미지에서 텍스트 추출
            String combinedText = ocrService.extractTextFromImages(files);

            // 2. URL 추출
            String firstUrl = extractFirstUrl(combinedText);

            // 3. AI 분석 요청
            SmishingRequest request = new SmishingRequest(combinedText, firstUrl);
            SmishingResponse smishingResult = smishingService.analyze(request);

            // 4. 응답 구성
            Map<String, Object> responseMap = new HashMap<>();
            System.out.println("[SMISHING-API] OCR 결과:");
            System.out.println(combinedText);

            System.out.println("[SMISHING-API] 추출된 URL: " + firstUrl);

            System.out.println("[SMISHING-API] AI 분석 결과: " +
                "isSmishing=" + smishingResult.isSmishing() +
                ", riskScore=" + smishingResult.getRiskScore());


            return ResponseEntity.ok(responseMap);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                Map.of("error", "OCR 또는 분석 중 오류 발생: " + e.getMessage())
            );
        }
    }

    private String extractFirstUrl(String text) {
        Pattern pattern = Pattern.compile("(http|https)://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
