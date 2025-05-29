package com.smishing.smith.dto;

public class SmishingRequest
{
	private String text;
	private String url;
	
	// 생성자
	public SmishingRequest() {}
	public SmishingRequest(String _text, String _url)
	{
		this.text = _text;
		this.url = _url;
	}
	
	// getters
	public String getText() { return text; }
	public String getUrl() { return url; }
	
	// setters
	public void setText(String _text) { this.text = _text; }
	public void setUrl(String _url) { this.url = _url; }
}
