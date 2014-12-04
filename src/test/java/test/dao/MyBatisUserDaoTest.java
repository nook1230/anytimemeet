package test.dao;

/*********************************************
 * MyBatisUserDaoTest
 * 
 * test #1: 2014. 10. 20
 * test #2: 2014. 10. 26
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

import com.mamascode.dao.UserDao;
import com.mamascode.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/application-config.xml"})
public class MyBatisUserDaoTest {
	private static int testCount = 0;
	private static String testPass = "test";
	@Autowired private UserDao userDao;
	
	private Logger logger = LoggerFactory.getLogger(MyBatisUserDaoTest.class);
	
	/////////////////////////////////////////////////////////////////////////
	// test setup
	
	@Before
	public void setUp() {
		System.out.println("test setup complete! #" + (++testCount));
	}
	
	/////////////////////////////////////////////////////////////////////////
	// test
	@Test
	public void count() {
		assertThat(userDao.getCount(), is(4));
	}
	
	@Test
	public void searchCount() {
		assertThat(userDao.getCount(UserDao.SEARCH_USER_NAME, "mmuse"), is(2));
		assertThat(userDao.getCount(UserDao.SEARCH_NICKNAME, "Mor"), is(1));
		assertThat(userDao.getCount(UserDao.SEARCH_ALL, "1230"), is(2));
		assertThat(userDao.getCount(UserDao.SEARCH_ALL, ""), is(4));
		assertThat(userDao.getCount(UserDao.SEARCH_USER_REAL_NAME, "황인호"), is(1));
	}
	
	//@Test
	public void check() {
		assertThat(userDao.isExistingUserName("mmuse1230"), is(true));
		assertThat(userDao.isExistingEmail("nook1230@naver.com"), is(true));
		assertThat(userDao.isExistingUserName("hicks"), is(false));
		assertThat(userDao.isExistingEmail("mymail1234@mail.com"), is(false));
	}
	
	//@Test
	public void get() {
		User userGet = userDao.getByUserName("mmuse1230");
		assertThat(userGet, is(notNullValue()));
		
		printUser(userGet);
	}
	
	//@Test
	public void getByNo() {
		User userGet = userDao.getByUserNo(1);
		assertThat(userGet, is(notNullValue()));
		
		printUser(userGet);
	}
	
	//@Test
	public void getList() {
		List<User> users = userDao.getList(0, 10);
		assertThat(users.size(), is(4));
		printUsers(1, "list default", users);
		
		users = userDao.getList(0, 10, UserDao.SEARCH_USER_NAME, "mmuse");
		assertThat(users.size(), is(2));
		printUsers(2, "list search", users);
	}
	
	//@Test
	public void login() {
		assertThat(userDao.isValidLogin(4, "1111"), is(1));
		assertThat(userDao.isValidLogin("nook1230", "1111"), is(1));
		assertThat(userDao.isValidLogin("mmuse1981", "lemon81"), is(1));
	}
	
	//@Test
	public void changePassword() {
		
		if(userDao.isValidLogin("test_user", "test") == 1)
			testPass = "test_man";
		else
			testPass= "test";
			
		assertThat(userDao.changePassword("test_user", testPass), is(1));
		assertThat(userDao.isValidLogin("test_user", testPass), is(1));
		assertThat(userDao.changePassword("test_user", ""), is(0));
	}
 	
	//@Test
	public void createAndDelete() {
		create();
		delete();
	}
	
	//@Test
	public void update() {
		create();
		updateInternal();
		delete();
	}
	
	private void create() {
		int count = userDao.getCount();
		
		User user = new User();
		user.setUserName("test_man");
		user.setPasswd("1111");
		user.setEmail("test_man@testmail.com");
		
		assertThat(userDao.create(user), is(1));
		assertThat(userDao.getCount(), is(count + 1));
	}
	
	//@Test
	public void certi() {
		User testUser = userDao.getByUserName("test_user");
		
		assertThat(userDao.certifyUser("test_user", testUser.getCertificationKey()), is(1));
		
		testUser = userDao.getByUserName("test_user");
		assertThat(testUser.isCertified(), is(true));
	}
	
	private void delete() {
		int count = userDao.getCount();
		
		assertThat(userDao.delete("test_man"), is(1));
		assertThat(userDao.getCount(), is(count - 1));
	}
	
	private void updateInternal() {
		User userUpdate = userDao.getByUserName("test_man");
		userUpdate.setNickname("test_guy");
		
		assertThat(userDao.update(userUpdate), is(1));
		
		User userGet = userDao.getByUserName("test_man");
		assertThat(userGet.getNickname(), is("test_guy"));
	}
	
	/* printUser: print user information in a list of users */
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
		logger.info("");
	}
}
