package com.smishing.smith.controller;

import com.smishing.smith.dto.SmishingRequest;
import com.smishing.smith.dto.SmishingResponse;
import com.smishing.smith.service.OcrService;
import com.smishing.smith.service.SmishingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/ocr")
public class OcrController
{
    private final OcrService ocrService;
    private final SmishingService smishingService;

    public OcrController(OcrService ocrService, SmishingService smishingService)
    {
        this.ocrService = ocrService;
        this.smishingService = smishingService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> analyzeImages(@RequestParam("file") MultipartFile[] files)
    {
        List<SmishingResponse> results = new ArrayList<>();
        StringBuilder finalTextBuilder = new StringBuilder();

        for (MultipartFile file : files)
        {
            try
            {
                String text = ocrService.extractTextFromImage(file);
                text = text.replaceAll("\\r?\\n", "");
                String url = ocrService.extractFirstUrl(text);
                if (url != null)
                {
                    text = text.replace(url, "[URL]");
                }

                // 각 이미지 처리 로그
                System.out.println("처리 중 파일: " + file.getOriginalFilename());
                System.out.println("OCR 원문: " + text);
                System.out.println("추출된 URL: " + url);
                System.out.println("URL 치환 결과: " + text);
                System.out.println("────────────────────────────");

                finalTextBuilder.append(text).append(" ");

                SmishingRequest request = new SmishingRequest(text, url);
                SmishingResponse response = smishingService.analyze(request);
                results.add(response);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        // 최종 통합 문장 출력
        String finalText = finalTextBuilder.toString().trim();
        System.out.println("최종 통합 텍스트: " + finalText);

        // JSON 응답 구성
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("results", results);
        responseMap.put("combinedText", finalText);

        return ResponseEntity.ok(responseMap);
    }
}
