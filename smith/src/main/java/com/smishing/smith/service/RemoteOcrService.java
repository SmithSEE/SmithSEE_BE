package com.smishing.smith.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RemoteOcrService {

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> analyzeRemotely(MultipartFile[] files) {
        String ocrApiUrl = "https://smishing-api-130947708321.asia-northeast3.run.app/ocr";

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        for (MultipartFile file : files) {
            try {
                Resource fileResource = new ByteArrayResource(file.getBytes()) {
                    @Override
                    public String getFilename() {
                        return file.getOriginalFilename();
                    }
                };
                body.add("file", fileResource);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("파일 읽기 실패: " + file.getOriginalFilename());
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                ocrApiUrl,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        return response.getBody();
    }
    
    public String extractFirstUrl(String text)
    {
        Pattern pattern = Pattern.compile("(http|https)://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find())
        {
            return matcher.group();
        }
        return null;
    }
}
