package com.mamascode.service;

/****************************************************
 * ClubService: interface
 * 선언적 트랜잭션 적용
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mamascode.model.ClubCategory;
import com.mamascode.model.ClubJoinInfo;
import com.mamascode.model.User;
import com.mamascode.model.Club;
import com.mamascode.utils.ListHelper;

@Transactional(propagation=Propagation.SUPPORTS, readOnly=true) // 기본 전파 속성:  Supports, 읽기 전용
public interface ClubService {
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// constants
	final static int ERROR_ACTIVATE = 0;
	final static int ACTIVATE = 1;
	final static int INACTIVATE = 2;
	
	final static int LIST_ALL = 1;
	final static int LIST_NAME_SEARCH = 2;
	final static int LIST_CATEGORY_FILTERING = 3;
	final static int LIST_RECENTLY = 4;
	
	final static int ORDER_DEFAULT = 0;
	final static int ORDER_BY_NAME_DESC = 1;
	final static int ORDER_BY_NAME_ACS = 2;
	final static int ORDER_BY_DATE_DESC = 3;
	final static int ORDER_BY_DATE_ASC = 4;
	
	final static int SEARCH_MEMBER_CREW_NAME = 0;
	final static int SEARCH_MEMBER_CREW_NICKNAME = 1;
	final static int SEARCH_MEMBER_CREW_ALL = 2;
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// interface methods
	
	/////// create, update
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean createNewClub(Club club);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	int activateClub(String clubName, int activate);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean setClub(Club club);
	
	/////// information
	Club getClubInformation(String clubName);
	ListHelper<Club> getUserClubList(int page, String userName, int perPage);
	ListHelper<User> getClubMembers(int page, String clubName, int perPage);
	List<String> getClubMemberNames(String clubName);
	
	/////// club list
	ListHelper<Club> getClubList(int whatList, int page, Object keyword, int perPage, int orderby);
	
	/////// member joining, member leave
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean joinClub(String clubName, String memberName);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean leaveClub(String clubName, String memberName);
	
	/////// club join application and invitation
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean applyToClub(String clubName, String userName, String comment);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean inviteUserToClub(String clubName, String userName, String comment);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean cancelApplication(String clubName, String userName);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean cancelInvitation(String clubName, String userName);
	
	boolean checkClubMemberCount(String clubName);
	
	ListHelper<ClubJoinInfo> getClubJoinApplications(int page, String clubName, int perPage);
	ListHelper<ClubJoinInfo> getClubJoinInvitations(int page, String clubName, int perPage);
	
	boolean isThisUserApplicant(String clubName, String userName);
	boolean isThisUserInvitee(String clubName, String userName);
	
	/////// delete
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean deleteClub(String clubName);
	
	/////// check whether he/she is a member of a club
	boolean isThisUserInThisClub(String clubName, String userName);
	
	/////// club categories
	boolean addClubCategory(ClubCategory clubCategory);
	List<ClubCategory> getClubGrandCategories();
	List<ClubCategory> getClubChildCategories(int parentCategoryId);
	ClubCategory getClubCategory(int categoryId);
	
	///////// club crews and master check
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean appointClubCrew(String clubName, String userName);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean dismissClubCrew(String clubName, String userName);
	
	ListHelper<User> getClubCrews(int page, String clubName, int perPage);

	///////// search
	List<User> searchClubMember(String keyword, 
			String clubName, int searchType);
	List<User> searchClubCrew(String keyword, String clubName,
			int searchType);
	
	///////// transfer master
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean transferMaster(String clubName, String newMasterName);
}
