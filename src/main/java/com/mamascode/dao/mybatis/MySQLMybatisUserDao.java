package com.mamascode.dao.mybatis;

/****************************************************
 * [MySQLMybatisUserDao] - UserDao 인터페이스 구현
 * MyBatis를 이용한 Data Access Object
 * 
 * Repository: MySQL 
 * 주요 처리 테이블: users
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
import java.util.Random;

import javax.sql.DataSource;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mamascode.dao.UserDao;
import com.mamascode.exceptions.UpdateResultCountNotMatchException;
import com.mamascode.model.ClubJoinInfo;
import com.mamascode.model.User;

@Repository
public class MySQLMybatisUserDao implements UserDao {
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
	private final String NAMESPACE = "com.mamascode.mybatis.mapper.UserMapper";
	
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
	/***** create: create a new user account ******/
	@Override
	public int create(User user) {
		// 파라미터 검정
		if(!checkRequiredColumn(user) || isExistingEmail(user.getEmail()) ||
				isExistingUserName(user.getUserName()))
			return 0;
		
		// 이메일 인증키 생성
		user.setCertificationKey(makeCertificationKey());
		
		int result = sqlSessionTemplate.insert(getMapperId("insertNewUser"), user);
		
		if(result == 1) {
			int activate = activate(user.getUserName());
			
			if(activate == 1) {
				return result;
			} else {
				throw new UpdateResultCountNotMatchException("can't activate a new user account!");
			}
		} else {
			throw new UpdateResultCountNotMatchException(
					"There is something wrong in creating a new account :(");
		}
	}
	
	// makeCertificationKey
	private String makeCertificationKey() {
		Random rand = new Random();
		StringBuilder builder = new StringBuilder();
		boolean isInteger;
		
		for(int i = 0; i < 20; i++) {
			int randNum;
			
			isInteger = rand.nextBoolean();
			
			if(isInteger) {
				// integer
				randNum = rand.nextInt(10);
				builder.append(randNum);
			} else {
				// alphabet
				randNum = rand.nextInt(26) + 97;
				builder.append((char) randNum);
			}
		}
			
		return builder.toString();
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** inactive: inactivate a user account ******/
	@Override
	public int inactive(String userName) {
		return sqlSessionTemplate.update(getMapperId("inactivateUserAccount"), userName);
	}
	
	/***** activate: activate a user account ******/
	@Override
	public int activate(String userName) {
		return sqlSessionTemplate.update(getMapperId("activateUserAccount"), userName);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////	
	/***** update: update user account information(optional) ******/
	@Override
	public int update(User user) {
		int result = sqlSessionTemplate.update(getMapperId("updateUserAccount"), user);
		
		if(result == 1) {
			return result;
		} else {
			throw new UpdateResultCountNotMatchException("MyBatisUserDao.update(User user) result is not 1");
		}
	}
	
	/***** delete: delete a user account ******/
	@Override
	public int delete(String userName) {
		int result = sqlSessionTemplate.delete(getMapperId("deleteUserAccount"), userName);
		
		if(result == 1) {
			return result;
		} else {
			throw new UpdateResultCountNotMatchException("MyBatisUserDao.delete(String userName) result is not 1");
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** getCount: get a size of rows of users table ******/
	@Override
	public int getCount() {
		return sqlSessionTemplate.selectOne(getMapperId("selectCountDefault"));
	}
	
	/***** getCount: get a size of rows of users table(check user name) ******/
	@Override
	public int getCount(String userName) {
		return sqlSessionTemplate.selectOne(getMapperId("selectCountCheckUserName"), userName);
	}
	
	/***** getCount: get a size of rows of users table(check email) ******/
	@Override
	public int getCountEmail(String email) {
		return sqlSessionTemplate.selectOne(getMapperId("selectCountCheckEmail"), email);
	}

	/***** getCount: get a size of rows of users table(search) ******/
	@Override
	public int getCount(int searchby, String keyword) {
		// 파라미터 searchby 검정
		if(searchby < 1 || searchby > 4)
			searchby = 4;
		
		// 세션에서 파라미터로 사용할 Hashmap 생성 및 원소 삽입
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("searchby", searchby);
		hashmap.put("keyword", keyword);
		
		return sqlSessionTemplate.selectOne(getMapperId("selectCountSearch"), hashmap);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** isExistingUserName: check if there is a user has a given name ******/
	@Override
	public boolean isExistingUserName(String userName) {
		int result = sqlSessionTemplate.selectOne(getMapperId("userNameCheck"), userName);
		if(result > 0)
			return true;
		
		return false;
	}
	
	/***** isExistingEmail: check if there is a user has a given email address ******/
	@Override
	public boolean isExistingEmail(String email) {
		int result = sqlSessionTemplate.selectOne(getMapperId("emailCheck"), email);
		if(result > 0)
			return true;
		
		return false;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** getByUserNo: get information of a user indexed a userNo ******/
	@Override
	public User getByUserNo(int userNo) {
		User user = sqlSessionTemplate.selectOne(getMapperId("selectUserByNo"), userNo);
		
		if(user != null) {
			user.setApplyingClubs(getApplyingClubs(user.getUserName()));
			user.setInvitedClubs(getInvitedClubs(user.getUserName()));
		}
		
		if(user == null)
			user = new User();
		
		return user;
	}
	
	/***** getByUserName: get information of a user who has user name as userName ******/
	@Override
	public User getByUserName(String userName) {
		User user = sqlSessionTemplate.selectOne(getMapperId("selectUserByName"), userName);
		
		if(user != null) {
			user.setApplyingClubs(getApplyingClubs(user.getUserName()));
			user.setInvitedClubs(getInvitedClubs(user.getUserName()));
		}
		
		if(user == null)
			user = new User();
		
		return user;
	}
	
	/* get a list of user's club application and invitation information */
	/* getApplyingClubs, getInvitedClubs */
	@Override
	public List<ClubJoinInfo> getApplyingClubs(String userName) {
		return sqlSessionTemplate.selectList(getMapperId("selectApplyingClubs"), userName);
	}
	
	@Override
	public List<ClubJoinInfo> getInvitedClubs(String userName) {
		return sqlSessionTemplate.selectList(getMapperId("selectInvitedClubs"), userName);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** getList: get a list for users ******/
	@Override
	public List<User> getList(int offset, int limit) {
		// 레코드 한정을 위한 RowBounds 객체
		RowBounds rowBounds = new RowBounds(offset, limit);
		return sqlSessionTemplate.selectList(getMapperId("selectUserListDefault"), null, rowBounds);
	}
	
	/***** getList: get a list for users(search) ******/
	@Override
	public List<User> getList(int offset, int limit, int searchby,
			String keyword) {
		// 파라미터 searchby 검정
		if(searchby < 1 || searchby > 4)
			searchby = 4;
		
		// 세션에서 파라미터로 사용할 Hashmap 생성 및 원소 삽입
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("searchby", searchby);
		hashmap.put("keyword", keyword);
		
		RowBounds rowBounds = new RowBounds(offset, limit);
		return sqlSessionTemplate.selectList(getMapperId("selectUserListSearch"), hashmap, rowBounds);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** isValidLogin: check if user input for login is valid(userNo) ******/
	@Override
	public int isValidLogin(int userNo, String password) {
		// 세션에서 파라미터로 사용할 Hashmap 생성 및 원소 삽입
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("userNo", userNo);
		hashmap.put("password", password);
		return sqlSessionTemplate.selectOne(getMapperId("validLoginCheckUserNo"), hashmap);
	}
	
	/***** isValidLogin: check if user input for login is valid(userName) ******/
	@Override
	public int isValidLogin(String userName, String password) {
		// 세션에서 파라미터로 사용할 Hashmap 생성 및 원소 삽입
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("userName", userName);
		hashmap.put("password", password);
		return sqlSessionTemplate.selectOne(getMapperId("validLoginCheckUserName"), hashmap);
	}
	
	/***** changePassword: change a password of a user account ******/
	@Override
	public int changePassword(String userName, String password) {
		// 파라미터 검정: 빈 문자열은 받지 않는다
		if(password.equals(""))
			return 0;
		
		// 세션에서 파라미터로 사용할 Hashmap 생성 및 원소 삽입
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("userName", userName);
		hashmap.put("password", password);
		
		int result = sqlSessionTemplate.update(getMapperId("changePassword"), hashmap);
		
		if(result == 1)
			return result;
		else {
			// 문제 발생: 트랜잭션 롤백을 위해 런타임 예외를 던진다
			throw new UpdateResultCountNotMatchException(
					"UserDao::changePassword(), There is something wrong in changing a password :(");
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////	
	/***** certifyUser: certify an user account ******/
	@Override
	public int certifyUser(String userName, String certificationKey) {
		// 이미 인증된 사용자라면 0을 리턴
		if(isCertified(userName))
			return 0;
		
		// 세션에서 파라미터로 사용할 Hashmap 생성 및 원소 삽입
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("userName", userName);
		hashmap.put("certificationKey", certificationKey);
		
		// 유효한 인증 요청인지 확인
		int result = sqlSessionTemplate.selectOne(getMapperId("validCertification"), hashmap);
		
		// 유효하다면, 인증처리 및 계정 활성화
		if(result == 1) {
			if(setCertified(userName, true) == 1 && activate(userName) == 1) {
				return result;
			} else {
				throw new UpdateResultCountNotMatchException(
							"UserDao::certifyUser(), can't certified a new user account!");
			}
		} else {
			throw new UpdateResultCountNotMatchException(
					"UserDao::certifyUser(), There is something wrong in certifying a user account :(");
		}
	}
	
	/***** isCertified ******/
	@Override
	public boolean isCertified(String userName) {
		return sqlSessionTemplate.selectOne(getMapperId("certificationCheck"), userName);
	}
	
	/***** setCertified ******/
	@Override
	public int setCertified(String userName, boolean set) {
		// 세션에서 파라미터로 사용할 Hashmap 생성 및 원소 삽입
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("userName", userName);
		hashmap.put("certified", ((set == true) ? 1 : 0));
		
		return sqlSessionTemplate.update(getMapperId("setCertification"), hashmap);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// utils
	
	/* checkRequiredColumn: check required columns */
	/************************************************
	 * MySQL은 오라클과 달리 ''을 null로 인식하지 않는다. 
	 * 기술종속적 부분으로서 Dao 구현 클래스에서 직접 필터링 처리
	 * required columns: user_name, email, passwd
	 ***********************************************/
	private boolean checkRequiredColumn(User user) {
		if(!user.getUserName().equals("") && !user.getEmail().equals("") &&
				!user.getPasswd().equals("")) {
			return true;
		}
		return false;
	}
	
	/////////// for a unit test ///////////
	/***** deleteAll: delete all user account   ******/
	/*************************************************
	 * DO NOT USE this method except for TEST!
	 * 
	 * If you reference this class 
	 * by type (I)UserDao (not MySQLMybatisUserDao),
	 * this method is invisible to you 
	 * and your Database may be safe 
	 * from unintended deleting data :D
	 **************************************************/
	public int deleteAll() {
		return sqlSessionTemplate.delete(getMapperId("deleteAll"));
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// temporary	
	/***** getMaxUserNo ******/
	@Override
	public int getMaxUserNo() {
		return sqlSessionTemplate.selectOne(getMapperId("selectMaxUserNo"));
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// util
	
	/***** getUserNameByKeyword: 사용자 이름 검색 ******/
	@Override
	public String getUserNameByKeyword(String keyword) {
		return sqlSessionTemplate.selectOne(getMapperId("selectUserNameByKeyword"), keyword);
	}
	
}
