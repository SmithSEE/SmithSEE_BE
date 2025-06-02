package com.smishing.smith.service;

import com.smishing.smith.dto.SmishingRequest;
import com.smishing.smith.dto.SmishingResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.HashMap;

@Service
public class SmishingService
{
	private final RestTemplate restTemplate = new RestTemplate();

	public SmishingResponse analyze(SmishingRequest request)
	{
	    String aiUrl = "http://localhost:8001/predict";

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.setBearerAuth(""); // 반드시 설정 필요

	    // Hugging Face API가 요구하는 형식으로 변환
	    Map<String, String> input = new HashMap<>();
	    input.put("text", request.getText());

	    HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(input, headers);

	    try {
	        ResponseEntity<String> response = restTemplate.exchange(
	            aiUrl,
	            HttpMethod.POST,
	            httpEntity,
	            String.class
	        );

	        // 응답 단순 파싱: LABEL_1이 스미싱, LABEL_0이 정상
	        String body = response.getBody();
			System.out.println("FastAPI 응답 본문: " + body);
	        boolean isSmishing = body != null && body.contains("LABEL_1");

			double riskScore = 0.0;
			if (body != null) {
				int scoreIndex = body.indexOf("riskScore");
				if (scoreIndex != -1) {
					String snippet = body.substring(scoreIndex);
					String value = snippet.replaceAll("[^0-9\\.]", ""); // 숫자만 추출
					try {
						riskScore = Double.parseDouble(value) * 100;
					} catch (NumberFormatException ignored) {}
				}
			}

			System.out.println("최종 판단 결과: smishing=" + isSmishing + ", score=" + riskScore);

	        return new SmishingResponse(
	            isSmishing,
	            riskScore
	        );

	    } catch (Exception e) {
	        e.printStackTrace();
	        return new SmishingResponse(false, 0.0);
	    }
	}

	/*
	public SmishingResponse analyze(SmishingRequest request)
	{
		String aiUrl = "https://api-inference.huggingface.co/models/sseul2/bert-smishing-model";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<SmishingRequest> httpEntity = new HttpEntity<SmishingRequest>(request, headers);

		ResponseEntity<SmishingResponse> response =
				restTemplate.exchange(
					aiUrl,
					HttpMethod.POST,
					httpEntity,
					SmishingResponse.class
				);

		return response.getBody();

		return new SmishingResponse(
		        true,
		        0.88
		    );
	}
	*/
}