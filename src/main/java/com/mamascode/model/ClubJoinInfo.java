package com.mamascode.model;

/****************************************************
 * ClubJoinInfo: Model
 * 
 * 동아리 가입 관련 정보
 *  
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

public class ClubJoinInfo {
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// fields
	private String clubName;	// 동아리 식별 이름
	private String clubTitle;	// 동아리 이름
	private String userName;	// 사용자 식별 이름
	private String comment;		// 가입,초대 코멘트
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// constructors ////////////////////////////////
	
	public ClubJoinInfo() {	}
	
	public ClubJoinInfo(String clubName, String clubTitle, String userName,
			String comment) {
		this.clubName = clubName;
		this.clubTitle = clubTitle;
		this.userName = userName;
		this.comment = comment;
	}
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// getters and setters /////////////////////////
	
	public String getClubName() {
		return clubName;
	}
	
	public void setClubName(String clubName) {
		this.clubName = clubName;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getClubTitle() {
		return clubTitle;
	}
	
	public void setClubTitle(String clubTitle) {
		this.clubTitle = clubTitle;
	}
}
