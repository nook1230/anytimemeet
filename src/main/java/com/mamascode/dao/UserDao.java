package com.mamascode.dao;

/****************************************************
 * UserDao: interface
 * Date access object
 * 
 * Model: User
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import java.util.List;

import com.mamascode.model.ClubJoinInfo;
import com.mamascode.model.User;

public interface UserDao {
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// constant
	public final static short SEARCH_USER_NAME = 1;			// for users
	public final static short SEARCH_NICKNAME = 2;			// for users
	public final static short SEARCH_USER_REAL_NAME = 3;	// for a administrator
	public final static short SEARCH_ALL = 4;				// user_name + nickname
	
	///////// Create, Update, Delete
	int create(User user);			// create an account
	int inactive(String userName);	// inactivate an account
	int activate(String userName);	// activate an account
	int update(User user);			// update an account information
	int delete(String userName);	// delete an account
	
	///////// get a size of rows of the users table
	int getCount();
	int getCount(String userName);
	int getCountEmail(String email);
	int getCount(int searchby, String keyword);
	
	boolean isExistingUserName(String userName);
	boolean isExistingEmail(String email);
	
	///////// get information of a user
	User getByUserNo(int userNo);
	User getByUserName(String userName);
	List<ClubJoinInfo> getApplyingClubs(String userName);
	List<ClubJoinInfo> getInvitedClubs(String userName);
	
	///////// get a list of users
	List<User> getList(int offset, int limit);
	List<User> getList(int offset, int limit, int searchby, String keyword); // search
	
	///////// login check
	int isValidLogin(int userNo, String password);
	int isValidLogin(String userName, String password);
	
	///////// user security
	int changePassword(String userName, String password);
	int certifyUser(String userName, String certificationKey); // certify an user account
	boolean isCertified(String userName);
	int setCertified(String userName, boolean set);
	
	///////// temporary
	int getMaxUserNo(); // get a max user number of the users table
	
	///////// util
	String getUserNameByKeyword(String keyword);
}
