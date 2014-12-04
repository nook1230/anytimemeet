package com.mamascode.model.utils;

/****************************************************
 * PopupInfo: Model
 * 
 * 팝업 창 생성 시에 사용될 정보를 담는 용도
 *  
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import java.util.Map;

public class PopupInfo {
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// fields
	
	private String title;
	private String comment;
	private String url;
	private boolean access;
	private Map<String, Object> hiddens;
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// constructors
	
	public PopupInfo(String title, String comment, String url, boolean access) {
		this.title = title;
		this.comment = comment;
		this.url = url;
		this.access = access;
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
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public boolean isAccess() {
		return access;
	}
	
	public void setAccess(boolean access) {
		this.access = access;
	}
	
	public Map<String, Object> getHiddens() {
		return hiddens;
	}
	
	public void setHiddens(Map<String, Object> hiddens) {
		this.hiddens = hiddens;
	}
}
