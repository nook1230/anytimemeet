package com.mamascode.model;

/****************************************************
 * Club: Model
 * 
 * JSR-303 빈 검증 적용
 *  
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import java.sql.Date;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class Club {
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// fields
	
	/* required */
	private int clubNo;
	
	// 동아리 식별 이름(빈 검증)
	@NotNull
	@Size(min=1, max=50, message="동아리 아이디는 50글자 이하로 작성해주세요. 동아리 아이디는 필수입니다")
	@Pattern(regexp="^[a-zA-Z][a-zA-Z0-9_]+$", message="동아리 아이디에는 알파벳과 숫자만 사용해주세요")
	private String clubName;
	
	// 동아리 이름(빈 검증)
	@NotNull
	@Size(min=1, max=100, message="동아리 이름은 100글자 이하로 작성해주세요. 동아리 이름은 필수입니다")
	private String clubTitle;
	
	// 동아리 대분류(최상위 카테고리, 빈 검증)
	@Min(value=0, message="대분류를 선택해주세요")
	private short grandCategoryId;
	
	// 대분류 이름
	private String grandCategoryTitle;
	
	// 동아리 소분류(하위 카테고리)
	private short categoryId = -1;
	
	// 소분류 이름
	private String categoryTitle;
	
	// 동아리 마스터 식별 번호
	private int masterNo;
	
	// 동아리 마스터 식별 이름
	private String masterName;
	
	// 동아리 종류 - 1: approval type(default), 2: closed(invitation) type
	private short type = 1;
	
	// 동아리 회원 최대 수(빈 검증)
	@NotNull
	@Max(value=100, message="동아리 회원수는 최대 100명까지입니다")
	private int maxMemberNum = 100;	/* default: 100 */
	
	// 동아리 활성화 여부
	private boolean active = false;
	
	// 동아리가 현재 회원 모집 중인지
	private boolean recruit;
	
	// 동아리 소개(빈 검증)
	@Size(max=500, message="동아리 소개는 500글자 이하로 작성해주세요.")
	private String clubIntroduction;
	
	// 동아라 개설 날짜
	private Date dateOfCreated;
	
	// 현재 동아리 회원 수
	private int numberOfClubMember;
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// getters and setters /////////////////////////
	
	public int getClubNo() {
		return clubNo;
	}
	
	public void setClubNo(int clubNo) {
		this.clubNo = clubNo;
	}
	
	public String getClubName() {
		return clubName;
	}
	
	public void setClubName(String clubName) {
		this.clubName = clubName;
	}
	
	public String getClubTitle() {
		return clubTitle;
	}

	public void setClubTitle(String clubTitle) {
		this.clubTitle = clubTitle;
	}

	public short getGrandCategoryId() {
		return grandCategoryId;
	}

	public void setGrandCategoryId(short grandCategoryId) {
		this.grandCategoryId = grandCategoryId;
	}

	public String getGrandCategoryTitle() {
		return grandCategoryTitle;
	}

	public void setGrandCategoryTitle(String grandCategoryTitle) {
		this.grandCategoryTitle = grandCategoryTitle;
	}

	public short getCategoryId() {
		return categoryId;
	}
	
	public void setCategoryId(short categoryId) {
		this.categoryId = categoryId;
	}
	
	public String getCategoryTitle() {
		return categoryTitle;
	}

	public void setCategoryTitle(String categoryTitle) {
		this.categoryTitle = categoryTitle;
	}

	public int getMasterNo() {
		return masterNo;
	}
	
	public String getMasterName() {
		return masterName;
	}

	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}

	public void setMasterNo(int masterNo) {
		this.masterNo = masterNo;
	}
	
	public short getType() {
		return type;
	}
	
	public void setType(short type) {
		this.type = type;
	}
	
	public int getMaxMemberNum() {
		return maxMemberNum;
	}
	
	public void setMaxMemberNum(int maxMemberNum) {
		this.maxMemberNum = maxMemberNum;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean isRecruit() {
		return recruit;
	}
	
	public void setRecruit(boolean recruit) {
		this.recruit = recruit;
	}
	
	public String getClubIntroduction() {
		return clubIntroduction;
	}

	public void setClubIntroduction(String clubIntroduction) {
		this.clubIntroduction = clubIntroduction;
	}

	public Date getDateOfCreated() {
		return dateOfCreated;
	}
	
	public void setDateOfCreated(Date dateOfCreated) {
		this.dateOfCreated = dateOfCreated;
	}

	public int getNumberOfClubMember() {
		return numberOfClubMember;
	}

	public void setNumberOfClubMember(int numberOfClubMember) {
		this.numberOfClubMember = numberOfClubMember;
	}
}
