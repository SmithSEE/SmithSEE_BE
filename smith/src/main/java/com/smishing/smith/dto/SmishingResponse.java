package com.smishing.smith.dto;

public class SmishingResponse
{
	private boolean isSmishing;
	private double riskScore;
	
	// 생성자
	public SmishingResponse() {}
	public SmishingResponse(boolean _isSmishing, double _riskScore)
	{
		this.isSmishing = _isSmishing;
		this.riskScore = _riskScore;
	}
	
	// getter
	public boolean isSmishing() { return isSmishing; }
	public double getRiskScore() { return riskScore; }
	
	// setter
	public void setSmishing(boolean _isSmishing) { this.isSmishing = _isSmishing; }
	public void setRiskScore(double _riskScore) { this.riskScore = _riskScore; }
}
