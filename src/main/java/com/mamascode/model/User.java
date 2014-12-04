package com.mamascode.model;

/****************************************************
 * User: Model
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

import java.sql.Timestamp;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class User {
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// fields
	
	/* required */
	
	// 사용자 식별 번호
	private int userNo;
	
	// 사용자 식별 이름
	@NotNull
	@Size(min=4, max=20, message="아이디는 4글자 이상 20글자 이하로 작성해주세요")
	@Pattern(regexp="^[a-zA-Z0-9_]+$", message="아이디에는 알파벳과 숫자, _만 허용됩니다")
	private String userName;
	
	// 비밀번호
	@NotNull
	@Size(min=4, max=20, message="비밀번호는 4글자 이상 20글자 이하로 작성해주세요")
	@Pattern(regexp="^[a-zA-Z0-9]+$", message="아이디에는 알파벳과 숫자만 허용됩니다")
	private String passwd;
	
	// 비밀번호 2
	@NotNull
	private String passwd2;
	
	// 이메일 주소
	@NotNull
	@Size(min=1, max=100, message="이메일은 100글자 이하로 작성해주세요")
	@Pattern(regexp="[A-Za-z0-9][A-Za-z0-9._-]+@[A-Za-z][A-Za-z0-9.-]+[.][A-Za-z]{2,4}", 
		message="형식에 맞지 않는 이메일 주소입니다")
	private String email;
	
	// 회원가입 날짜
	private Timestamp dateOfJoin;
	
	// 활성화 여부
	private boolean active;
	
	// 계정 인증 여부
	private boolean certified;
	
	// 인증 키
	private String certificationKey;
	
	/* optional */
	
	// 별명
	@Size(max=10, message="별명은 10글자 이하로 작성해주세요")
	private String nickname;
	
	// 실명
	@Size(max=10, message="실명은 10글자 이하로 작성해주세요")
	private String userRealName;
	
	// 생년월일
	private Timestamp dateOfBirth;
	
	// 동아리 가입 날짜
	private Timestamp dateOfClubJoin;
	
	// 동아리 가입 신청 목록
	private List<ClubJoinInfo> applyingClubs;
	
	// 동아리 초대 목록
	private List<ClubJoinInfo> invitedClubs;
	
	// 동아리 운영진 임명 날짜
	private Timestamp clubCrewAppointedDate;
	
	// 프로필 사진
	private ProfilePicture profilePicture;
	
	// 자기 소개
	@Size(max=500, message="자기 소개는 500글자 이하로 작성해주세요.")
	private String userIntroduction;
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// constructor /////////////////////////////////
	
	public User() {
		userNo = 0;
		userName = "";
		passwd = "1111";
		passwd2 = "";
		email = "";
		dateOfJoin = null;
		nickname = "";
		userRealName = "";
		dateOfBirth = null;
		applyingClubs = null;
		invitedClubs = null;
		userIntroduction = "";
	}
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// getters and setters /////////////////////////
	
	public int getUserNo() {
		return userNo;
	}

	public void setUserNo(int userNo) {
		this.userNo = userNo;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
	public String getPasswd2() {
		return passwd2;
	}

	public void setPasswd2(String passwd2) {
		this.passwd2 = passwd2;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Timestamp getDateOfJoin() {
		return dateOfJoin;
	}

	public void setDateOfJoin(Timestamp dateOfJoin) {
		this.dateOfJoin = dateOfJoin;
	}

	public boolean isCertified() {
		return certified;
	}

	public void setCertified(boolean certified) {
		this.certified = certified;
	}

	public String getCertificationKey() {
		return certificationKey;
	}

	public void setCertificationKey(String certificationKey) {
		this.certificationKey = certificationKey;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getUserRealName() {
		return userRealName;
	}

	public void setUserRealName(String userRealName) {
		this.userRealName = userRealName;
	}

	public Timestamp getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Timestamp dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Timestamp getDateOfClubJoin() {
		return dateOfClubJoin;
	}

	public void setDateOfClubJoin(Timestamp dateOfClubJoin) {
		this.dateOfClubJoin = dateOfClubJoin;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<ClubJoinInfo> getApplyingClubs() {
		return applyingClubs;
	}

	public void setApplyingClubs(List<ClubJoinInfo> applyingClubs) {
		this.applyingClubs = applyingClubs;
	}

	public List<ClubJoinInfo> getInvitedClubs() {
		return invitedClubs;
	}

	public void setInvitedClubs(List<ClubJoinInfo> invitedClubs) {
		this.invitedClubs = invitedClubs;
	}

	public Timestamp getClubCrewAppointedDate() {
		return clubCrewAppointedDate;
	}

	public void setClubCrewAppointedDate(Timestamp clubCrewAppointedDate) {
		this.clubCrewAppointedDate = clubCrewAppointedDate;
	}

	public ProfilePicture getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(ProfilePicture profilePicture) {
		this.profilePicture = profilePicture;
	}

	public String getUserIntroduction() {
		return userIntroduction;
	}

	public void setUserIntroduction(String userIntroduction) {
		this.userIntroduction = userIntroduction;
	}
}
