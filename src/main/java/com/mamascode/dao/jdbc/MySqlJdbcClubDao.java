package com.mamascode.dao.jdbc;

/****************************************************
 * @Deprecated
 * MySqlJdbcClubDao: implements ClubDao(I)
 *
 * uses the Spring JDBC Template. 
 * handling: clubs, club_members, club_categories
 * 트랜잭션 처리: Service tire
 * 
 * by Hwang Inho
 ****************************************************/

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.mamascode.dao.ClubDao;
import com.mamascode.model.ClubCategory;
import com.mamascode.model.ClubJoinInfo;
import com.mamascode.model.ProfilePicture;
import com.mamascode.model.User;
import com.mamascode.exceptions.UpdateResultCountNotMatchException;
import com.mamascode.model.Club;

@Deprecated
public class MySqlJdbcClubDao implements ClubDao {
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
	// constructors(default)

	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// implemented methods
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** create: create a new club  ******/
	@Override
	public int create(final Club club) {
		// parameter filtering
		if(!checkClubParameters(club) || isExistingClubName(club.getClubName()))
			return 0;
		
		// create a club
		int result = jdbcTemplate.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				String sql = createSql(club);
				return setPreparedStatement(conn, sql, club);
			}
		});
			
		if(result == 1) {
			// if result is 1, initiate a master user into this club  
			int resultJoin = joinClub(club.getClubName(), club.getMasterName());
			
			if(resultJoin == 1)
				// If resultJoin is 1, success all
				return resultJoin;
			else
				throw new UpdateResultCountNotMatchException("joinClub result is not 1");
		} else {
			throw new UpdateResultCountNotMatchException("creating club result is not 1");
		}
	}
	
	/* isExistingClubName: check if there is a club has a given name */
	@Override
	public boolean isExistingClubName(String clubName) {
		int result = jdbcTemplate.queryForInt(
				"SELECT COUNT(club_name) FROM clubs WHERE club_name = ?", clubName);
		
		if(result != 0)
			return true;
		
		return false;
	}
	
	/* create sql statement(club creation) */
	private String createSql(Club club) {
		// make a sql statement
		StringBuilder builder = new StringBuilder();
		builder.append("INSERT INTO clubs ")
		.append("(club_name, club_title, grand_category_id, master_name, ");
		builder.append("date_of_created, type, max_member_num");
		
		if(club.getCategoryId() != -1) builder.append(", category_id");
		
		builder.append(") VALUES (?, ?, ?, ?, NOW(), ?, ?");
		
		if(club.getCategoryId() != -1) builder.append(", ?");
		
		builder.append(")");
		
		return builder.toString();
	}
	
	/* set a prepared statement(club creation) */
	private PreparedStatement setPreparedStatement(Connection conn,
			String sql, Club club) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, club.getClubName());
		ps.setString(2, club.getClubTitle());
		ps.setShort(3, club.getGrandCategoryId());
		ps.setString(4, club.getMasterName());
		ps.setShort(5, club.getType());
		ps.setInt(6, club.getMaxMemberNum());
		if(club.getCategoryId() != -1) {
			ps.setShort(7, club.getCategoryId());
		}
		
		return ps;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** inactivate: inactivate a club  ******/
	@Override
	public int inactivate(String clubName) {
		return jdbcTemplate.update("UPDATE clubs SET active = 0 WHERE club_name = ?", clubName);
	}
	
	/***** activate: activate a club  ******/
	@Override
	public int activate(String clubName) {
		return jdbcTemplate.update("UPDATE clubs SET active = 1 WHERE club_name = ?", clubName);
	}
	
	/***** update: update club information  ******/
	@Override
	public int update(Club club) {
		// sql statement
		StringBuilder builder = new StringBuilder();
		builder.append("UPDATE clubs SET club_title = ?, category_id = ?, ")
		.append("type = ?, max_member_num = ?, recruit = ? ")
		.append("WHERE club_name = ?");
		
		String sql = builder.toString();
		short isRecruit = (short) (club.isRecruit() ? 1 : 0);
		
		// execute
		int result = jdbcTemplate.update(sql, club.getClubTitle(), club.getCategoryId(),
				club.getType(), club.getMaxMemberNum(), isRecruit, club.getClubName());
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException("update result is not 1");
	}
	
	/***** delete: delete a club  ******/
	@Override
	public int delete(String clubName) {
		int clubMemberCount = getClubMemberCount(clubName);
		
		int result = 0;
		int resultClose = closeClub(clubName);
		
		if(resultClose == clubMemberCount) {
			result = jdbcTemplate.update("DELETE FROM clubs WHERE club_name = ?", clubName);
				
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
	/***** getCount: get a size of the table clubs  ******/
	@Override
	public int getCount(int searchby, Object keyword) {
		return 0;
	}
	/*
	@Override
	public int getCount() {
		return jdbcTemplate.queryForInt("SELECT COUNT(*) FROM clubs");
	}
	
	/***** getCount: get a size of the table clubs(name search)
	@Override
	public int getCount(String clubName) {
		return jdbcTemplate.queryForInt(
			"SELECT COUNT(*) FROM clubs WHERE club_name LIKE CONCAT('%', ?, '%')", 
			clubName);
	}
	
	/***** getCount: get a size of the table clubs(filtered by category)
	@Override
	public int getCount(short catogoryId) {
		return jdbcTemplate.queryForInt(
			"SELECT COUNT(*) FROM clubs WHERE category_id = ?", 
			catogoryId);
	}
	*/

	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** get: get club information  ******/
	@Override
	public Club get(String clubName) {
		Club club = jdbcTemplate.query("SELECT * FROM clubs WHERE club_name = ?", 
				new Object[] {clubName}, clubResultSetExtractor);
		club.setNumberOfClubMember(getNumberOfClubMember(clubName));
		
		return club;
	}
	
	@Override
	public int getNumberOfClubMember(String clubName) {
		return jdbcTemplate.queryForInt(
				"SELECT COUNT(*) FROM club_members WHERE club_name = ?", 
				clubName);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** getCountUserClubs: get a number of clubs of a specified user  ******/
	@Override
	public int getCountUserClubs(String userName) {
		return jdbcTemplate.queryForInt(
				"SELECT COUNT(*) FROM club_members WHERE member_name = ?", userName);
	}
	
	/***** getUserClubs: get a list of a user's clubs  ******/
	@Override
	public List<Club> getUserClubs(int offset, int limit, String userName) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT c.club_no, c.club_name, c.club_title, ")
		.append("c.grand_category_id, c.category_id, c.master_name, c.type, ")
		.append("c.max_member_num, c.active, c.recruit, c.date_of_created ")
		.append("FROM clubs AS c, club_members cm ")
		.append("WHERE c.club_name = cm.club_name AND cm.member_name = ? ")
		.append("LIMIT ? OFFSET ?");
		
		String sql = builder.toString();
		
		return jdbcTemplate.query(sql, new Object[] {userName, limit, offset}, clubRowMapper);
	}
	
	/***** getUserClubs: get a list of a user's clubs (full) ******/
	@Override
	public List<Club> getUserClubs(String userName) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT c.club_no, c.club_name, c.club_title, ")
		.append("c.grand_category_id, c.category_id, c.master_name, c.type, ")
		.append("c.max_member_num, c.active, c.recruit, c.date_of_created ")
		.append("FROM clubs AS c, club_members cm ")
		.append("WHERE c.club_name = cm.club_name AND cm.member_name = ? ");
		
		String sql = builder.toString();
		
		return jdbcTemplate.query(sql, new Object[] {userName}, clubRowMapper);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	@Override
	public List<Club> getList(int offset, int limit, int searchby,
			Object keyword, int reverse) {
		return null;
	}
	
	/***** getList: get a club list
	@Override
	public List<Club> getList(int offset, int limit, boolean reverse) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT c.club_no, c.club_name, c.category_id, ")
		.append("c.grand_category_id, c.club_title, cat.category_title, ")
		.append("c.type, c.max_member_num, c.active, c.recruit, ")
		.append("c.master_name, c.date_of_created ")
		.append("FROM clubs AS c, club_categories AS cat ")
		.append("WHERE c.grand_category_id = cat.category_id ");
		
		if(reverse)
			builder.append("ORDER BY date_of_created DESC ");
		
		builder.append("LIMIT ? OFFSET ?");
		
		String sql = builder.toString();
		
		return jdbcTemplate.query(sql, new Object[] {limit, offset}, clubRowMapper);
	}

	

	/***** getList: get a club list(name search)
	@Override
	public List<Club> getList(final int offset, final int limit, final String clubName, boolean reverse) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT c.club_no, c.club_name, c.category_id, ")
		.append("c.grand_category_id, c.club_title, cat.category_title, c.master_name, ")
		.append("c.type, c.max_member_num, c.active, c.recruit, c.date_of_created ")
		.append("FROM clubs AS c, club_categories AS cat ")
		.append("WHERE c.grand_category_id = cat.category_id ")
		.append("AND club_name LIKE CONCAT('%', ?, '%') LIMIT ? OFFSET ?");
		
		String sql = builder.toString();
		
		return jdbcTemplate.query(sql, new Object[] {limit, offset, clubName}, clubRowMapper);
	}
	
	/***** getList: get a club list(filtered by category_id)
	@Override
	public List<Club> getList(final int offset, final int limit, final short categoryId, boolean reverse) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT c.club_no, c.club_name, c.category_id, ")
		.append("c.grand_category_id, c.club_title, cat.category_title, c.master_name, ")
		.append("c.type, c.max_member_num, c.active, c.recruit, c.date_of_created ")
		.append("FROM clubs AS c, club_categories AS cat ")
		.append("WHERE c.grand_category_id = cat.category_id ")
		.append("AND c.grand_category_id = ? LIMIT ? OFFSET ?");
		
		String sql = builder.toString();
		
		return jdbcTemplate.query(sql, new Object[] {limit, offset, categoryId}, clubRowMapper);
	}*/

	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** insertClubApplication ******/
	@Override
	public int insertClubApplication(String clubName, String userName, String comment) {
		String sql = new StringBuilder()
				.append("INSERT INTO club_join_applications ")
				.append("(club_name, user_name, comment, appl_date) ")
				.append("VALUES (?, ?, ?, NOW())").toString();
		int result = jdbcTemplate.update(sql, clubName, userName, comment);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException("insertClubApplication result is not 1");
	}
	
	/***** isThisUserApplicant: check if a user already apply to this club  ******/
	@Override
	public boolean isThisUserApplicant(String clubName, String userName) {
		String sql = new StringBuilder()
				.append("SELECT COUNT(*) FROM club_join_applications ")
				.append("WHERE club_name = ? AND user_name = ?").toString();
		
		int count = jdbcTemplate.queryForInt(sql, clubName, userName);
		
		if(count != 0)
			return true;
		
		return false;
	}
	
	/***** insertClubInvitation ******/
	@Override
	public int insertClubInvitation(String clubName, String userName, String comment) {
		String sql = new StringBuilder()
				.append("INSERT INTO club_invitations ")
				.append("(club_name, user_name, comment, inv_date) ")
				.append("VALUES (?, ?, ?, NOW())").toString();
			
		int result = jdbcTemplate.update(sql, clubName, userName, comment);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException("insertClubInvitation result is not 1");
	}
	
	/***** isThisUserInvitee: check if a user already is invited to this club  ******/
	@Override
	public boolean isThisUserInvitee(String clubName, String userName) {
		String sql = new StringBuilder()
		.append("SELECT COUNT(*) FROM club_invitations ")
		.append("WHERE club_name = ? AND user_name = ?").toString();
		
		int count = jdbcTemplate.queryForInt(sql, clubName, userName);

		if(count != 0)
			return true;

		return false;
	}
	
	/***** joinClub: 동아리 가입 처리 ******/
	@Override
	public int joinClub(String clubName, String memberName) {
		StringBuilder builder = new StringBuilder();
		builder.append("INSERT INTO club_members ")
		.append("(club_name, member_name, join_date) VALUES (?, ?, NOW())");
		
		String sql = builder.toString();
		
		int result = jdbcTemplate.update(sql, clubName, memberName);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException("joinClub result is not 1");
	}
	
	/***** deleteApplication & deleteInvitation ******/
	@Override
	public int deleteApplication(String clubName, String userName) {
		int result = jdbcTemplate.update(
				"DELETE FROM club_join_applications WHERE club_name = ? AND user_name = ?", 
				clubName, userName);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException("deleteApplication result is not 1");
	}

	@Override
	public int deleteInvitation(String clubName, String userName) {
		int result = jdbcTemplate.update(
				"DELETE FROM club_invitations WHERE club_name = ? AND user_name = ?", 
				clubName, userName);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException("deleteInvitation result is not 1");
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** leaveClub: leave a club ******/
	@Override
	public int leaveClub(String clubName, String memberName) {
		// check whether a given memberNo is 
		// a identification number of master user
		int masterCount = jdbcTemplate.queryForInt(
				"SELECT COUNT(*) FROM clubs WHERE master_name = ? AND club_name = ?",
				memberName, clubName);
		
		// if memberNo is a number of master user in this club, return zero
		if(masterCount != 0)
			return 0;
				
		// if not, execute delete query
		StringBuilder builder = new StringBuilder();
		builder.append("DELETE FROM club_members ")
		.append("WHERE club_name = ? AND member_name = ?");
		
		String sql = builder.toString();
		
		int result = jdbcTemplate.update(sql, clubName, memberName);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException("leaveClub result is not 1");
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** isExistingClubMember: check if a user is a member of a club ******/
	@Override
	public boolean isExistingClubMember(String clubName, String memberName) {
		int count = jdbcTemplate.queryForInt(
				"SELECT COUNT(*) FROM club_members WHERE club_name = ? AND member_name = ?", 
				clubName, memberName);
		
		if(count != 0)
			return true;
		
		return false;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** closeClub: clear member information of a club ******/
	@Override
	public int closeClub(String clubName) {
		return jdbcTemplate.update("DELETE FROM club_members WHERE club_name = ?", clubName);
	}

	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** getClubMemberCount: get a number of club members ******/
	@Override
	public int getClubMemberCount(String clubName) {
		return jdbcTemplate.queryForInt(
				"SELECT COUNT(*) FROM club_members WHERE club_name = ?", clubName);
	}
	
	/***** getClubMemberList: get club members ******/
	@Override
	public List<User> getClubMemberList(int offset, int limit, String clubName) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT u.user_no, u.user_name, u.email, ")
		.append("u.date_of_join, c.active, u.nickname, ")
		.append("u.user_real_name, u.date_of_birth, c.join_date, ")
		.append("p.pic_id, p.file_name ")
		.append("FROM users AS u INNER JOIN club_members AS c ON u.user_name = c.member_name ")
		.append("LEFT OUTER JOIN profile_pictures AS p ON u.user_name = p.user_name ")
		.append("WHERE c.club_name = ? ORDER BY c.join_date ASC LIMIT ? OFFSET ?");
		String sql = builder.toString();
			
		return jdbcTemplate.query(sql, 
				new Object[] {clubName, limit, offset}, clubMemberUserRowMapper);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	
	/***** getClubJoinApplications ******/
	@Override
	public List<ClubJoinInfo> getClubJoinApplications(int offset, int limit, String clubName) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT c.club_name, cj.user_name, c.club_title, cj.comment ")
		.append("FROM clubs AS c, club_join_applications AS cj ")
		.append("WHERE c.club_name = cj.club_name ")
		.append("AND cj.club_name = ? LIMIT ? OFFSET ?");
		String sql = builder.toString();
		
		return jdbcTemplate.query(sql, 
				new Object[] {clubName, limit, offset}, 
				clubJoinInfoRowMapper);
	}
	
	/***** getClubJoinApplicationsCount ******/
	@Override
	public int getClubJoinApplicationsCount(String clubName) {
		String sql = "SELECT COUNT(*) FROM club_join_applications WHERE club_name = ?";
		return jdbcTemplate.queryForInt(sql, clubName);
	}
	
	/***** getClubInvitations ******/
	@Override
	public List<ClubJoinInfo> getClubInvitations(int offset, int limit, String clubName) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT c.club_name, ci.user_name, c.club_title, ci.comment ")
		.append("FROM clubs AS c, club_invitations AS ci ")
		.append("WHERE c.club_name = ci.club_name ")
		.append("AND ci.club_name = ? LIMIT ? OFFSET ?");
		String sql = builder.toString();
		
		return jdbcTemplate.query(sql, 
				new Object[] {clubName, limit, offset}, 
				clubJoinInfoRowMapper);
	}
	
	/***** getClubInvitationsCount ******/
	@Override
	public int getClubInvitationsCount(String clubName) {
		String sql = "SELECT COUNT(*) FROM club_invitations WHERE club_name = ?";
		return jdbcTemplate.queryForInt(sql, clubName);
	}
	
	/***** clubJoinInfoRowMapper ******/
	private RowMapper<ClubJoinInfo> clubJoinInfoRowMapper = new RowMapper<ClubJoinInfo>() {
		
		@Override
		public ClubJoinInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
			ClubJoinInfo clubJoinInfo = new ClubJoinInfo();
			
			clubJoinInfo.setClubName(rs.getString("club_name"));
			clubJoinInfo.setUserName(rs.getString("user_name"));
			clubJoinInfo.setClubTitle(rs.getString("club_title"));
			clubJoinInfo.setComment(rs.getString("comment"));
			
			return clubJoinInfo;
		}
	};
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// club crews
	/***** isThisUserClubMaster: check if this user is a master of a club ******/
	@Override
	public boolean isThisUserClubMaster(String userName, String clubName) {
		int count = jdbcTemplate.queryForInt(
				"SELECT COUNT(*) FROM clubs WHERE master_name = ? AND club_name = ?", 
				userName, clubName);
		
		if(count == 1)
			return true;
		
		return false;
	}
	
	/***** isThisUserClubCrew: check if this user is a crew of a club ******/
	@Override
	public boolean isThisUserClubCrew(String userName, String clubName) {
		int count = jdbcTemplate.queryForInt(
				"SELECT COUNT(*) FROM club_crew WHERE crew_name = ? AND club_name = ?", 
				userName, clubName);
		
		if(count == 1)
			return true;
		
		return false;
	}
	
	/***** insertClubCrew: add a club crew ******/
	@Override
	public int insertClubCrew(String userName, String clubName) {
		int result = jdbcTemplate.update(
				"INSERT INTO club_crew (club_name, crew_name, appointed_date) VALUES (?, ?, NOW())", 
				clubName, userName);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException("insertClubCrew result is not 1");
	}
	
	/***** deleteClubCrew: delete a club crew ******/
	@Override
	public int deleteClubCrew(String userName, String clubName) {
		int result = jdbcTemplate.update(
				"DELETE FROM club_crew WHERE club_name = ? AND crew_name = ?", 
				clubName, userName);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException("deleteClubCrew result is not 1");
	}
	
	/***** getClubCrews: get a list of club crews ******/
	@Override
	public List<User> getClubCrews(String clubName, int offset, int limit) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT u.user_no, u.user_name, u.nickname, c.appointed_date ")
			.append("FROM club_crew c, users u ")
			.append("WHERE c.crew_name = u.user_name ")
			.append("AND c.club_name = ? LIMIT ? OFFSET ?");
		String sql = builder.toString();
		
		return jdbcTemplate.query(sql, new Object[] {clubName, limit, offset},
				new RowMapper<User>() {
			// row mapper: type - User
			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User user = new User();
				user.setUserNo(rs.getInt("user_no"));
				user.setUserName(rs.getString("user_name"));
				user.setNickname(rs.getString("nickname"));
				user.setClubCrewAppointedDate(rs.getTimestamp("appointed_date"));
				return user;
			}
		});
	}
	
	/***** getClubCrewCount ******/
	@Override
	public int getClubCrewCount(String clubName) {
		return jdbcTemplate.queryForInt(
				"SELECT COUNT(*) FROM club_crew WHERE club_name = ?", clubName);
	}
	
	/***** getClubNameForMasterUser ******/
	@Override
	public List<String> getClubNameForMasterUser(String userName) {
		return jdbcTemplate.query(
				"SELECT club_name FROM clubs WHERE master_name = ?", 
				new Object[] {userName}, new RowMapper<String>() {
					@Override
					public String mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getString("club_name");
					}
				});
	}
	
	/***** getClubNameForClubCrewUser ******/
	@Override
	public List<String> getClubNameForClubCrewUser(String userName) {
		return jdbcTemplate.query(
				"SELECT club_name FROM club_crew WHERE crew_name = ?", 
				new Object[] {userName}, new RowMapper<String>() {
					@Override
					public String mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getString("club_name");
					}
				});
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// delete user information: 사용자 정보 삭제(탈퇴 처리를 위해)

	/* deleteClubCrew: 운영진 정보 삭제 */
	@Override
	public int deleteClubCrew(String userName) {
		try {
			return deleteUserInternal(userName, "club_crew", "crew_name");
		} catch (UpdateResultCountNotMatchException e) {
			throw new UpdateResultCountNotMatchException("deleteClubCrew result count does not match");
		}
	}
	
	/* deleteClubJoinApplication: 가입 신청 정보 삭제 */
	@Override
	public int deleteClubJoinApplication(String userName) {
		try {
			return deleteUserInternal(userName, "club_join_applications", "user_name");
		} catch (UpdateResultCountNotMatchException e) {
			throw new UpdateResultCountNotMatchException("deleteClubJoinApplication result count does not match");
		}
	}
	
	/* deleteClubJoinInvitation: 가입 초대 정보 삭제 */
	@Override
	public int deleteClubJoinInvitation(String userName) {
		try {
			return deleteUserInternal(userName, "club_invitations", "user_name");
		} catch (UpdateResultCountNotMatchException e) {
			throw new UpdateResultCountNotMatchException("deleteClubJoinInvitation result count does not match");
		}
	}
	
	/* deleteClubMemberForUser: 동아리 가입 정보 삭제 */
	@Override
	public int deleteClubMemberForUser(String userName) {
		try {
			return deleteUserInternal(userName, "club_members", "member_name");
		} catch (UpdateResultCountNotMatchException e) {
			throw new UpdateResultCountNotMatchException("deleteClubMemberForUser result count does not match");
		}
	}
	
	// 내부 메소드 - deleteUserInternal: 실제 DB 작업을 처리
	private int deleteUserInternal(String userName, String tableName, String columnName) {
		// 레코드 수 가져오기
		int resultCount = getCountRecordForUser(userName, tableName, columnName);
		
		// 쿼리문 생성
		StringBuilder builder = new StringBuilder();
		builder.append("DELETE FROM ").append(tableName).append(" WHERE ")
		.append(columnName).append(" = ?");
		
		String sql = builder.toString();
		
		if(jdbcTemplate.update(sql, userName) == resultCount) {
			// 레코드가 모두 지워진 경우에는 레코드 수를 리턴(정상 처리)
			return resultCount;
		} else {
			// 그렇지 않은 경우는 Runtime 예외 발생(트랜잭션 롤백)
			throw new UpdateResultCountNotMatchException("result count does not match");
		}
	}
	
	// 내부 메소드 - getCountRecordForUser: 삭제할 레코드 수를 리턴
	private int getCountRecordForUser(String userName, String tableName, String columnName) {
		// 쿼리문 생성
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT COUNT(*) FROM ").append(tableName).append(" WHERE ")
		.append(columnName).append(" = ?");
		
		String sql = builder.toString();
		
		// 쿼리 실행
		return jdbcTemplate.queryForInt(sql, userName);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// search

	/* searchClubMember: search club members */
	@Override
	public List<User> searchClubMember(String keyword, String clubName, int searchType) {
		// sql statement
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT u.user_name, u.nickname, u.user_no, cm.join_date ")
				.append("FROM club_members cm, users u ")
				.append("WHERE cm.member_name = u.user_name ")
				.append("AND cm.club_name = ? ");
		
		switch(searchType) {
		case SEARCH_MEMBER_CREW_NAME:
			builder.append("AND u.user_name LIKE CONCAT('%', ? ,'%')");
			break;
		case SEARCH_MEMBER_CREW_NICKNAME:
			builder.append("AND u.nickname LIKE CONCAT('%', ? ,'%')");
			break;
		case SEARCH_MEMBER_CREW_ALL: default:
			builder.append("AND (u.user_name LIKE CONCAT('%', ? ,'%') OR u.nickname LIKE CONCAT('%', ? ,'%'))");
			break;
		}
		
		String sql = builder.toString();
		
		// execute query
		if(searchType == SEARCH_MEMBER_CREW_NAME || searchType == SEARCH_MEMBER_CREW_NICKNAME) {
			return jdbcTemplate.query(sql, new Object[] {clubName, keyword}, 
					searchClubMemberRowMapper);
		} else {
			return jdbcTemplate.query(sql, new Object[] {clubName, keyword, keyword}, 
					searchClubMemberRowMapper);
		}
	}

	/* searchClubMemberCount: get a number of rows that correspond to searching keyword */
	@Override
	public int searchClubMemberCount(String keyword, String clubName,
			int searchType) {
		// sql statement
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT COUNT(*) FROM club_members cm, users u ")
				.append("WHERE cm.member_name = u.user_name AND cm.club_name = ? ");
		
		switch(searchType) {
		case SEARCH_MEMBER_CREW_NAME:
			builder.append("AND u.user_name LIKE CONCAT('%', ? ,'%')");
			break;
		case SEARCH_MEMBER_CREW_NICKNAME:
			builder.append("AND u.nickname LIKE CONCAT('%', ? ,'%')");
			break;
		case SEARCH_MEMBER_CREW_ALL: default:
			builder.append("AND u.user_name LIKE CONCAT('%', ? ,'%') OR u.nickname LIKE CONCAT('%', ? ,'%')");
			break;
		}
		
		String sql = builder.toString();
		
		// execute query
		if(searchType == SEARCH_MEMBER_CREW_NAME || searchType == SEARCH_MEMBER_CREW_NICKNAME) {
			return jdbcTemplate.queryForInt(sql, clubName, keyword);
		} else {
			return jdbcTemplate.queryForInt(sql, clubName, keyword, keyword);
		}
	}
	
	/* searchClubCrew: search club crews */
	@Override
	public List<User> searchClubCrew(String keyword, String clubName, int searchType) {
		// sql statement
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT u.user_name, u.nickname, u.user_no, c.appointed_date ")
				.append("FROM club_crew c, users u ")
				.append("WHERE c.crew_name = u.user_name ")
				.append("AND c.club_name = ? ");
		
		switch(searchType) {
		case SEARCH_MEMBER_CREW_NAME:
			builder.append("AND u.user_name LIKE CONCAT('%', ? ,'%')");
			break;
		case SEARCH_MEMBER_CREW_NICKNAME:
			builder.append("AND u.nickname LIKE CONCAT('%', ? ,'%')");
			break;
		case SEARCH_MEMBER_CREW_ALL: default:
			builder.append("AND (u.user_name LIKE CONCAT('%', ? ,'%') OR u.nickname LIKE CONCAT('%', ? ,'%'))");
			break;
		}
		
		String sql = builder.toString();
		
		// execute query
		if(searchType == SEARCH_MEMBER_CREW_NAME || searchType == SEARCH_MEMBER_CREW_NICKNAME) {
			return jdbcTemplate.query(sql, new Object[] {clubName, keyword}, 
					searchClubCrewRowMapper);
		} else {
			return jdbcTemplate.query(sql, new Object[] {clubName, keyword, keyword}, 
					searchClubCrewRowMapper);
		}
	}

	/* searchClubCrewCount: get a number of rows that correspond to searching keyword */
	@Override
	public int searchClubCrewCount(String keyword, String clubName,
			int searchType) {
		// sql statement
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT COUNT(*) FROM club_crew c, users u ")
				.append("WHERE c.crew_name = u.user_name AND c.club_name = ? ");
		
		switch(searchType) {
		case SEARCH_MEMBER_CREW_NAME:
			builder.append("AND u.user_name LIKE CONCAT('%', ? ,'%')");
			break;
		case SEARCH_MEMBER_CREW_NICKNAME:
			builder.append("AND u.nickname LIKE CONCAT('%', ? ,'%')");
			break;
		case SEARCH_MEMBER_CREW_ALL: default:
			builder.append("AND u.user_name LIKE CONCAT('%', ? ,'%') OR u.nickname LIKE CONCAT('%', ? ,'%')");
			break;
		}
		
		String sql = builder.toString();
		
		// execute query
		if(searchType == SEARCH_MEMBER_CREW_NAME || searchType == SEARCH_MEMBER_CREW_NICKNAME) {
			return jdbcTemplate.queryForInt(sql, clubName, keyword);
		} else {
			return jdbcTemplate.queryForInt(sql, clubName, keyword, keyword);
		}
	}
	
	/* searchClubMemberRowMapper: row mapper for club member searching */
	private RowMapper<User> searchClubMemberRowMapper = new RowMapper<User>() {
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setUserName(rs.getString("user_name"));
			user.setNickname(rs.getString("nickname"));
			user.setUserNo(rs.getInt("user_no"));
			user.setDateOfClubJoin(rs.getTimestamp("join_date"));
			return user;
		}
	};
	
	/* searchClubCrewRowMapper: row mapper for club crew searching */
	private RowMapper<User> searchClubCrewRowMapper = new RowMapper<User>() {
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setUserName(rs.getString("user_name"));
			user.setNickname(rs.getString("nickname"));
			user.setUserNo(rs.getInt("user_no"));
			user.setDateOfJoin(rs.getTimestamp("appointed_date"));
			return user;
		}
	};
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// ResultSetExtractors and RowMappers
	
	/* clubResultSetExtractor: extract a club object from result set */
	private ResultSetExtractor<Club> clubResultSetExtractor =  new ResultSetExtractor<Club>() {

		@Override
		public Club extractData(ResultSet rs) throws SQLException,
				DataAccessException {
			Club club = null;
			
			if(rs.next()) {
				club = makeClubFromResultSet(rs);
			} else {
				club = new Club();	// empty club
			}
			
			return club;
		}
	};
	
	/* clubRowMapper: row mapper for a club list */
	private RowMapper<Club> clubRowMapper = new RowMapper<Club>() {
		
		@Override
		public Club mapRow(ResultSet rs, int rowNum) throws SQLException {
			return makeClubFromResultSet(rs);
		}
	};
	
	/* userRowMapper: row mapper for a user list */
	private RowMapper<User> clubMemberUserRowMapper = new RowMapper<User>() {
		
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			return makeUserFromResultSet(rs);		
		}
	}; 
	
	/////////// make a object from result set ///////////
	
	/* makeClubFromResultSet */
	private Club makeClubFromResultSet(ResultSet rs) throws SQLException {
		Club club = new Club();
		
		club.setClubNo(rs.getInt("club_no"));
		club.setClubName(rs.getString("club_name"));
		club.setClubTitle(rs.getString("club_title"));
		club.setGrandCategoryId(rs.getShort("grand_category_id"));
		club.setCategoryId(rs.getShort("category_id"));
		club.setMasterName(rs.getString("master_name"));
		club.setType(rs.getShort("type"));
		club.setMaxMemberNum(rs.getInt("max_member_num"));
		
		boolean active = rs.getShort("active") == 1 ? true : false;
		boolean recruit = rs.getShort("recruit") == 1 ? true : false;
		
		club.setActive(active);
		club.setRecruit(recruit);
		club.setDateOfCreated(rs.getDate("date_of_created"));
		
		return club;
	}
	
	/* makeUserFromResultSet */
	private User makeUserFromResultSet(ResultSet rs) 
			throws SQLException {
		User user = new User();
		
		user.setUserNo(rs.getInt("user_no"));
		user.setUserName(rs.getString("user_name"));
		user.setEmail(rs.getString("email"));
		user.setDateOfJoin(rs.getTimestamp("date_of_join"));
		
		boolean active = false;
		if(rs.getInt("active") == 1)
			active = true;
		user.setActive(active);
		
		user.setNickname(rs.getString("nickname"));
		user.setUserRealName(rs.getString("user_real_name"));
		user.setDateOfBirth(rs.getTimestamp("date_of_birth"));
		user.setDateOfClubJoin(rs.getTimestamp("join_date"));
		
		ProfilePicture profilePicture = new ProfilePicture();
		profilePicture.setPicId(rs.getInt("pic_id"));
		profilePicture.setFileName(rs.getString("file_name"));
		profilePicture.setUserName(user.getUserName());
		
		user.setProfilePicture(profilePicture);
		
		return user;
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
	 * by type (I)ClubDao (not MySqlJdbcClubDao),
	 * this method is invisible to you 
	 * and your Database may be safe 
	 * from unintended deleting data :D
	 **************************************************/
	public int deleteAll() {
		return jdbcTemplate.update("DELETE FROM clubs");
	}
	
	public int deleteAllFromClubMembers() {
		return jdbcTemplate.update("DELETE FROM club_members");
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// club categories
	/* addClubCategory */
	@Override
	public int addClubCategory(ClubCategory clubCategory) {
		return jdbcTemplate.update(
				"INSERT INTO club_categories VALUES (?, ?, ?)", clubCategory.getCategoryId(), 
				clubCategory.getParentCategoryId(), clubCategory.getCategoryTitle());
	}
	
	/* getClubGrandCategories */
	@Override
	public List<ClubCategory> getClubGrandCategories() {
		return jdbcTemplate.query(
				"SELECT * FROM club_categories WHERE parent_cat_id = 0", 
				clubCategoryRowMapper);
	}
	
	/* getClubChildCategories */
	@Override
	public List<ClubCategory> getClubChildCategories(int parentCategoryId) {
		return jdbcTemplate.query(
				"SELECT * FROM club_categories WHERE parent_cat_id = ?",
				new Object[] {parentCategoryId}, clubCategoryRowMapper);
	}
	
	/* getClubCategory */
	@Override
	public ClubCategory getClubCategory(int categoryId) {
		return jdbcTemplate.query(
				"SELECT * FROM club_categories WHERE category_id = ?", 
				new Object[] {categoryId}, new ResultSetExtractor<ClubCategory>() {

			@Override
			public ClubCategory extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				ClubCategory clubCategory = new ClubCategory();
				
				if(rs.next()) {
					clubCategory.setCategoryId(rs.getInt("category_id"));
					clubCategory.setParentCategoryId(rs.getInt("parent_cat_id"));
					clubCategory.setCategoryTitle(rs.getString("category_title"));
				}
				return clubCategory;
			}
		});
	}
	
	/* clubCategoruRowMapper */
	private RowMapper<ClubCategory> clubCategoryRowMapper = new RowMapper<ClubCategory>() {
		@Override
		public ClubCategory mapRow(ResultSet rs, int rowNum) throws SQLException {
			ClubCategory clubCategory = new ClubCategory();
			clubCategory.setCategoryId(rs.getInt("category_id"));
			clubCategory.setParentCategoryId(rs.getInt("parent_cat_id"));
			clubCategory.setCategoryTitle(rs.getString("category_title"));
			return clubCategory;
		}
	};
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// temporary
	/***** getLastInsertId: get a club_no inserted most recently ******/
	@Override
	public int getLastInsertId() {
		return jdbcTemplate.queryForInt("SELECT LAST_INSERT_ID()");
	}
	
	/***** getMaxClubNo: get a max club_no of the table clubs  ******/
	@Override
	public int getMaxClubNo() {
		return jdbcTemplate.queryForInt("SELECT MAX(club_no) FROM clubs");
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// empty
	
	@Override
	public List<String> getClubNameForUser(String userName) {
		return null;
	}

	@Override
	public int transferMaster(String clubName, String newMasterName) {
		return 0;
	}

	@Override
	public int deleteClubCrewAll(String clubName) {
		return 0;
	}

	@Override
	public int deleteClubJoinApplicationAll(String clubName) {
		return 0;
	}

	@Override
	public int deleteClubJoinInvitationAll(String clubName) {
		return 0;
	}

	@Override
	public List<String> getClubMemberList(String clubName) {
		return null;
	}
}
