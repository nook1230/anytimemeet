package com.mamascode.model;

/****************************************************
 * Notice: Model
 *  
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import java.sql.Timestamp;

import com.mamascode.service.NoticeService;

public class Notice {
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// fields
	
	private int noticeId;				// 식별 id
	private String userName;			// 알림을 받을 사용자의 식별 이름
	private String noticeMsg;			// 알림 내용
	private String noticeUrl;			// 알림에 연결되는 url
	private boolean noticeRead;			// 알림이 확인되었는지
	private short noticeType; 			// 알림 종류 - 1: 일반, 2: 동아리 마스터
	private String extra;				// 기타
	private Timestamp noticeDate;		// 알림 일시
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// constructors
	
	public Notice() {
		noticeId = 0; userName = ""; noticeMsg = "";
		noticeUrl = ""; noticeRead = false;
		extra = ""; noticeDate = null;
		noticeType = NoticeService.NOTICE_TYPE_GENERAL;
	}
	
	public Notice(String userName, String noticeMsg) {
		this.userName = userName;
		this.noticeMsg = noticeMsg;
		noticeUrl = ""; extra = "";
		noticeType = NoticeService.NOTICE_TYPE_GENERAL;
	}
	
	public Notice(String userName, String noticeMsg, short noticeType) {
		this.userName = userName;
		this.noticeMsg = noticeMsg;
		this.noticeType = noticeType;
		noticeUrl = ""; extra = "";
	}
	
	public Notice(String userName, String noticeMsg, String noticeUrl) {
		this.userName = userName;
		this.noticeMsg = noticeMsg;
		this.noticeUrl = noticeUrl;
		noticeType = NoticeService.NOTICE_TYPE_GENERAL; extra = "";
	}

	public Notice(String userName, String noticeMsg, String noticeUrl,
			short noticeType) {
		this.userName = userName;
		this.noticeMsg = noticeMsg;
		this.noticeUrl = noticeUrl;
		this.noticeType = noticeType;
		extra = "";
	}
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// getters and setters
	
	public int getNoticeId() {
		return noticeId;
	}
	
	public void setNoticeId(int noticeId) {
		this.noticeId = noticeId;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getNoticeMsg() {
		return noticeMsg;
	}
	
	public void setNoticeMsg(String noticeMsg) {
		this.noticeMsg = noticeMsg;
	}
	
	public String getNoticeUrl() {
		return noticeUrl;
	}
	
	public void setNoticeUrl(String noticeUrl) {
		this.noticeUrl = noticeUrl;
	}
	
	public boolean isNoticeRead() {
		return noticeRead;
	}
	
	public void setNoticeRead(boolean noticeRead) {
		this.noticeRead = noticeRead;
	}
	
	public short getNoticeType() {
		return noticeType;
	}
	
	public void setNoticeType(short noticeType) {
		this.noticeType = noticeType;
	}
	
	public String getExtra() {
		return extra;
	}
	
	public void setExtra(String extra) {
		this.extra = extra;
	}

	public Timestamp getNoticeDate() {
		return noticeDate;
	}

	public void setNoticeDate(Timestamp noticeDate) {
		this.noticeDate = noticeDate;
	}
}
