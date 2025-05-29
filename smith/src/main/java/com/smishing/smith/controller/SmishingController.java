package com.smishing.smith.controller;

import com.smishing.smith.dto.SmishingRequest;
import com.smishing.smith.dto.SmishingResponse;
import com.smishing.smith.service.SmishingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/smishing")
public class SmishingController
{
	private final SmishingService smishingService;
	
	// 생성자
	public SmishingController(SmishingService smishingService)
	{
		this.smishingService = smishingService;
	}
	
	@PostMapping
	public ResponseEntity<SmishingResponse> analyzeMessage(@RequestBody SmishingRequest request)
	{
		SmishingResponse result = smishingService.analyze(request);
		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}
}
