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

@CrossOrigin(origins = "*")
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
        List<String> ocrLogs = new ArrayList<>();
        StringBuilder finalTextBuilder = new StringBuilder();

        for (MultipartFile file : files)
        {
            try {
                String text = ocrService.extractTextFromImage(file);
                text = text.replaceAll("\\r?\\n", "");
                String url = ocrService.extractFirstUrl(text);
                if (url != null) {
                    text = text.replace(url, "[URL]");
                }

                // 로그 누적
                ocrLogs.add("파일: " + file.getOriginalFilename());
                ocrLogs.add("OCR 결과: " + text);
                ocrLogs.add("URL: " + url);
                ocrLogs.add("────────────");

                finalTextBuilder.append(text).append(" ");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        String finalText = finalTextBuilder.toString().trim();
        System.out.println("최종 통합 텍스트: " + finalText);

        // AI 요청은 여기서 딱 1번만
        String firstUrl = ocrService.extractFirstUrl(finalText);
        SmishingRequest request = new SmishingRequest(finalText, firstUrl);
        SmishingResponse smishingResult = smishingService.analyze(request);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("result", smishingResult);
        responseMap.put("combinedText", finalText);
        responseMap.put("log", ocrLogs);

        return ResponseEntity.ok(responseMap);
    }
}
