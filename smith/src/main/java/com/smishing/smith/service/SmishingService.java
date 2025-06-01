package com.smishing.smith.service;

import com.smishing.smith.dto.SmishingRequest;
import com.smishing.smith.dto.SmishingResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SmishingService
{
	private final RestTemplate restTemplate = new RestTemplate();
	
	public SmishingResponse analyze(SmishingRequest request)
	{
		/*
		String aiUrl = "";
		
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
		*/
		return new SmishingResponse(
		        true,
		        0.88,
		        "임시 응답: URL 포함 + 금융 키워드 감지"
		    );
	}
}