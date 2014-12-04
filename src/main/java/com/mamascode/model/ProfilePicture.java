package com.mamascode.model;

/****************************************************
 * ProfilePicture: Model
 *  
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

public class ProfilePicture {
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// fields
	private int picId;			// 식별 id
	private String userName;	// 사용자 식별 이름
	private String fileName;	// 파일 이름
	boolean fileExist;			// 파일 존재 여부
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// getters and setters
	
	public int getPicId() {
		return picId;
	}
	
	public void setPicId(int picId) {
		this.picId = picId;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getUserProfilePictureName() {
		return userName + "_" + fileName;
	}

	public boolean isFileExist() {
		return fileExist;
	}

	public void setFileExist(boolean fileExist) {
		this.fileExist = fileExist;
	}
}
