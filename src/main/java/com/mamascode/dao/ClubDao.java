package com.mamascode.dao;

/****************************************************
 * ClubDao: interface
 * Date access object
 * 
 * Model: Club
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import java.util.List;

import com.mamascode.model.ClubCategory;
import com.mamascode.model.ClubJoinInfo;
import com.mamascode.model.User;
import com.mamascode.model.Club;

public interface ClubDao {
	///////// constants
	// 동아리 운영진 검색
	final static int SEARCH_MEMBER_CREW_NAME = 0;
	final static int SEARCH_MEMBER_CREW_NICKNAME = 1;
	final static int SEARCH_MEMBER_CREW_ALL = 2;
	
	// 동아리 검색
	final static int SEARCH_ALL = 0;
	final static int SEARCH_CLUB_NAME = 1;
	final static int SEARCH_CLUB_CATEGORY = 2;
	
	// 결과 정렬
	final static int ORDER_DEFAULT = 0;
	final static int ORDER_BY_NAME_DESC = 1;
	final static int ORDER_BY_NAME_ACS = 2;
	final static int ORDER_BY_DATE_DESC = 3;
	final static int ORDER_BY_DATE_ASC = 4;
	
	///////// Create, Update, Delete
	int create(Club club);			// create a club
	int inactivate(String clubName);	// inactivate a club
	int activate(String clubName);	// activate a club
	int update(Club club);			// update a club information
	int delete(String clubName);	// delete a club
	boolean isExistingClubName(String clubName);
	
	///////// get a size of rows of the clubs table
	int getCount(int searchby, Object keyword);
	
	///////// get information of a club
	Club get(String clubName);	// get a club information
	int getNumberOfClubMember(String clubName);
	
	///////// get a list of clubs
	List<Club> getList(int offset, int limit, int searchby, Object keyword, int orderby);
	
	///////// get a club list of the specified user
	int getCountUserClubs(String userName);
	List<Club> getUserClubs(int offset, int limit, String userName);
	List<Club> getUserClubs(String userName);
	
	///////// join, leave a club + close a club
	int insertClubApplication(String clubName, String userName, String comment);
	int insertClubInvitation(String clubName, String userName, String comment);
	boolean isThisUserApplicant(String clubName, String userName);
	boolean isThisUserInvitee(String clubName, String userName);
	boolean isExistingClubMember(String clubName, String memberName);
	int joinClub(String clubName, String memberName);
	int deleteApplication(String clubName, String userName);
	int deleteInvitation(String clubName, String userName);
	
	int leaveClub(String clubName, String memberName);
	int closeClub(String clubName);
	
	///////// club member list
	int getClubMemberCount(String clubName); // get a number of club's member
	List<String> getClubMemberList(String clubName); // get a list of club members(all)
	List<User> getClubMemberList(int offset, int limit, String clubName); // get a list of club members
			
	///////// club join application and invitation
	List<ClubJoinInfo> getClubJoinApplications(int offset, int limit, String clubName);
	int getClubJoinApplicationsCount(String clubName);
	List<ClubJoinInfo> getClubInvitations(int offset, int limit, String clubName);
	int getClubInvitationsCount(String clubName);
	
	///////// club crews and master check
	boolean isThisUserClubMaster(String userName, String clubName);
	boolean isThisUserClubCrew(String userName, String clubName);
	int insertClubCrew(String userName, String clubName);
	int deleteClubCrew(String userName, String clubName);
	
	List<User> getClubCrews(String clubName, int offset, int limit);
	int getClubCrewCount(String clubName);
	
	List<String> getClubNameForMasterUser(String userName);
	List<String> getClubNameForClubCrewUser(String userName);
	List<String> getClubNameForUser(String userName);
	
	///////// delete user information
	int deleteClubCrew(String userName);
	int deleteClubJoinApplication(String userName);
	int deleteClubJoinInvitation(String userName);
	int deleteClubMemberForUser(String userName);
	
	///////// search
	List<User> searchClubMember(String keyword, String clubName, int searchType);
	int searchClubMemberCount(String keyword, String clubName, int searchType);
	List<User> searchClubCrew(String keyword, String clubName, int searchType);
	int searchClubCrewCount(String keyword, String clubName, int searchType);
	
	///////// search
	int transferMaster(String clubName, String newMasterName);
	
	///////// processing club categories
	int addClubCategory(ClubCategory clubCategory);
	List<ClubCategory> getClubGrandCategories();
	List<ClubCategory> getClubChildCategories(int parentCategoryId);
	ClubCategory getClubCategory(int categoryId);
	
	///////// delete club 
	int deleteClubCrewAll(String clubName);
	int deleteClubJoinApplicationAll(String clubName);
	int deleteClubJoinInvitationAll(String clubName);
	
	///////// temporary
	int getLastInsertId(); // get a last inserted club number
	int getMaxClubNo();	// get a max club number of the clubs table
}
