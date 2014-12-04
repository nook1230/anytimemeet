package test.dao;

/*********************************************
 * MyBatisClubDaoTest
 * 
 * test #1: 2014. 10. 26 [Error]
 * test #2: 2014. 10. 27 - count, createTEST, get,
 * 						   getNew, updateTEST
 * 			전체 레코드 수 조회, 동아리 생성/삭제, 
 * 			동아리 정보 조회, 동아리 정보 수정
 * test #3: 2014. 10. 27 - list, userClubs
 * 			동아리 리스트 조회, 사용자의 가입 동아리 조회
 * test #4: 2014. 10. 28 - clubMembers, checkClubMaster,
 * 						clubCrews, insertAndDeleteCrew
 * 			동아리 회원 리스트, 동아리 운영진 관련 테스트
 *********************************************/

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mamascode.dao.ClubDao;
import com.mamascode.dao.mybatis.MySQLMybatisClubDao;
import com.mamascode.model.Club;
import com.mamascode.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/application-config.xml"})
public class MyBatisClubDaoTest {
	private static int testCount = 0;
	@Autowired private ClubDao clubDao;
	MySQLMybatisClubDao testClubDao;
	
	private Logger logger = LoggerFactory.getLogger(MyBatisClubDaoTest.class);
	
	/////////////////////////////////////////////////////////////////////////
	// test setup
	
	@Before
	public void setUp() {
		testClubDao = (MySQLMybatisClubDao) clubDao;
		System.out.println("test setup complete! #" + (++testCount));
	}
	
	/////////////////////////////////////////////////////////////////////////
	// test
	//@Test
	public void count() {
		assertThat(clubDao.getCount(ClubDao.SEARCH_ALL, ""), is(1));
		assertThat(clubDao.getCount(ClubDao.SEARCH_CLUB_NAME, "test"), is(1));
		assertThat(clubDao.getCount(ClubDao.SEARCH_CLUB_NAME,"test_club"), is(0));
		assertThat(clubDao.getCount(ClubDao.SEARCH_CLUB_CATEGORY, (short) 6), is(1));
		assertThat(clubDao.getCount(ClubDao.SEARCH_CLUB_CATEGORY, (short) 7), is(1));
		assertThat(clubDao.getCount(ClubDao.SEARCH_CLUB_CATEGORY, (short) 1), is(0));
	}
	
	//@Test
	public void list() {
		create();
		/*
		List<Club> clubList = clubDao.getList(0, 10, ClubDao.SEARCH_ALL, null, true);
		assertThat(clubList, is(notNullValue()));
		assertThat(clubList.size(), is(3));
		printClubs(1, "club list (default)", clubList);
		
		clubList = clubDao.getList(0, 10, ClubDao.SEARCH_CLUB_NAME, "test", false);
		assertThat(clubList, is(notNullValue()));
		assertThat(clubList.size(), is(3));
		printClubs(2, "club list (name search 1)", clubList);
		
		clubList = clubDao.getList(0, 10, ClubDao.SEARCH_CLUB_NAME, "test_", false);
		assertThat(clubList, is(notNullValue()));
		assertThat(clubList.size(), is(2));
		printClubs(3, "club list (name search 2)", clubList);
		
		clubList = clubDao.getList(0, 10, ClubDao.SEARCH_CLUB_NAME, "mmuse", false);
		assertThat(clubList, is(notNullValue()));
		assertThat(clubList.size(), is(0));
		printClubs(4, "club list (name search 3)", clubList);
		
		clubList = clubDao.getList(0, 10, ClubDao.SEARCH_CLUB_CATEGORY, (short) 6, false);
		assertThat(clubList, is(notNullValue()));
		assertThat(clubList.size(), is(1));
		printClubs(5, "club list (category search 1)", clubList);
		
		clubList = clubDao.getList(0, 10, ClubDao.SEARCH_CLUB_CATEGORY, (short) 1, false);
		assertThat(clubList, is(notNullValue()));
		assertThat(clubList.size(), is(2));
		printClubs(6, "club list (category search 2)", clubList);
		
		clubList = clubDao.getList(0, 10, ClubDao.SEARCH_CLUB_CATEGORY, (short) 2, false);
		assertThat(clubList, is(notNullValue()));
		assertThat(clubList.size(), is(1));
		printClubs(7, "club list (category search 3)", clubList);
		
		clubList = clubDao.getList(0, 10, ClubDao.SEARCH_CLUB_CATEGORY, (short) 3, false);
		assertThat(clubList, is(notNullValue()));
		assertThat(clubList.size(), is(0));
		printClubs(8, "club list (category search 4)", clubList);
		*/
		delete();
	}
	
	//@Test
	public void userClubs() {
		List<Club> userClubs = clubDao.getUserClubs("mmuse1230");
		assertThat(userClubs, is(notNullValue()));
		assertThat(userClubs.size(), is(1));
		assertThat(userClubs.size(), is(clubDao.getCountUserClubs("mmuse1230")));
		printClubs(1, "user clubs 1", userClubs);
		
		userClubs = clubDao.getUserClubs("mmuse1981");
		assertThat(userClubs, is(notNullValue()));
		assertThat(userClubs.size(), is(1));
		assertThat(userClubs.size(), is(clubDao.getCountUserClubs("mmuse1981")));
		printClubs(2, "user clubs 2", userClubs);
	}
	
	//@Test
	public void clubMembers() {
		List<User> clubMembers = clubDao.getClubMemberList(0, 10, "test");
		assertThat(clubMembers, is(notNullValue()));
		assertThat(clubMembers.size(), is(3));
		assertThat(clubMembers.size(), is(clubDao.getClubMemberCount("test")));
		printUsers(1, "club members 1", clubMembers);
	}
	
	//@Test
	public void clubCrews() {
		List<User> clubCrews = clubDao.getClubCrews("test", 0, 10);
		assertThat(clubCrews, is(notNullValue()));
		assertThat(clubCrews.size(), is(1));
		assertThat(clubCrews.size(), is(clubDao.getClubCrewCount("test")));
		printUsers(1, "club crews 1", clubCrews);
	}
	
	//@Test
	public void checkClubMaster() {
		assertTrue(clubDao.isThisUserClubMaster("mmuse1230", "test"));
		assertTrue(clubDao.isThisUserClubCrew("mmuse1981", "test"));
		assertTrue(!clubDao.isThisUserClubCrew("nook1230", "test"));
	}
	
	@Test
	public void search() {
		List<User> searchUsers = clubDao.searchClubMember(
				"mmuse", "test", ClubDao.SEARCH_MEMBER_CREW_NAME);
		assertThat(searchUsers, is(notNullValue()));
		assertThat(searchUsers.size(), is(2));
		assertThat(searchUsers.size(), is(clubDao.searchClubMemberCount(
				"mmuse", "test", ClubDao.SEARCH_MEMBER_CREW_NAME)));
		printUsers(1, "search result 1 (name search: mmuse)", searchUsers);
		
		searchUsers = clubDao.searchClubMember(
				"옴마니", "test", ClubDao.SEARCH_MEMBER_CREW_NICKNAME);
		assertThat(searchUsers, is(notNullValue()));
		assertThat(searchUsers.size(), is(1));
		assertThat(searchUsers.size(), is(clubDao.searchClubMemberCount(
				"옴마니", "test", ClubDao.SEARCH_MEMBER_CREW_NICKNAME)));
		printUsers(2, "search result 2 (nickname search: 옴마니)", searchUsers);
		
		searchUsers = clubDao.searchClubMember(
				"1230", "test", ClubDao.SEARCH_MEMBER_CREW_ALL);
		assertThat(searchUsers, is(notNullValue()));
		assertThat(searchUsers.size(), is(2));
		assertThat(searchUsers.size(), is(clubDao.searchClubMemberCount(
				"1230", "test", ClubDao.SEARCH_MEMBER_CREW_ALL)));
		printUsers(3, "search result 3 (full search: 1230)", searchUsers);
		
		searchUsers = clubDao.searchClubCrew(
				"mmuse", "test", ClubDao.SEARCH_MEMBER_CREW_NAME);
		assertThat(searchUsers, is(notNullValue()));
		assertThat(searchUsers.size(), is(1));
		assertThat(searchUsers.size(), is(clubDao.searchClubCrewCount(
				"mmuse", "test", ClubDao.SEARCH_MEMBER_CREW_NAME)));
		printUsers(4, "crew search result 1 (name search: mmuse)", searchUsers);
		
		searchUsers = clubDao.searchClubCrew(
				"Mor", "test", ClubDao.SEARCH_MEMBER_CREW_NICKNAME);
		assertThat(searchUsers, is(notNullValue()));
		assertThat(searchUsers.size(), is(0));
		assertThat(searchUsers.size(), is(clubDao.searchClubCrewCount(
				"Mor", "test", ClubDao.SEARCH_MEMBER_CREW_NICKNAME)));
		printUsers(5, "crew search result 5 (nickname search: Mor)", searchUsers);
	}
	
	//@Test
	public void insertAndDeleteCrew() {
		assertThat(clubDao.insertClubCrew("nook1230", "test"), is(1));
		assertTrue(clubDao.isThisUserClubCrew("nook1230", "test"));
		assertThat(clubDao.deleteClubCrew("nook1230", "test"), is(1));
		assertTrue(!clubDao.isThisUserClubCrew("nook1230", "test"));
	}
	
	//@Test
	public void createTEST() {
		create();
		delete();
	}
	
	//@Test
	public void get() {
		Club clubGet = clubDao.get("test");
		assertThat(clubGet, is(notNullValue()));
		printClub(clubGet);
	}
	
	//@Test
	public void getNew() {
		create();
		
		Club clubGet1 = clubDao.get("test_club1");
		assertThat(clubGet1, is(notNullValue()));
		printClub(clubGet1);
		
		Club clubGet2 = clubDao.get("test_club2");
		assertThat(clubGet2, is(notNullValue()));
		printClub(clubGet2);
		
		delete();
	}
	
	//@Test
	public void updateTEST() {
		create();
		update();
		delete();
	}
	
	/////////////////////////////////////////////////////////////////////////
	// Internal methods	
	
	private void create() {
		Club club1 = new Club();
		club1.setClubName("test_club1");
		club1.setClubTitle("OMG no.1");
		club1.setGrandCategoryId((short) 1);	// sports
		club1.setMasterName("test_user");
		
		Club club2 = new Club();
		club2.setClubName("test_club2");
		club2.setClubTitle("OMG no.2");
		club2.setGrandCategoryId((short) 1);	// sports
		club2.setMasterName("test_user");
		club2.setCategoryId((short) 2);			// baseball
		
		int count = clubDao.getCount(ClubDao.SEARCH_ALL, null);
		assertThat(clubDao.create(club1), is(1));
		assertThat(clubDao.getCount(ClubDao.SEARCH_ALL, null), is(count+1));
		
		count = clubDao.getCount(ClubDao.SEARCH_ALL, null);
		assertThat(clubDao.create(club2), is(1));
		assertThat(clubDao.getCount(ClubDao.SEARCH_ALL, null), is(count+1));
	}
	
	//@Test
	public void delete() {
		assertThat(testClubDao.testDeleteClubMembers("test_club1"), is(1));
		assertThat(testClubDao.testDeleteClub("test_club1"), is(1));
		assertThat(testClubDao.testDeleteClubMembers("test_club2"), is(1));
		assertThat(testClubDao.testDeleteClub("test_club2"), is(1));
		
		assertThat(clubDao.getCount(ClubDao.SEARCH_ALL, null), is(1));
	}
	
	private void update() {
		Club clubUpdate = clubDao.get("test_club1");
		clubUpdate.setCategoryId((short) 4);
		
		assertThat(clubDao.update(clubUpdate), is(1));
		Club clubGet = clubDao.get("test_club1");
		assertThat(clubGet.getCategoryId(), is((short) 4));
		assertThat(clubGet.getCategoryTitle(), is("tenis"));
	}

	/////////////////////////////////////////////////////////////////////////
	// utils
	
	/* printClubs: print club information in a list of clubs */
	private void printClubs(int testNo, String testName, List<Club> clubs) {
		logger.info("--------------------------");
		logger.info("test #{}: {}", testNo, testName);
		logger.info("--------------------------");
		for(Club club : clubs) {
			printClub(club);
		}
		logger.info("--------------------------");
	}
	
	private void printClub(Club club) {
		logger.info("{}: {}", club.getClubNo(), club.getClubName());
		logger.info("master: {}", club.getMasterName());
		logger.info("crandCategory: {}", club.getGrandCategoryTitle());
		logger.info("category: {}", club.getCategoryTitle());
		logger.info("type: {}", club.getType());
		logger.info("max member number: {}", club.getMaxMemberNum());
		logger.info("active: {}", club.isActive());
		logger.info("recruit: {}", club.isRecruit());
		logger.info("date of created: {}", club.getDateOfCreated());
		logger.info("");
	}
	
	private void printUsers(int testNo, String testName, List<User> users) {
		logger.info("test #{}: {}", testNo, testName);
		for(User user : users) {
			printUser(user);
		}
		logger.info("--------------------------");
	}
	
	private void printUser(User user) {
		logger.info("\t[{}] {}, nick: {}", 
				user.getUserNo(), user.getUserName(), user.getNickname());
		logger.info("\temail: {}, certi_key: {}", 
				user.getEmail(), user.getCertificationKey());
		logger.info("\tactive: {}, certified: {}", 
				user.isActive(), user.isCertified());
		logger.info("\tdateOfJoin: {}", user.getDateOfJoin());
		if(user.getProfilePicture() != null 
				&& user.getProfilePicture().getFileName() != null 
				&& !user.getProfilePicture().getFileName().equals(""))
			logger.info("\tprofilePic: {}_{}", 
					user.getProfilePicture().getUserName(), user.getProfilePicture().getFileName());
		if(user.getClubCrewAppointedDate() != null)
			logger.info("\tappointed: {}", user.getClubCrewAppointedDate());
		if(user.getDateOfClubJoin() != null)
			logger.info("\tclub join: {}", user.getDateOfClubJoin());
		logger.info("");
	}
}
