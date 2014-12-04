package com.mamascode.model;

/****************************************************
 * MeetingDate: Model
 *  
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import java.sql.Date;

public class MeetingDate {
	//////////////////////////////////////////
	// fields
	private int dateId;					// 날짜 id
	private int meetingId;				// 모임 식별 id
	private Date recommendedDate;		// 제안 날짜
	private String recommendedTime;		// 제안 시간
	private short dateStatus;			// 제안 모임 날짜의 상태 - 0: default, 1: confirmed, 2: not-confirmed
	private int countParticipants;		// 이 날짜에 참석하기로 한 참가자 수
	
	//////////////////////////////////////////
	// static method
	
	/* getDayString: 정수형 day를 문자열 요일 표시로 변환 */
	public static String getDayString(int day) {
		String dayOfWeek;
		
		switch(day) {
		case 0: dayOfWeek = "日"; break;
		case 1: dayOfWeek = "月"; break;
		case 2: dayOfWeek = "火"; break;
		case 3: dayOfWeek = "水"; break;
		case 4: dayOfWeek = "木"; break;
		case 5: dayOfWeek = "金"; break;
		case 6: dayOfWeek = "土"; break;
		default: dayOfWeek = "?"; break;
		}
		
		return dayOfWeek;
	}
	
	//////////////////////////////////////////
	// getters and setters
	public int getDateId() {
		return dateId;
	}
	
	public void setDateId(int dateId) {
		this.dateId = dateId;
	}
	
	public int getMeetingId() {
		return meetingId;
	}
	
	public void setMeetingId(int meetingId) {
		this.meetingId = meetingId;
	}
	
	public Date getRecommendedDate() {
		return recommendedDate;
	}
	
	public void setRecommendedDate(Date recommendedDate) {
		this.recommendedDate = recommendedDate;
	}
	
	public String getRecommendedTime() {
		return recommendedTime;
	}
	
	public void setRecommendedTime(String recommendedTime) {
		this.recommendedTime = recommendedTime;
	}

	public short getDateStatus() {
		return dateStatus;
	}

	public void setDateStatus(short dateStatus) {
		this.dateStatus = dateStatus;
	}

	public int getCountParticipants() {
		return countParticipants;
	}

	public void setCountParticipants(int countParticipants) {
		this.countParticipants = countParticipants;
	}
}
