package com.mamascode.service;

/****************************************************
 * ClubServiceImpl: ClubService 구현
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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mamascode.dao.ClubArticleDao;
import com.mamascode.dao.ClubArticleReplyDao;
import com.mamascode.dao.ClubDao;
import com.mamascode.dao.MeetingDao;
import com.mamascode.dao.MeetingReplyDao;
import com.mamascode.model.ClubCategory;
import com.mamascode.model.ClubJoinInfo;
import com.mamascode.model.User;
import com.mamascode.model.Club;
import com.mamascode.utils.ListHelper;

@Service
public class ClubServiceImpl implements ClubService {
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// clubDao
	@Autowired ClubDao clubDao;
	@Autowired MeetingDao meetingDao;
	@Autowired ClubArticleDao clubArticleDao;
	@Autowired MeetingReplyDao meetingReplyDao;
	@Autowired ClubArticleReplyDao articleReplyDao;
	
	public void setClubDao(ClubDao clubDao) {
		this.clubDao = clubDao;
	}
	
	public void setMeetingDao(MeetingDao meetingDao) {
		this.meetingDao = meetingDao;
	}

	public void setClubArticleDao(ClubArticleDao clubArticleDao) {
		this.clubArticleDao = clubArticleDao;
	}
	
	public void setMeetingReplyDao(MeetingReplyDao meetingReplyDao) {
		this.meetingReplyDao = meetingReplyDao;
	}
	
	public void setArticleReplyDao(ClubArticleReplyDao articleReplyDao) {
		this.articleReplyDao = articleReplyDao;
	}
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// constructor(default)
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// implemented methods	

	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** createNewClub: create a new club  ******/
	@Override
	public boolean createNewClub(Club club) {
		int result = clubDao.create(club);
		
		if(result == 1)
			return true;
		
		return false;
	}
	
	/***** activateClub: activate or inactivate a club  ******/
	@Override
	public int activateClub(String clubName, int activate) {
		int result = 0;
		
		if(activate == ACTIVATE) {
			result = clubDao.activate(clubName);
		} else if(activate == INACTIVATE) {
			result = clubDao.inactivate(clubName);
		}
		
		if(result == 1)
			return activate;
		else
			return ClubService.ERROR_ACTIVATE;
	}
	
	/***** setClub: set club information  ******/
	@Override
	public boolean setClub(Club club) {
		int result = clubDao.update(club);
		
		if(result == 1)
			return true;
		
		return false;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** getClubInformation  ******/
	@Override
	public Club getClubInformation(String clubName) {
		return clubDao.get(clubName);
	}
	
	/***** getUserClubList ******/
	@Override
	public ListHelper<Club> getUserClubList(int page, String userName,
			int perPage) {
		int totalCount = clubDao.getCountUserClubs(userName);
		ListHelper<Club> listHelper = new ListHelper<Club>(
				totalCount, page, perPage);
		List<Club> clubs = clubDao.getUserClubs(listHelper.getOffset(), perPage, userName);
		listHelper.setList(clubs);
		
		return listHelper;
	}

	/***** getClubMembers: get a member list of a club ******/
	@Override
	public ListHelper<User> getClubMembers(int page, String clubName, int perPage) {
		int count = 0;
		List<User> members = null;
		ListHelper<User> listHelper = null;
		
		count = clubDao.getClubMemberCount(clubName);
		listHelper = new ListHelper<User>(count, page, perPage);
		members = clubDao.getClubMemberList(
				listHelper.getOffset(), listHelper.getObjectPerPage(), clubName);
		
		listHelper.setList(members);
		return listHelper;
	}
	
	@Override
	/***** getClubMemberNames ******/
	public List<String> getClubMemberNames(String clubName) {
		return clubDao.getClubMemberList(clubName);
	}
	
	/***** isThisUserInThisClub: check if this user is in this club ******/
	@Override
	public boolean isThisUserInThisClub(String clubName, String userName) {
		if(clubDao.isExistingClubMember(clubName, userName))
			return true;
		
		return false;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** getClubList: get a club list by a given condition(whatList)  ******/
	@Override
	public ListHelper<Club> getClubList(int whatList, int page, Object keyword, int perPage, int orderby) {
		int count = 0;
		List<Club> clubs = null;
		ListHelper<Club> listHelper = null;
		
		switch(whatList) {
		case LIST_NAME_SEARCH:
			count = clubDao.getCount(ClubDao.SEARCH_CLUB_NAME, keyword);
			listHelper = new ListHelper<Club>(count, page, perPage);
			clubs = clubDao.getList(listHelper.getOffset(), 
					listHelper.getObjectPerPage(), ClubDao.SEARCH_CLUB_NAME, keyword, orderby);
			break;
			
		case LIST_CATEGORY_FILTERING:
			short catId = (short) keyword;
			count = clubDao.getCount(ClubDao.SEARCH_CLUB_CATEGORY, catId);
			listHelper = new ListHelper<Club>(count, page, perPage);
			clubs = clubDao.getList(listHelper.getOffset(), 
					listHelper.getObjectPerPage(), ClubDao.SEARCH_CLUB_CATEGORY, catId, orderby);
			break;
			
		case LIST_RECENTLY:
			// 동아리 목록
			count = clubDao.getCount(ClubDao.SEARCH_ALL, "");
			listHelper = new ListHelper<Club>(count, page, perPage);
			clubs = clubDao.getList(listHelper.getOffset(), 
					listHelper.getObjectPerPage(), ClubDao.SEARCH_ALL, "", orderby);
			// 동아리 회원 수 가져오기
			for(Club club : clubs) {
				club.setNumberOfClubMember(
						clubDao.getClubMemberCount(club.getClubName()));
			}
			break;
			
		case LIST_ALL: default:
			count = clubDao.getCount(ClubDao.SEARCH_ALL, "");
			listHelper = new ListHelper<Club>(count, page, perPage);
			clubs = clubDao.getList(listHelper.getOffset(), 
					listHelper.getObjectPerPage(), ClubDao.SEARCH_ALL, "", orderby);
			break;
		}
		
		listHelper.setList(clubs);
		return listHelper;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** joinClub: join a club ******/
	@Override
	public boolean joinClub(String clubName, String memberName) {
		// check whether there is a existing join information
		// if join information already exists, return false 
		if(clubDao.isExistingClubMember(clubName, memberName))
			return false;
		
		if(!clubDao.isThisUserApplicant(clubName, memberName) && 
				!clubDao.isThisUserInvitee(clubName, memberName))
			return false;
		
		int result = clubDao.joinClub(clubName, memberName);
		
		if(result == 1) {
			if(clubDao.isThisUserApplicant(clubName, memberName)) {
				clubDao.deleteApplication(clubName, memberName);
			}
				
			if(clubDao.isThisUserInvitee(clubName, memberName)) {
				clubDao.deleteInvitation(clubName, memberName);
			}
			
			return true;
		}
		
		return false;
	}

	/***** leaveClub: leave a club ******/
	@Override
	public boolean leaveClub(String clubName, String memberName) {
		// check whether there is a existing join information				
		// if join information does not exists, return false 
		if(!clubDao.isExistingClubMember(clubName, memberName))
			return false;
		
		int result = clubDao.leaveClub(clubName, memberName);
		
		if(result == 1)
			return true;
		
		return false;
	}
	
	/***** applyToClub ******/
	@Override
	public boolean applyToClub(String clubName, String userName, String comment) {		
		if(clubDao.isExistingClubMember(clubName, userName) ||
				clubDao.isThisUserApplicant(clubName, userName) || 
				clubDao.isThisUserInvitee(clubName, userName))
			return false;
		
		int result = clubDao.insertClubApplication(clubName, userName, comment);
		
		if(result == 1)
			return true;
		
		return false;
	}
	
	/***** inviteUserToClub ******/
	@Override
	public boolean inviteUserToClub(String clubName, String userName,
			String comment) {
		
		if(clubDao.isExistingClubMember(clubName, userName) ||
				clubDao.isThisUserInvitee(clubName, userName))
			return false;
		
		int result = clubDao.insertClubInvitation(clubName, userName, comment);
		
		if(result == 1)
			return true;
		
		return false;
	}
	
	/***** checkClubMemberCount ******/
	public boolean checkClubMemberCount(String clubName) {
		Club club = clubDao.get(clubName);
		
		int clubMemberMaxNum = club.getMaxMemberNum();
		int clubMemberCurrentNum = clubDao.getClubMemberCount(clubName);
		
		if(clubMemberCurrentNum < clubMemberMaxNum)
			return true;
		
		return false;
	}
	
	
	/***** cancelApplication ******/
	@Override
	public boolean cancelApplication(String clubName, String userName) {
		if(clubDao.isThisUserApplicant(clubName, userName)) {
			int result = clubDao.deleteApplication(clubName, userName);
			
			if(result == 1)
				return true;
		}
		
		return false;
	}
	
	/***** cancelInvitation ******/
	@Override
	public boolean cancelInvitation(String clubName, String userName) {
		if(clubDao.isThisUserInvitee(clubName, userName)) {
			int result = clubDao.deleteInvitation(clubName, userName);
			
			if(result == 1)
				return true;
		}
		
		return false;
	}
	
	/***** isThisUserApplicant: 회원이 현재 동아리에 가입 신청 중인지 ******/
	@Override
	public boolean isThisUserApplicant(String clubName, String userName) {
		return clubDao.isThisUserApplicant(clubName, userName);
	}
	
	/***** isThisUserInvitee: 동아리에 현재 초대 받은 회원인지 ******/
	@Override
	public boolean isThisUserInvitee(String clubName, String userName) {
		return clubDao.isThisUserInvitee(clubName, userName);
	}
	
	/***** getClubJoinApplications: 동아리에 대한 가입 신청 조회 ******/
	@Override
	public ListHelper<ClubJoinInfo> getClubJoinApplications(int page,
			String clubName, int perPage) {
		int totalCount = clubDao.getClubJoinApplicationsCount(clubName);
		ListHelper<ClubJoinInfo> listHelper = 
				new ListHelper<ClubJoinInfo>(totalCount, page, perPage);
		
		listHelper.setList(clubDao.getClubJoinApplications(
				listHelper.getOffset(), listHelper.getObjectPerPage(), clubName));
		
		return listHelper;
	}
	
	/***** getClubJoinInvitations: 동아리에 대한 가입 초대 조회 ******/
	@Override
	public ListHelper<ClubJoinInfo> getClubJoinInvitations(int page,
			String clubName, int perPage) {
		int totalCount = clubDao.getClubInvitationsCount(clubName);
		ListHelper<ClubJoinInfo> listHelper = 
				new ListHelper<ClubJoinInfo>(totalCount, page, perPage);
		
		listHelper.setList(clubDao.getClubInvitations(
				listHelper.getOffset(), listHelper.getObjectPerPage(), clubName));
		
		return listHelper;
	}

	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** deleteClub: delete a club ******/
	@Override
	public boolean deleteClub(String clubName) {
		// 동아리의 모임글에 달려 있는 댓글 모두 삭제
		meetingReplyDao.deleteRepliesOfClub(clubName);
		
		// 동아리 모임글 삭제
		meetingDao.delete(clubName);
		
		// 동아리의 게시글에 달려 있는 댓글 모두 삭제
		articleReplyDao.deleteRepliesOfClub(clubName);
		
		// 동아리 게시글 삭제
		clubArticleDao.delete(clubName);
		
		// 운영진 정보 모두 삭제
		clubDao.deleteClubCrewAll(clubName);
		
		// 가입 신청/초대 정보 모두 삭제
		clubDao.deleteClubJoinInvitationAll(clubName);
		clubDao.deleteClubJoinInvitationAll(clubName);
		
		// 동아리 회원 정보 삭제
		clubDao.closeClub(clubName);
		
		// 동아리 삭제
		int result = clubDao.delete(clubName);
		
		if(result == 1)
			return true;
		
		return false;
	}

	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// club categories
	
	/***** addClubCategory: 새 카테고리 추가 ******/
	@Override
	public boolean addClubCategory(ClubCategory clubCategory) {
		int result = clubDao.addClubCategory(clubCategory);
		
		if(result == 1)
			return true;
		
		return false;
	}

	/***** getClubGrandCategories: 최상위 카테고리 조회 ******/
	@Override
	public List<ClubCategory> getClubGrandCategories() {
		return clubDao.getClubGrandCategories();
	}

	/***** getClubChildCategories: 하위 카테고리 조회 ******/
	@Override
	public List<ClubCategory> getClubChildCategories(int parentCategoryId) {
		return clubDao.getClubChildCategories(parentCategoryId);
	}

	/***** getClubCategory: 카테고리 정보 조회 ******/
	@Override
	public ClubCategory getClubCategory(int categoryId) {
		return clubDao.getClubCategory(categoryId);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// club crew
	
	/***** appointClubCrew: 카테고리 정보 조회 ******/
	@Override
	public boolean appointClubCrew(String clubName, String userName) {
		int result = clubDao.insertClubCrew(userName, clubName);
		
		if(result == 1)
			return true; 
		return false;
	}

	/***** getClubCategory: 카테고리 정보 조회 ******/
	@Override
	public boolean dismissClubCrew(String clubName, String userName) {
		int result = clubDao.deleteClubCrew(userName, clubName);
		
		if(result == 1)
			return true; 
		return false;
	}

	/***** getClubCategory: 카테고리 정보 조회 ******/
	@Override
	public ListHelper<User> getClubCrews(int page, String clubName, int perPage) {
		int totalCount = clubDao.getClubCrewCount(clubName);
		
		ListHelper<User> listHelper = new ListHelper<User>(
				totalCount, page, 20);
		
		List<User> crews = clubDao.getClubCrews(
				clubName, listHelper.getOffset(), listHelper.getObjectPerPage());
		
		listHelper.setList(crews);
		
		return listHelper;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// search
	
	/***** searchClubMember: 동아리 멤버 검색 ******/
	@Override
	public List<User> searchClubMember(String keyword, String clubName,
			int searchType) {
		return clubDao.searchClubMember(keyword, clubName, searchType);
	}

	/***** searchClubCrew: 동아리 운영진 조회 ******/
	@Override
	public List<User> searchClubCrew(String keyword, String clubName,
			int searchType) {
		return clubDao.searchClubCrew(
				keyword, clubName, searchType);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	///////// transfer master
	
	/***** transferMaster: 동아리 마스터 권한 양도 ******/
	@Override
	public boolean transferMaster(String clubName, String newMasterName) {
		int result = clubDao.transferMaster(clubName, newMasterName);
		
		if(result == 1)
			return true;
		
		return false;
	}
}
