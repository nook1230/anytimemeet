package com.mamascode.dao;

/****************************************************
 * ProfilePictureDao: interface
 * Date access object
 * 
 * Model: ProfilePicture
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import com.mamascode.model.ProfilePicture;

public interface ProfilePictureDao {
	///////// constants
	
	///////// Create, Update, Delete
	int register(String userName, String fileName);
	int update(int picId, String fileName);
	int update(String userName, String fileName);
	int delete(int picId);
	int delete(String userName);
	
	///////// get a size of rows of the table
	int getCount();
	boolean doesHaveProfilePicture(String userName);
	
	///////// get a name of a user profile picture
	ProfilePicture get(String userName);
	ProfilePicture get(int picId);
	
}
