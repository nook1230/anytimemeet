package com.mamascode.model;

/****************************************************
 * Meeting: Model
 *  
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import java.sql.Timestamp;
import java.util.List;

public class Meeting {
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// fields
	private int meetingId = -1;					// 모임 식별 id
	private String clubName;					// 동아리 식별 이름
	private String title;						// 모임 글 제목
	private String administratorName;			// 개최자 식별 이름 
	private String administratorNickname;		// 개최자 별명
	private List<MeetingDate> meetingDates;		// 모임 날짜(List)
	private String introduction;				// 모임 설명
	private String location; 					// 모임 장소
	private short meetingStatus;				// 모임 상태 - 0: default, 1: confirmed, 2: canceled
	private Timestamp regDate;					// 등록 날짜
	private int repliesCount;					// 댓글 수
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// getters and setters /////////////////////////
	
	public int getMeetingId() {
		return meetingId;
	}
	
	public void setMeetingId(int meetingId) {
		this.meetingId = meetingId;
		
		// 모임 날짜 목록에도 각각 모임 식별 id를 설정
		if(this.meetingDates != null) {
			for(int i = 0; i < this.meetingDates.size(); i++) {
				this.meetingDates.get(i).setMeetingId(meetingId);
			}
		}
	}
	
	public String getClubName() {
		return clubName;
	}

	public void setClubName(String clubName) {
		this.clubName = clubName;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getAdministratorName() {
		return administratorName;
	}
	
	public void setAdministratorName(String administratorName) {
		this.administratorName = administratorName;
	}

	public String getAdministratorNickname() {
		return administratorNickname;
	}

	public void setAdministratorNickname(String administratorNickname) {
		this.administratorNickname = administratorNickname;
	}

	public List<MeetingDate> getMeetingDates() {
		return meetingDates;
	}

	public void setMeetingDates(List<MeetingDate> meetingDates) {
		this.meetingDates = meetingDates;
	}

	public String getIntroduction() {
		return introduction;
	}
	
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public short getMeetingStatus() {
		return meetingStatus;
	}
	
	public void setMeetingStatus(short meetingStatus) {
		this.meetingStatus = meetingStatus;
	}

	public Timestamp getRegDate() {
		return regDate;
	}

	public void setRegDate(Timestamp regDate) {
		this.regDate = regDate;
	}
	
	public int getRepliesCount() {
		return repliesCount;
	}

	public void setRepliesCount(int repliesCount) {
		this.repliesCount = repliesCount;
	}
}
