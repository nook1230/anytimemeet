package com.mamascode.dao.mybatis;

/****************************************************
 * [MySQLMybatisClubDao] - ClubDao 인터페이스 구현
 * MyBatis를 이용한 Data Access Object
 * 
 * Repository: MySQL 
 * 주요 처리 테이블: clubs, club_members, club_crew 등
 * 트랜잭션 처리: Service 계층 
 * 스프링 컴포넌트(@Repository)
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mamascode.dao.ClubDao;
import com.mamascode.exceptions.UpdateResultCountNotMatchException;
import com.mamascode.model.Club;
import com.mamascode.model.ClubCategory;
import com.mamascode.model.ClubJoinInfo;
import com.mamascode.model.User;

@Repository
public class MySQLMybatisClubDao implements ClubDao {
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// SqlSessionTemplate and data source
	@Autowired private SqlSessionTemplate sqlSessionTemplate;
	@Autowired private DataSource dataSource;
	
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// constant
	private final String NAMESPACE = "com.mamascode.mybatis.mapper.ClubMapper";
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// constructors(default)
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// util
	
	///// getMapperId: 맵퍼의 SQL 아이디와 네임스페이스를 연결해줌 
	private String getMapperId(String mapperId) {
		return String.format("%s.%s", NAMESPACE, mapperId);
	}
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// implemented methods
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// club creation
	
	/***** create: create a new club ******/
	@Override
	public int create(Club club) {
		// parameter filtering
		if(!checkClubParameters(club) || isExistingClubName(club.getClubName()))
			return 0;
		
		int result = sqlSessionTemplate.insert(getMapperId("insertNewClub"), club);
		
		if(result == 1) {
			// if result is 1, initiate a master user into this club  
			int resultJoin = joinClub(club.getClubName(), club.getMasterName());
			
			if(resultJoin == 1)
				// If resultJoin is 1, success all
				return resultJoin;
			else
				throw new UpdateResultCountNotMatchException("MyBatisClubDao::joinClub() result is not 1");
		} else {
			throw new UpdateResultCountNotMatchException("MyBatisClubDao::creat() result is not 1");
		}
	}
	
	/***** isExistingClubName: check if there is a club has a given name *****/
	@Override
	public boolean isExistingClubName(String clubName) {
		int result = sqlSessionTemplate.selectOne(getMapperId("checkClubName"), clubName);
		
		if(result > 0)
			return true;
		
		return false;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// activation, update, delete
	
	/***** inactivate: inactivate a club ******/
	@Override
	public int inactivate(String clubName) {
		return sqlSessionTemplate.update(getMapperId("inactivateClub"), clubName);
	}
	
	/***** activate: activate a club ******/
	@Override
	public int activate(String clubName) {
		return sqlSessionTemplate.update(getMapperId("activateClub"), clubName);
	}
	
	/***** update: update club information ******/
	@Override
	public int update(Club club) {
		int result = sqlSessionTemplate.update(getMapperId("updateClub"), club);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException("MySQLMybatisClubDao::update() result is not 1");
	}
	
	/***** delete: delete a club ******/
	@Override
	public int delete(String clubName) {
		int clubMemberCount = getClubMemberCount(clubName);
		
		int result = 0;
		int resultClose = closeClub(clubName);
		
		if(resultClose == clubMemberCount) {
			result = sqlSessionTemplate.delete(getMapperId("deleteClub"), clubName);
			if(result == 1) {
				return result;
			} else {
				throw new UpdateResultCountNotMatchException("deleting club result is not 1");
			}
		} else {
			throw new UpdateResultCountNotMatchException("club members remain");
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// count
	
	/***** getCount: get a size of the table clubs ******/
	@Override
	public int getCount(int searchby, Object keyword) {
		// 검색 조건 필터링
		if(searchby < 0 || searchby > 2)
			searchby = SEARCH_ALL;
		
		// 파라미터로 전달될 해시 맵
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("searchby", searchby);
		hashmap.put("keyword", keyword);
		
		return sqlSessionTemplate.selectOne(getMapperId("selectCount"), hashmap);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// club information
	
	/***** get: get club information ******/
	@Override
	public Club get(String clubName) {
		Club club = sqlSessionTemplate.selectOne(getMapperId("selectClubByClubName"), clubName);
		
		if(club != null)
			club.setNumberOfClubMember(getNumberOfClubMember(clubName)); // 동아리 멤버 수 가져오기
		
		if(club == null)
			club = new Club();
		
		return club;
	}
	
	/***** getNumberOfClubMember: get a number of members of a club ******/
	@Override
	public int getNumberOfClubMember(String clubName) {
		return sqlSessionTemplate.selectOne(getMapperId("selectClubMembersCount"), clubName);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// club list
	/***** getList: get a club list ******/
	@Override
	public List<Club> getList(int offset, int limit, int searchby,
			Object keyword, int orderby) {
		// 검색 조건 필터링
		if(searchby < 0 || searchby > 2)
			searchby = SEARCH_ALL;
		
		if(orderby < 0 || orderby > 5)
			orderby = ORDER_DEFAULT;
		
		// offset과 limit 설정(by RowBounds)
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		// 파라미터로 전달할 해시 맵
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("searchby", searchby);
		hashmap.put("keyword", keyword);
		hashmap.put("orderby", orderby);
		
		return sqlSessionTemplate.selectList(getMapperId("selectList"), hashmap, rowBounds);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// user clubs
	
	/***** getCountUserClubs: get a number of clubs of a specified user ******/
	@Override
	public int getCountUserClubs(String userName) {
		return sqlSessionTemplate.selectOne(getMapperId("selectCountUserClubs"), userName);
	}
	
	/***** getUserClubs: get a list of a user's clubs(filtered) ******/
	@Override
	public List<Club> getUserClubs(int offset, int limit, String userName) {
		RowBounds rowBounds = new RowBounds(offset, limit);
		return sqlSessionTemplate.selectList(getMapperId("selectUserClubs"), userName, rowBounds);
	}
	
	/***** getUserClubs: get a list of a user's clubs(full) ******/
	@Override
	public List<Club> getUserClubs(String userName) {
		return sqlSessionTemplate.selectList(getMapperId("selectUserClubs"), userName);
	}

	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// 동아리 가입
	
	/***** insertClubApplication ******/
	@Override
	public int insertClubApplication(String clubName, String userName,
			String comment) {
		ClubJoinInfo clubJoinInfo = new ClubJoinInfo(clubName, "", userName, comment);
		return sqlSessionTemplate.insert(getMapperId("insertJoinApplication"), clubJoinInfo);
	}
	
	/***** insertClubInvitation ******/
	@Override
	public int insertClubInvitation(String clubName, String userName,
			String comment) {
		ClubJoinInfo clubJoinInfo = new ClubJoinInfo(clubName, "", userName, comment);
		return sqlSessionTemplate.insert(getMapperId("insertJoinInvitation"), clubJoinInfo);
	}
	
	/***** isThisUserApplicant: check if a user already apply to this club  ******/
	@Override
	public boolean isThisUserApplicant(String clubName, String userName) {
		// 파라미터로 전달할 해시 맵
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("clubName", clubName);
		hashmap.put("userName", userName);
		
		int result = sqlSessionTemplate.selectOne(getMapperId("checkClubJoinApplication"), hashmap);
		
		if(result > 0)
			return true;
		
		return false;
	}
	
	/***** isThisUserInvitee: check if a user has been invited to this club ******/
	@Override
	public boolean isThisUserInvitee(String clubName, String userName) {
		// 파라미터로 전달할 해시 맵
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("clubName", clubName);
		hashmap.put("userName", userName);
		
		int result = sqlSessionTemplate.selectOne(getMapperId("checkClubInvitation"), hashmap);
		
		if(result > 0)
			return true;
		
		return false;
	}
	
	/***** deleteClubJoinApplication: 가입 신청 정보 삭제 ******/
	@Override
	public int deleteApplication(String clubName, String userName) {
		// 파라미터로 전달할 해시 맵
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("clubName", clubName);
		hashmap.put("userName", userName);
		
		int result = sqlSessionTemplate.delete(getMapperId("deleteClubJoinApplication"), hashmap);
		
		if(result == 1)
			return result;
		else // for transaction roll-back
			throw new UpdateResultCountNotMatchException(
					"MySQLMybatisClubDao::deleteApplication() result is not 1");
	}
	
	/***** deleteInvitation: 가입 초대 정보 삭제 ******/
	@Override
	public int deleteInvitation(String clubName, String userName) {
		// 파라미터로 전달할 해시 맵
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("clubName", clubName);
		hashmap.put("userName", userName);
		
		int result = sqlSessionTemplate.delete(getMapperId("deleteClubJoinInvitation"), hashmap);
		
		if(result == 1)
			return result;
		else // for transaction roll-back
			throw new UpdateResultCountNotMatchException(
					"MySQLMybatisClubDao::deleteInvitation() result is not 1");
	}
	
	/***** isExistingClubMember: check if a user is a member of a club ******/
	@Override
	public boolean isExistingClubMember(String clubName, String memberName) {
		// 파라미터로 전달할 해시 맵
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("clubName", clubName);
		hashmap.put("memberName", memberName);
		
		int result = sqlSessionTemplate.selectOne(getMapperId("checkClubMember"), hashmap);
		
		if(result > 0)
			return true;
		
		return false;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	
	/***** joinClub: 동아리 가입 처리 ******/
	@Override
	public int joinClub(String clubName, String memberName) {
		// 세션에서 파라미터로 사용할 Hashmap 생성 및 원소 삽입
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("clubName", clubName);
		hashmap.put("memberName", memberName);
		
		int result = sqlSessionTemplate.insert(getMapperId("insertClubMember"), hashmap);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException("MyBatisClubDao::joinClub() result is not 1");
	}
	
	/***** leaveClub: 동아리 탈퇴 ******/
	@Override
	public int leaveClub(String clubName, String memberName) {
		// 세션에서 파라미터로 사용할 Hashmap 생성 및 원소 삽입
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("clubName", clubName);
		hashmap.put("memberName", memberName);
		hashmap.put("masterName", memberName);
		
		// 탈퇴하려는 멤버가 동아리 마스터인지 확인
		// 동아리 마스터라면 그대로 리턴한다: 실패
		if(isThisUserClubMaster(memberName, clubName))
			return 0;
		
		// 탈퇴 처리
		int result = sqlSessionTemplate.delete(getMapperId("deleteClubMember"), hashmap);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException("MyBatisClubDao::leaveClub() result is not 1");
	}
	
	/***** closeClub: clear member information of a club ******/
	@Override
	public int closeClub(String clubName) {
		return sqlSessionTemplate.delete(getMapperId("deleteClubMembersAll"), clubName);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// club member
	
	/***** getClubMemberCount: get a number of club members ******/
	@Override
	public int getClubMemberCount(String clubName) {
		return sqlSessionTemplate.selectOne(getMapperId("selectCountClubMembers"), clubName);
	}
	
	/***** getClubMemberList: get all the names of club members ******/
	@Override
	public List<String> getClubMemberList(String clubName) {
		return sqlSessionTemplate.selectList(getMapperId("selectClubMemberNames"), clubName);
	}

	/***** getClubMemberList: get club members ******/
	@Override
	public List<User> getClubMemberList(int offset, int limit, String clubName) {
		// offset과 limit 설정(by RowBounds)
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		return sqlSessionTemplate.selectList(getMapperId("selectClubMembers"), clubName, rowBounds);
	}

	//////////////////////////////////////////////////////////////////////////////
	// 동아리 가입 신청과 초대에 대한 리스트
	
	/***** getClubJoinApplications ******/
	@Override
	public List<ClubJoinInfo> getClubJoinApplications(int offset, int limit,
			String clubName) {
		// offset과 limit 설정(by RowBounds)
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		return sqlSessionTemplate.selectList(
				getMapperId("selectClubJoinApplicationList"), clubName, rowBounds);
	}
	
	/***** getClubJoinApplicationsCount ******/
	@Override
	public int getClubJoinApplicationsCount(String clubName) {
		return sqlSessionTemplate.selectOne(getMapperId("selectCountClubJoinApplication"), clubName);
	}
	
	/***** getClubInvitations ******/
	@Override
	public List<ClubJoinInfo> getClubInvitations(int offset, int limit,
			String clubName) {
		// offset과 limit 설정(by RowBounds)
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		return sqlSessionTemplate.selectList(
				getMapperId("selectClubJoinInvitationList"), clubName, rowBounds);
	}
	
	/***** getClubInvitationsCount ******/
	@Override
	public int getClubInvitationsCount(String clubName) {
		return sqlSessionTemplate.selectOne(getMapperId("selectCountClubJoinInvitation"), clubName);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// club master + club crews
	
	/***** isThisUserClubMaster: check if this user is a master of a club ******/
	@Override
	public boolean isThisUserClubMaster(String userName, String clubName) {
		// 세션에서 파라미터로 사용할 Hashmap 생성 및 원소 삽입
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("clubName", clubName);
		hashmap.put("userName", userName);
		
		int result = sqlSessionTemplate.selectOne(getMapperId("checkClubMaster"), hashmap);
		
		if(result > 0)
			return true;
		
		return false;
	}
	
	/***** isThisUserClubCrew: check if this user is a crew of a club ******/
	@Override
	public boolean isThisUserClubCrew(String userName, String clubName) {
		// 세션에서 파라미터로 사용할 Hashmap 생성 및 원소 삽입
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("clubName", clubName);
		hashmap.put("userName", userName);
		
		int result = sqlSessionTemplate.selectOne(getMapperId("checkClubCrew"), hashmap);
		
		if(result > 0)
			return true;
		
		return false;
	}
	
	/***** insertClubCrew: add a club crew ******/
	@Override
	public int insertClubCrew(String userName, String clubName) {
		// 세션에서 파라미터로 사용할 Hashmap 생성 및 원소 삽입
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("clubName", clubName);
		hashmap.put("userName", userName);
		
		int result = sqlSessionTemplate.insert(getMapperId("insertClubCrew"), hashmap);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException(
					"MyBatisClubDao::insertClubCrew() result is not 1");
	}
	
	/***** deleteClubCrew: delete a club crew ******/
	@Override
	public int deleteClubCrew(String userName, String clubName) {
		// 세션에서 파라미터로 사용할 Hashmap 생성 및 원소 삽입
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("clubName", clubName);
		hashmap.put("userName", userName);
		
		int result = sqlSessionTemplate.delete(getMapperId("deleteClubCrew"), hashmap);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException(
					"MyBatisClubDao::deleteClubCrew() result is not 1");
	}
	
	/***** getClubCrews: get a list of club crews ******/
	@Override
	public List<User> getClubCrews(String clubName, int offset, int limit) {
		// offset과 limit 설정(by RowBounds)
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		return sqlSessionTemplate.selectList(getMapperId("selectClubCrewList"), clubName, rowBounds);
	}
	
	/***** getClubCrewCount ******/
	@Override
	public int getClubCrewCount(String clubName) {
		return sqlSessionTemplate.selectOne(getMapperId("selectClubCrewCount"), clubName);
	}
	
	/***** getClubNameForMasterUser ******/
	@Override
	public List<String> getClubNameForMasterUser(String userName) {
		return sqlSessionTemplate.selectList(getMapperId("selectMasterClubName"), userName);
	}
	
	/***** getClubNameForClubCrewUser ******/
	@Override
	public List<String> getClubNameForClubCrewUser(String userName) {
		return sqlSessionTemplate.selectList(getMapperId("selectCrewClubName"), userName);
	}
	
	/***** getClubNameForUser ******/
	@Override
	public List<String> getClubNameForUser(String userName) {
		return sqlSessionTemplate.selectList(getMapperId("selectUserClubName"), userName);
	}	

	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// 회원 탈퇴를 위한 정리 메소드들
	
	/***** deleteClubCrew: 운영진 정보 삭제 ******/
	@Override
	public int deleteClubCrew(String userName) {
		return sqlSessionTemplate.delete(
				getMapperId("deleteClubCrewForUser"), userName);
	}
	
	/***** deleteClubJoinApplication: 가입 신청 정보 삭제 ******/
	@Override
	public int deleteClubJoinApplication(String userName) {
		return sqlSessionTemplate.delete(
				getMapperId("deleteClubJoinApplicationForUser"), userName);
	}
	
	/***** deleteClubJoinInvitation: 가입 초대 정보 삭제 ******/
	@Override
	public int deleteClubJoinInvitation(String userName) {
		return sqlSessionTemplate.delete(
				getMapperId("deleteClubJoinInvitationForUser"), userName);
	}
	
	/***** deleteClubMemberForUser: 동아리 가입 정보 삭제 ******/
	@Override
	public int deleteClubMemberForUser(String userName) {
		return sqlSessionTemplate.delete(
				getMapperId("deleteClubMemberForUser"), userName);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// search(club member & club crews)
	
	/***** searchClubMember: search club members ******/
	@Override
	public List<User> searchClubMember(String keyword, String clubName,
			int searchType) {
		// 세션에서 파라미터로 사용할 Hashmap 생성 및 원소 삽입
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("clubName", clubName);
		hashmap.put("searchType", searchType);
		hashmap.put("keyword", keyword);
		
		return sqlSessionTemplate.selectList(
				getMapperId("selectClubMemberListSearch"), hashmap);
	}
	
	/***** searchClubMemberCount: get a number of rows that correspond to a keyword ******/
	@Override
	public int searchClubMemberCount(String keyword, String clubName,
			int searchType) {
		// 세션에서 파라미터로 사용할 Hashmap 생성 및 원소 삽입
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("clubName", clubName);
		hashmap.put("searchType", searchType);
		hashmap.put("keyword", keyword);

		return sqlSessionTemplate.selectOne(
				getMapperId("selectClubMemberCountSearch"), hashmap);
	}
	
	/***** searchClubCrew: search club crews ******/
	@Override
	public List<User> searchClubCrew(String keyword, String clubName,
			int searchType) {
		// 세션에서 파라미터로 사용할 Hashmap 생성 및 원소 삽입
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("clubName", clubName);
		hashmap.put("searchType", searchType);
		hashmap.put("keyword", keyword);
		
		return sqlSessionTemplate.selectList(
				getMapperId("selectClubCrewListSearch"), hashmap);
	}
	
	/***** searchClubCrewCount: get a number of rows that correspond to a keyword ******/
	@Override
	public int searchClubCrewCount(String keyword, String clubName,
			int searchType) {
		// 세션에서 파라미터로 사용할 Hashmap 생성 및 원소 삽입
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("clubName", clubName);
		hashmap.put("searchType", searchType);
		hashmap.put("keyword", keyword);

		return sqlSessionTemplate.selectOne(
				getMapperId("selectClubCrewCountSearch"), hashmap);
	}
	
	/***** transferMaster: 동아리 마스터 권한 양도 ******/
	@Override
	public int transferMaster(String clubName, String newMasterName) {
		// 세션에서 파라미터로 사용할 Hashmap 생성 및 원소 삽입
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("clubName", clubName);
		hashmap.put("newMasterName", newMasterName);
		
		int result = sqlSessionTemplate.update(getMapperId("transferMaster"), hashmap);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException(
					"MySQLMybatisClubDao::transferMaster() result is not 1");
	}

	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// club category
	
	/***** addClubCategory ******/
	@Override
	public int addClubCategory(ClubCategory clubCategory) {
		return sqlSessionTemplate.insert(
				getMapperId("insertNewClubCategory"), clubCategory);
	}
	
	/***** getClubGrandCategories: 최상위 카테고리 조회 ******/
	@Override
	public List<ClubCategory> getClubGrandCategories() {
		return sqlSessionTemplate.selectList(getMapperId("selectGrandCategories"));
	}
	
	/***** getClubChildCategories: 자식 카테고리 조회 ******/
	@Override
	public List<ClubCategory> getClubChildCategories(int parentCategoryId) {
		return sqlSessionTemplate.selectList(
				getMapperId("selectClubCategories"), parentCategoryId);
	}
	
	/***** getClubCategory ******/
	@Override
	public ClubCategory getClubCategory(int categoryId) {
		return sqlSessionTemplate.selectOne(
				getMapperId("selectClubCategory"), categoryId);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// delete club: 동아리 폐쇄 시에 삭제될 부가 정보들 - 운영진, 가입/초대 정보
	
	/***** deleteClubCrewAll ******/
	@Override
	public int deleteClubCrewAll(String clubName) {
		return sqlSessionTemplate.delete(getMapperId("deleteClubCrewAll"), clubName);
	}
	
	/***** deleteClubJoinApplicationAll ******/
	@Override
	public int deleteClubJoinApplicationAll(String clubName) {
		return sqlSessionTemplate.delete(getMapperId("deleteClubJoinApplicationAll"), clubName);
	}
	
	/***** deleteClubJoinInvitationAll ******/
	@Override
	public int deleteClubJoinInvitationAll(String clubName) {
		return sqlSessionTemplate.delete(getMapperId("deleteClubJoinInvitationAll"), clubName);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// temporary
	@Override
	public int getLastInsertId() {
		return sqlSessionTemplate.selectOne(getMapperId("selectLastInsertId"));
	}

	@Override
	public int getMaxClubNo() {
		return sqlSessionTemplate.selectOne(getMapperId("selectMaxClubNo"));
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// utils
	
	/* checkClubParameters: MySQL은 빈 문자열을 null로 인식하지 않기 때문에 여기서 필터링 처리해준다 */
	private boolean checkClubParameters(Club club) {
		if(!club.getClubName().equals("") && !club.getClubTitle().equals("") &&
				!club.getMasterName().equals(""))
			return true;
		
		return false;
	}
	
	/////////// for a unit test ///////////
	/***** deleteAll: delete all clubs   ******/
	/*************************************************
	 * DO NOT USE this method except for TEST!
	 * 
	 * If you reference this class 
	 * by type (I)ClubDao (not MySQLMybatisClubDao),
	 * this method is invisible to you 
	 * and your Database may be safe 
	 * from unintended deleting data :D
	 **************************************************/
	public int testDeleteClub(String clubName) {
		return sqlSessionTemplate.delete(getMapperId("testDeleteClub"), clubName);
	}
	
	public int testDeleteClubMembers(String clubName) {
		return sqlSessionTemplate.delete(getMapperId("testDeleteClubMemberAll"), clubName);
	}

}
