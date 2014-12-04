package com.mamascode.model.utils;

/****************************************************
 * ProcessingResult: Model
 * 
 * 특정 처리에 대한 결과를 담는 용도
 *  
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

public class ProcessingResult {
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// fields
	
	private String title;
	private boolean result = false;
	private String heading;
	private String success;
	private String fail;
	private String errorCause;
	private String successUrl;
	private String failUrl;
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// constructors
	
	public ProcessingResult(String title, String heading,
			String success, String fail) {
		this.title = title;
		this.heading = heading;
		this.success = success;
		this.fail = fail;
	}
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// getters and setters

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public boolean isResult() {
		return result;
	}
	
	public void setResult(boolean result) {
		this.result = result;
	}
	
	public String getHeading() {
		return heading;
	}
	
	public void setHeading(String heading) {
		this.heading = heading;
	}
	
	public String getSuccess() {
		return success;
	}
	
	public void setSuccess(String success) {
		this.success = success;
	}
	
	public String getFail() {
		return fail;
	}
	
	public void setFail(String fail) {
		this.fail = fail;
	}
	
	public String getErrorCause() {
		return errorCause;
	}
	
	public void setErrorCause(String errorCause) {
		this.errorCause = errorCause;
	}

	public String getSuccessUrl() {
		return successUrl;
	}

	public void setSuccessUrl(String successUrl) {
		this.successUrl = successUrl;
	}

	public String getFailUrl() {
		return failUrl;
	}

	public void setFailUrl(String failUrl) {
		this.failUrl = failUrl;
	}
}
