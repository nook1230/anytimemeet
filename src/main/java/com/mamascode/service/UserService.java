package com.mamascode.service;

/****************************************************
 * UserService: interface
 * 선언적 트랜잭션 적용
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mamascode.model.User;
import com.mamascode.utils.ListHelper;

@Transactional(propagation=Propagation.SUPPORTS, readOnly=true) // 기본 전파 속성:  Supports, 읽기 전용
public interface UserService {
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// constants
	final static short SEARCH_USER_NAME = 1;		// for users
	final static short SEARCH_NICKNAME = 2;			// for users
	final static short SEARCH_USER_REAL_NAME = 3;	// for a administrator
	final static short SEARCH_ALL = 4;				// user_name + nickname
	
	final static int ACTIVATE = 1;
	final static int INACTIVATE = 2;
	
	// error code
	final static int DEL_ERR_MASTER = 0;
	final static int DEL_ERR_RESULT_MATCH = 1;
	final static int DEL_ERR_SUCCESS = 2;
	final static int DEL_ERR_FAILURE = 3;
	final static int DEL_ERR_FAILURE_PASSWORD = 4;
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// interface methods
	
	/////// create, update
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean createNewUserAccount(User user);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	int activateUserAccount(String userName, int activate);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean setUserAccount(User user);
	
	/////// information
	User getUserAccountByUserNo(int userNo);
	User getUserAccountByUserName(String userName);
	boolean checkUserName(String userName);
	boolean checkEmail(String email);
	
	/////// user list
	ListHelper<User> getUserList(int page, int searchby, String keyword, int perPage);
	
	/////// user security: login, changing a password...
	boolean login(String userName, String password);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean changePassword(String userName, String password);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean certifyUserAccount(String userName, String certificationKey);
	
	/////// user authority check: club crew
	boolean isThisUserClubMaster(String userName, String clubName);
	boolean isThisUserClubCrew(String userName, String clubName);
	
	/////// delete
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	int deleteUserAccount(String userName, String profilePicturefilePath);
	
	/////// util 	
	String searchUserName(String keyword);
}
