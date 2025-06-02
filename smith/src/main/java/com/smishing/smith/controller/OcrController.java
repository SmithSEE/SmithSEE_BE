package com.smishing.smith.controller;

import com.smishing.smith.dto.SmishingRequest;
import com.smishing.smith.dto.SmishingResponse;
import com.smishing.smith.service.RemoteOcrService;
import com.smishing.smith.service.SmishingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/ocr")
public class OcrController
{
    private final RemoteOcrService ocrService;
    private final SmishingService smishingService;

    public OcrController(RemoteOcrService ocrService, SmishingService smishingService)
    {
        this.ocrService = ocrService;
        this.smishingService = smishingService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> analyzeImages(@RequestParam("file") MultipartFile[] files)
    {
        // 1. 원격 OCR 서버에 파일 배열 전달
        Map<String, Object> ocrResult = ocrService.analyzeRemotely(files);

        // 2. combinedText와 log 추출
        String combinedText = (String) ocrResult.get("combinedText");
        List<String> ocrLogs = (List<String>) ocrResult.get("log");
        
        System.out.println("텍스트 본문:" + combinedText);

        // 3. URL 추출
        String firstUrl = ocrService.extractFirstUrl(combinedText);

        // 4. AI 분석 요청
        SmishingRequest request = new SmishingRequest(combinedText, firstUrl);
        SmishingResponse smishingResult = smishingService.analyze(request);

        // 5. 최종 응답 구성
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("result", smishingResult);
        responseMap.put("combinedText", combinedText);
        responseMap.put("log", ocrLogs);

        return ResponseEntity.ok(responseMap);
    }
}
