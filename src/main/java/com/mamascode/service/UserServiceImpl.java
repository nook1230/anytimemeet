package com.mamascode.service;

/****************************************************
 * UserServiceImpl: UserService 구현
 * 
 * Spring component(@Service)
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mamascode.dao.ClubDao;
import com.mamascode.dao.NoticeDao;
import com.mamascode.dao.ProfilePictureDao;
import com.mamascode.dao.UserDao;
import com.mamascode.model.ProfilePicture;
import com.mamascode.model.User;
import com.mamascode.utils.ListHelper;
import com.mamascode.model.Club;

@Service
public class UserServiceImpl implements UserService {
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// Dao
	@Autowired UserDao userDao;
	@Autowired ClubDao clubDao;
	@Autowired ProfilePictureDao profilePictureDao;
	@Autowired NoticeDao noticeDao;
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// constructor and setter
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public void setClubDao(ClubDao clubDao) {
		this.clubDao = clubDao;
	}
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// implemented methods
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** createNewUserAccount: create a new user account  ******/
	@Override
	public boolean createNewUserAccount(User user) {
		int result = 0;
		
		if(!userDao.isExistingUserName(user.getUserName()) && 
				!userDao.isExistingEmail(user.getEmail())) {
			result = userDao.create(user);
		}
		
		if(result == 1)
			return true;
		
		return false;
	}
	
	/***** activateUserAccount: activate or inactivate a user account  ******/
	@Override
	public int activateUserAccount(String userName, int activate) {
		if(activate == ACTIVATE) {
			userDao.activate(userName);
		} else if(activate == INACTIVATE) {
			userDao.inactive(userName);
		}
		
		return activate;
	}
	
	/***** setUserAccount: set optional information of a user account  ******/
	@Override
	public boolean setUserAccount(User user) {
		int result = userDao.update(user);
		ProfilePicture picture = user.getProfilePicture();
		
		if(result == 1 && picture.isFileExist()) {
			// 사용자 정보 수정 성공 & 업로드 파일이 존재하는 경우
			// 프로필 사진 정보 DB 갱신
			
			if(profilePictureDao.doesHaveProfilePicture(user.getUserName())) {
				return profilePictureDao.update(
						picture.getUserName(), picture.getFileName()) == 1;
			} else {
				return profilePictureDao.register(
						picture.getUserName(), picture.getFileName()) == 1;
			}
		} else if(result == 1 && !picture.isFileExist()) {
			// 사용자 정보 수정 성공 & 업로드 파일이 존재하지 않는 경우
			// 그대로 종료
			return true;
		}
		
		return false;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** login: processing login  ******/
	@Override
	public boolean login(String userName, String password) {
		int result = userDao.isValidLogin(userName, password);
		
		if(result == 1)
			return true;
		
		return false;
	}
	
	/***** changePassword: set a password of a user account  ******/
	@Override
	public boolean changePassword(String userName, String password) {
		int result = userDao.changePassword(userName, password);
		
		if(result == 1)
			return true;
		
		return false;
	}
	
	/***** certifyUserAccount: certify a user account  ******/
	@Override
	public boolean certifyUserAccount(String userName, String certificationKey) {
		int result = userDao.certifyUser(userName, certificationKey);
		
		if(result == 1)
			return true;
		
		return false;
	}

	@Override
	public boolean isThisUserClubMaster(String userName, String clubName) {
		return clubDao.isThisUserClubMaster(userName, clubName);
	}

	@Override
	public boolean isThisUserClubCrew(String userName, String clubName) {
		return clubDao.isThisUserClubCrew(userName, clubName);
	}

	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** getUserAccount: get information of a user account  ******/
	@Override
	public User getUserAccountByUserNo(int userNo) {
		User user;
		ProfilePicture profilePicture;
		
		user = userDao.getByUserNo(userNo);
		profilePicture = profilePictureDao.get(userNo);
		user.setProfilePicture(profilePicture);
		
		return user;
	}
	
	/***** getUserAccountByUserName: get information of a user account ******/
	@Override
	public User getUserAccountByUserName(String userName) {
		User user;
		ProfilePicture profilePicture;
		
		user = userDao.getByUserName(userName);
		profilePicture = profilePictureDao.get(userName);
		user.setProfilePicture(profilePicture);
		
		return user;
	}
	
	/***** checkUserName: check whether there is a given user name in DB ******/
	@Override
	public boolean checkUserName(String userName) {
		int count = userDao.getCount(userName);
		
		// 중복된 계정 이름이 있으면 true
		if(count > 0)
			return true;
		
		return false;
	}
	
	/***** checkUserName: check whether there is a given email address in DB ******/
	@Override
	public boolean checkEmail(String email) {
		int count = userDao.getCountEmail(email);
		
		// 중복된 이메일 계정이 있으면 true
		if(count > 0)
			return true;
		
		return false;
	}

	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** getUserList: get a list of users  ******/
	@Override
	public ListHelper<User> getUserList(int page, int searchby, 
			String keyword, int perPage) {
		int count = 0;
		List<User> users = null;
		ListHelper<User> listHelper = null;
		
		if(searchby == 0) {
			// a total list of users
			count = userDao.getCount();
			listHelper = new ListHelper<User>(count, page, perPage);
			users = userDao.getList(listHelper.getOffset(), 
					listHelper.getObjectPerPage());
		} else {
			// a search result
			count = userDao.getCount(searchby, keyword);
			listHelper = new ListHelper<User>(count, page, perPage);
			users = userDao.getList(listHelper.getOffset(), 
					listHelper.getObjectPerPage(), 
					searchby, keyword);
		}
		
		listHelper.setList(users);
		return listHelper;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** deleteUserAccount: delete a user account  ******/
	@Override
	public int deleteUserAccount(String userName, String profilePicturefilePath) {		
		List<Club> userClub = clubDao.getUserClubs(userName);
		
		for(Club club : userClub) {
			// 사용자가 master인 동아리가 있다면 탈퇴 불가능
			// 동아리를 삭제하거나, 다른 회원에게 master 권한을 양도해야 함
			if(clubDao.isThisUserClubMaster(userName, club.getClubName()))
				return DEL_ERR_MASTER;
		}
		
		////// 관련 정보 삭제: 아래 메소드들에서 문제가 있을 경우 예외가 발생해서 트랜잭션 롤백 처리됨
		// 운영진 정보 모두 삭제
		clubDao.deleteClubCrew(userName);
		
		// 동아리 회원 정보 삭제
		clubDao.deleteClubMemberForUser(userName);
		
		// 동아리 가입 신청, 초대 정보 모두 삭제
		clubDao.deleteClubJoinApplication(userName);
		clubDao.deleteClubJoinInvitation(userName);
		
		// 알림 정보 모두 삭제
		noticeDao.deleteNotice(userName); // 트랜잭션 제외
		
		///// 프로필 사진과 사진 정보 삭제
		// 프로필 사진 파일 삭제
		ProfilePicture profilePicture = profilePictureDao.get(userName);
		if(profilePicture != null) {
			String profilePictureName = profilePicture.getUserProfilePictureName();
			File profilePictrueFile = new File(profilePicturefilePath + profilePictureName);
			profilePictrueFile.delete(); // 사진 삭제
			
			// DB 정보 삭제: 사진 삭제 성공 여부와 관련 없이 삭제 처리
			profilePictureDao.delete(userName);
		}
		
		// 사용자 정보 삭제
		int result = userDao.delete(userName);
		
		if(result == 1)
			return DEL_ERR_SUCCESS;
		
		return DEL_ERR_RESULT_MATCH;	// 이전에 DAO에서 예외가 발생 되어 리턴될 일은 없음
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** searchUserName: keyword에 일치하는 사용자 이름 반환  ******/
	@Override
	public String searchUserName(String keyword) {
		return userDao.getUserNameByKeyword(keyword);
	}
}
