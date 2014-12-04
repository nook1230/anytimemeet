package com.mamascode.dao.jdbc;

/****************************************************
 * @Deprecated
 * MySqlJdbcUserDao: implements UserDao(I)
 *
 * uses the Spring JDBC Template. 
 * handling: users
 * 트랜잭션 처리: Service tire
 * 
 * by Hwang Inho
 ****************************************************/

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.mamascode.dao.UserDao;
import com.mamascode.model.ClubJoinInfo;
import com.mamascode.model.User;
import com.mamascode.exceptions.UpdateResultCountNotMatchException;

@Deprecated
public class MySqlJdbcUserDao implements UserDao {
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// JdbcTemplate and data source
	@Autowired private JdbcTemplate jdbcTemplate;
	@Autowired private DataSource dataSource;
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// constants
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// constructors(default)
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// implemented methods
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** create: create a new user account  ******/
	@Override
	public int create(User user) {
		if(!checkRequiredColumn(user) || isExistingEmail(user.getEmail()) ||
				isExistingUserName(user.getUserName()))
			return 0;
		
		StringBuilder builder = new StringBuilder();
		
		// sql query statement
		builder.append("INSERT INTO users (user_name, passwd, email, ")
		.append("date_of_join, certification_key) ")
		.append("VALUES (?, SHA(?), ?, NOW(), ?)");			
		
		String sql = builder.toString();
		
		int result = jdbcTemplate.update(sql, user.getUserName(), user.getPasswd(),
				user.getEmail(), makeCertificationKey());
		
		// 계정 활성화(이메일 인증 기능 생략)
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
	/***** inactive: inactivate a user account  ******/
	@Override
	public int inactive(String userName) {
		return jdbcTemplate.update(
				"UPDATE users SET active = 0 WHERE user_name = ?", userName);
	}
	
	/***** activate: activate a user account  ******/
	@Override
	public int activate(String userName) {
		return jdbcTemplate.update(
				"UPDATE users SET active = 1 WHERE user_name = ?", userName);
	}
	
	/***** update: update user account information(optional)  ******/
	@Override
	public int update(User user) {
		StringBuilder builder = new StringBuilder();
		
		// sql query statement
		builder.append("UPDATE users SET nickname = ?, user_real_name = ?, ")
		.append("date_of_birth = ? WHERE user_no = ?");

		String sql = builder.toString();
		
		return jdbcTemplate.update(sql, user.getNickname(), user.getUserRealName(),
				user.getDateOfBirth(), user.getUserNo());
	}
	
	/***** delete: delete a user account  ******/
	@Override
	public int delete(String userName) {
		int result =  jdbcTemplate.update(
				"DELETE FROM users WHERE user_name = ?", userName);
		
		if(result == 1)
			return result;
		else {
			throw new UpdateResultCountNotMatchException("delete result is not 1");
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** getCount: get a size of rows of users table  ******/
	@Override
	public int getCount() {
		return jdbcTemplate.queryForInt("SELECT COUNT(*) FROM users");
	}
	
	/***** getCount: get a size of rows of users table(search)  ******/
	@Override
	public int getCount(final int searchby, final String keyword) {
		return jdbcTemplate.query(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				String sql = makeSearchSql("SELECT COUNT(*) FROM users", searchby);				
				PreparedStatement ps = conn.prepareStatement(sql);
				
				switch(searchby) {
				case SEARCH_USER_NAME:
				case SEARCH_NICKNAME:
				case SEARCH_USER_REAL_NAME:
					ps.setString(1, keyword);
					break;
				case SEARCH_ALL:
					ps.setString(1, keyword);
					ps.setString(2, keyword);
					break;
				default:
				}
				return ps;
			}
		}, new ResultSetExtractor<Integer>() {

			@Override
			public Integer extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				if(rs.next())
					return rs.getInt(1);
				
				return 0;
			}
		});
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** isExistingUserName: check if there is a user has a given name ******/
	@Override
	public boolean isExistingUserName(String userName) {
		int count = jdbcTemplate.queryForInt(
				"SELECT COUNT(*) FROM users WHERE user_name = ?", userName);
		if(count != 0)
			return true;
		
		return false;
	}
	
	/***** isExistingEmail: check if there is a user has a given email address ******/
	@Override
	public boolean isExistingEmail(String email) {
		int count = jdbcTemplate.queryForInt(
				"SELECT COUNT(*) FROM users WHERE email = ?", email);
		if(count != 0)
			return true;
		
		return false;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** getByUserNo: get information of a user indexed a userNo  ******/
	@Override
	public User getByUserNo(int userNo) {
		User user = jdbcTemplate.query("SELECT * FROM users WHERE user_no = ?", 
				new Object[] {userNo}, userResultSetExtractor);
		
		user.setApplyingClubs(getApplyingClubs(user.getUserName()));
		user.setInvitedClubs(getInvitedClubs(user.getUserName()));
		
		return user;
	}
	
	/***** getByUserName: get information of a user who has user name as userName  ******/
	@Override
	public User getByUserName(String userName) {
		User user = jdbcTemplate.query("SELECT * FROM users WHERE user_name = ?", 
				new Object[] {userName}, userResultSetExtractor);
		
		user.setApplyingClubs(getApplyingClubs(userName));
		user.setInvitedClubs(getInvitedClubs(userName));
		
		return user;
	}
	
	/* get a list of user's club application and invitation info */
	/* getApplyingClubs, getInvitedClubs and clubJoinInfoRowMapper */
	@Override
	public List<ClubJoinInfo> getApplyingClubs(String userName) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT c.club_name, c.club_title ")
		.append("FROM clubs AS c, club_join_applications AS cj ")
		.append("WHERE c.club_name = cj.club_name ")
		.append("AND cj.user_name = ?");
		String sql = builder.toString();
		
		return jdbcTemplate.query(sql, new Object[] {userName} ,clubJoinInfoRowMapper);
	}
	
	@Override
	public List<ClubJoinInfo> getInvitedClubs(String userName) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT c.club_name, c.club_title ")
		.append("FROM clubs AS c, club_invitations AS ci ")
		.append("WHERE c.club_name = ci.club_name ")
		.append("AND ci.user_name = ?");
		String sql = builder.toString();
		
		return jdbcTemplate.query(sql, new Object[] {userName} , clubJoinInfoRowMapper);
	}
	
	private RowMapper<ClubJoinInfo> clubJoinInfoRowMapper = new RowMapper<ClubJoinInfo>() {
		
		@Override
		public ClubJoinInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
			ClubJoinInfo clubJoinInfo = new ClubJoinInfo();
			
			clubJoinInfo.setClubName(rs.getString("club_name"));
			clubJoinInfo.setClubTitle(rs.getString("club_title"));
			
			return clubJoinInfo;
		}
	};
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** getList: get a list for users  ******/
	@Override
	public List<User> getList(int offset, int limit) {
		return jdbcTemplate.query(
				"SELECT * FROM users ORDER BY user_no DESC LIMIT ? OFFSET ?", 
				new Object[] {limit, offset}, userRowMapper);
	}
	
	/***** getList: get a list for users(search)  ******/
	@Override
	public List<User> getList(final int offset, final int limit, 
			final int searchby, final String keyword) {
		return jdbcTemplate.query(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				
				String sql = makeSearchSql("SELECT * FROM users", searchby);				
				PreparedStatement ps = conn.prepareStatement(
						sql + " ORDER BY user_no LIMIT ? OFFSET ?");
				setPreparedStatement(ps, searchby, keyword, offset, limit);
				return ps;
			}
		}, userRowMapper);
	}
	
	/* makeSearchSql: make a sql statement for search */
	private String makeSearchSql(String basicSql, int searchby) {
		StringBuilder builder = new StringBuilder();
		builder.append(basicSql);
		
		switch(searchby) {
		case SEARCH_USER_NAME:
			builder.append(" WHERE user_name LIKE CONCAT('%', ?, '%')"); 
			break;
		case SEARCH_NICKNAME:
			builder.append(" WHERE nickname LIKE CONCAT('%', ?, '%')"); 
			break;
		case SEARCH_ALL:
			builder.append(
					" WHERE user_name LIKE CONCAT('%', ?, '%') OR nickname LIKE CONCAT('%', ?, '%')"); 
			break;
		case SEARCH_USER_REAL_NAME:
			builder.append(" WHERE user_real_name LIKE CONCAT('%', ?, '%')"); break;
		default:
		}
		
		return builder.toString();
	}
	
	/* setPreparedStatement: set a prepared statement for search */
	private void setPreparedStatement(PreparedStatement ps, int searchby, 
			String keyword, int offset, int limit) throws SQLException {
		switch(searchby) {
		case SEARCH_USER_NAME:
		case SEARCH_NICKNAME:
		case SEARCH_USER_REAL_NAME:
			ps.setString(1, keyword);
			ps.setInt(2, limit);
			ps.setInt(3, offset);
			break;
		case SEARCH_ALL:
			ps.setString(1, keyword);
			ps.setString(2, keyword);
			ps.setInt(3, limit);
			ps.setInt(4, offset);
			break;
		default:
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** certifyUser: certify an user account  ******/
	@Override
	public int certifyUser(String userName, String certificationKey) {
		// if this user account is already certified, return zero
		if(isCertified(userName))
			return 0;
		
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT COUNT(user_name) FROM users ")
		.append("WHERE user_name = ? AND certification_key = ?");
			
		String sql = builder.toString();
		
		int result = jdbcTemplate.queryForInt(sql, userName, certificationKey);
		
		if(result == 1) {
			if(setCertified(userName, true) == 1 && activate(userName) == 1) {
				return result;
			} else {
				throw new UpdateResultCountNotMatchException("can't certified a new user account!");
			}
		} else {
			throw new UpdateResultCountNotMatchException(
					"There is something wrong in certifying a user account :(");
		}
	}
	
	/* isCertified */
	@Override
	public boolean isCertified(String userName) {
		String sql = "SELECT certified FROM users WHERE user_name = ?";
		int certified = jdbcTemplate.queryForInt(sql, userName);
		
		if(certified == 1)
			return true;
		
		return false;
	}
	
	/* setCertified */
	@Override
	public int setCertified(String userName, boolean set) {
		String sql = "UPDATE users SET certified = ? WHERE user_name = ?";
		int certified = 0;
		
		if(set)
			certified = 1;
		
		return jdbcTemplate.update(sql, certified, userName);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** isValidLogin: check if user input for login is valid  ******/
	@Override
	public int isValidLogin(String userName, String password) {
		return jdbcTemplate.queryForInt(
				"SELECT COUNT(user_no) FROM users WHERE user_name = ? AND passwd = SHA(?)",
				userName, password);
	}
	
	/***** isValidLogin: check if user input for login is valid  ******/
	@Override
	public int isValidLogin(int userNo, String password) {
		return jdbcTemplate.queryForInt(
				"SELECT COUNT(user_no) FROM users WHERE user_no = ? AND passwd = SHA(?)",
				userNo, password);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** changePassword: change a password of a user account  ******/	
	@Override
	public int changePassword(String userName, String password) {
		return jdbcTemplate.update(
				"UPDATE users SET passwd = SHA(?) WHERE user_name = ?",
				password, userName);
	}

	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// ResultSetExtractors and RowMappers
	
	/* userResultSetExtractor: extract a user object from result set  */
	private ResultSetExtractor<User> userResultSetExtractor = new ResultSetExtractor<User>() {

		@Override
		public User extractData(ResultSet rs) throws SQLException,
				DataAccessException {
			User user = null;
			
			if(rs.next()) {
				user = makeUserFromResultSet(rs);
			} else {
				user = new User();
			}
			
			return user;
		}
	};
	
	/* userRowMapper: row mapper for a user list */
	private RowMapper<User> userRowMapper = new RowMapper<User>() {
		
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			return makeUserFromResultSet(rs);		
		}
	};
	
	/* makeUserFromResultSet */
	private User makeUserFromResultSet(ResultSet rs) 
			throws SQLException {
		User user = new User();
		
		user.setUserNo(rs.getInt("user_no"));
		user.setUserName(rs.getString("user_name"));
		user.setEmail(rs.getString("email"));
		//user.setPasswd(rs.getString("passwd"));
		user.setDateOfJoin(rs.getTimestamp("date_of_join"));
		user.setCertificationKey(rs.getString("certification_key"));
		
		boolean active = (rs.getInt("active") == 1) ? true : false;
		boolean certified = (rs.getInt("certified") == 1) ? true : false;
		user.setActive(active);
		user.setCertified(certified);
		
		user.setNickname(rs.getString("nickname"));
		user.setUserRealName(rs.getString("user_real_name"));
		user.setDateOfBirth(rs.getTimestamp("date_of_birth"));
		
		return user;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// utils
	
	/* checkRequiredColumn: check required columns */
	/************************************************
	 * MySQL은 ''을 null로 인식하지 않는다. 기술종속적 부분으로서
	 * Dao 구현 클래스에서 직접 필터링 처리
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
	 * by type (I)UserDao (not MySqlUserDao),
	 * this method is invisible to you 
	 * and your Database may be safe 
	 * from unintended deleting data :D
	 **************************************************/
	public int deleteAll() {
		return jdbcTemplate.update("DELETE FROM users");
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// temporary	
	/***** getMaxUserNo: get an account information  ******/
	@Override
	public int getMaxUserNo() {
		return jdbcTemplate.queryForInt("SELECT MAX(user_no) FROM users");
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// empty

	@Override
	public int getCount(String userName) {
		return 0;
	}

	@Override
	public int getCountEmail(String email) {
		return 0;
	}

	@Override
	public String getUserNameByKeyword(String keyword) {
		return null;
	}
}
