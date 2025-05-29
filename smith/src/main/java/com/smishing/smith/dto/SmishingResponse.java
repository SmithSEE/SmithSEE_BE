package com.smishing.smith.dto;

public class SmishingResponse
{
	private boolean isSmishing;
	private double riskScore;
	private String reason;
	
	// 생성자
	public SmishingResponse() {}
	public SmishingResponse(boolean _isSmishing, double _riskScore, String _reason)
	{
		this.isSmishing = _isSmishing;
		this.riskScore = _riskScore;
		this.reason = _reason;
	}
	
	// getter
	public boolean isSmishing() { return isSmishing; }
	public double getRiskScore() { return riskScore; }
	public String getReason() { return reason; }
	
	// setter
	public void setSmishing(boolean _isSmishing) { this.isSmishing = _isSmishing; }
	public void setRiskScore(double _riskScore) { this.riskScore = _riskScore; }
	public void setReason(String _reason) { this.reason = _reason; }
}
