package com.mamascode.dao.jdbc;

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

import com.mamascode.dao.MeetingDao;
import com.mamascode.model.Meeting;
import com.mamascode.model.MeetingDate;
import com.mamascode.model.User;
import com.mamascode.exceptions.UpdateResultCountNotMatchException;

/****************************************************
 * @Deprecated
 * MySqlJdbcMeetingDao: implements MeetingDao(I)
 *
 * uses the Spring JDBC Template. 
 * handling: meetings, meeting_members, meeting_dates
 * 트랜잭션 처리: Service tire
 * 
 * by Hwang Inho
 ****************************************************/

@Deprecated
public class MySqlJdbcMeetingDao implements MeetingDao {
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
	/***** create: create a new meeting  ******/
	@Override
	public int create(Meeting meeting) {
		// check empty strings
		if(!checkMeetingParameter(meeting) || meeting.getAdministratorName() == null
				|| meeting.getAdministratorName().equals("")) {
			// Meeting.administratorName도 체크해준다
			return 0;
		}
		
		// create a meeting
		int meetingResult = createMeeting(meeting);
		
		// get a last inserted meeting id and set a meeting id
		int meetingId = getLastInsertId();
		meeting.setMeetingId(meetingId);
		
		if(meetingResult == 1) {
			// add recommended dates of the meeting to DB
			int mdResult = addMeetingDates(meeting.getMeetingDates());
			
			if(mdResult == meeting.getMeetingDates().size()) {
				// if a return value of addMeetingDates equals 
				// a size of meeting.meetingDates, DB commit
				return meetingResult;
			} else {
				// otherwise, throw exception for DB roll back
				throw new UpdateResultCountNotMatchException(
						"addMeetingDates result does not match");
			}
		} else {
			// failed, throw exception for DB roll back
			throw new UpdateResultCountNotMatchException(
					"meetingResult is not 1");
		}
	}
	
	// createMeeting: internal method... insert meeting information to DB
	private int createMeeting(Meeting meeting) {
		StringBuilder builder = new StringBuilder();
		builder.append("INSERT INTO meetings ")
		.append("(club_name, title, administrator_name, introduction, location, reg_date) ")
		.append("VALUES (?, ?, ?, ?, ?, NOW())");
		String sql = builder.toString();
		
		return jdbcTemplate.update(sql, meeting.getClubName(), 
				meeting.getTitle(), meeting.getAdministratorName(), 
				meeting.getIntroduction(), meeting.getLocation());
	}
	
	// addMeetingDates: internal method... insert meeting dates information to DB
	private int addMeetingDates(List<MeetingDate> meetingDates) {
		int count = 0;
		StringBuilder builder = new StringBuilder();
		builder.append("INSERT INTO meeting_dates ")
		.append("(meeting_id, recommended_date, recommended_time) ")
		.append("VALUES (?, ?, ?)");
		
		String sql = builder.toString();
		
		for(int i = 0; i < meetingDates.size(); i++) {
			final MeetingDate meetingDate = meetingDates.get(i);
			int result = jdbcTemplate.update(sql, meetingDate.getMeetingId(), 
					meetingDate.getRecommendedDate(), meetingDate.getRecommendedTime());
			
			if(result == 1)
				count++;
		}
		
		return count;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** update: update a meeting  ******/
	@Override
	public int update(Meeting meeting) {
		// check empty strings
		if(!checkMeetingParameter(meeting)) {
			return 0;
		}
				
		StringBuilder builder = new StringBuilder();
		builder.append("UPDATE meetings SET title = ?, ")
		.append("introduction = ?, location = ? WHERE meeting_id = ?");
		String sql = builder.toString();
		
		return jdbcTemplate.update(sql, meeting.getTitle(), meeting.getIntroduction(), 
				meeting.getLocation(), meeting.getMeetingId()); 
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** delete: delete a meeting  ******/
	@Override
	public int delete(int meetingId) {
		// get a list of dateId
		List<Integer> dateIds = getDateIds(meetingId);
		
		// delete participants information
		for(int dateId : dateIds) {
			int memberCount = getMeetingMembersCount(dateId);	// count a number of member join a meeting
			
			if(memberCount != deleteMeetingMembers(dateId)) {
				// error: throw runtime exception for transaction roll back
				throw new UpdateResultCountNotMatchException("memberCount does not match");
			}
		}
		
		// delete meeting dates information
		if(deleteMeetingDates(meetingId) != dateIds.size()) {
			// error: throw runtime exception for transaction roll back
			throw new UpdateResultCountNotMatchException(
					"the result of deleteMeetingDates() does not match");
		}
		
		// delete the meeting information
		int result = deleteMeeting(meetingId);
			
		if(result == 1) {
			return result;
		} else {
			// error: throw runtime exception for transaction roll back
			throw new UpdateResultCountNotMatchException("result is not 1");
		}
	}
	
	private List<Integer> getDateIds(int meetingId) {
		// get meeting date identification numbers for deletion
		return jdbcTemplate.query(
			"SELECT date_id FROM meeting_dates WHERE meeting_id = ?", 
			new RowMapper<Integer>() {
				@Override
				public Integer mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					int dateId = rs.getInt(1);
					return dateId;
				}
			}, meetingId);
	}
	
	private int deleteMeetingMembers(int dateId) {
		return jdbcTemplate.update(
				"DELETE FROM meeting_members WHERE date_id = ?", dateId);
	}
	
	private int deleteMeetingDates(int meetingId) {
		return jdbcTemplate.update(
				"DELETE FROM meeting_dates WHERE meeting_id = ?", meetingId);
	}
	
	private int deleteMeeting(int meetingId) {
		return jdbcTemplate.update(
				"DELETE FROM meetings WHERE meeting_id = ?", meetingId);
	}
	
	private int getMeetingMembersCount(int dateId) {
		return jdbcTemplate.queryForInt(
				"SELECT COUNT(*) FROM meeting_members WHERE date_id = ?", dateId);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** getCount: get a size of rows of the meetings table  ******/
	@Override
	public int getCount() {
		return jdbcTemplate.queryForInt("SELECT COUNT(*) FROM meetings");
	}

	/***** get: get a meeting information  ******/
	@Override
	public Meeting get(int meetingId) {
		// get information for a meeting
		Meeting meeting =  jdbcTemplate.query("SELECT * FROM meetings WHERE meeting_id = ?", 
				new Object[] {meetingId}, meetingResultSetExtractor);
		
		// get information for meeting dates
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT md.date_id, md.meeting_id, md.recommended_date, ")
		.append("md.recommended_time, md.date_status, COUNT(mm.date_id) AS count ")
		.append("FROM meeting_dates AS md LEFT OUTER JOIN meeting_members AS mm ")
		.append("ON md.date_id = mm.date_id WHERE md.meeting_id = ? ")
		.append("GROUP BY md.date_id, md.meeting_id, md.recommended_date, ")
		.append("md.recommended_time, md.date_status");
		
		String sql = builder.toString();
		
		List<MeetingDate> meetingDates = jdbcTemplate.query(sql, 
				new Object[] {meetingId}, meetingDateRowMapper);
		
		meeting.setMeetingDates(meetingDates); // set meeting dates
		
		return meeting;
	}
	
	/* meetingDateRowMapper: used in get() */
	private RowMapper<MeetingDate> meetingDateRowMapper = new RowMapper<MeetingDate>() {
		
		@Override
		public MeetingDate mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			MeetingDate meetingDate = new MeetingDate();
			
			meetingDate.setDateId(rs.getInt("date_id"));
			meetingDate.setMeetingId(rs.getInt("meeting_id"));
			meetingDate.setRecommendedDate(rs.getDate("recommended_date"));
			meetingDate.setRecommendedTime(rs.getString("recommended_time"));
			meetingDate.setDateStatus(rs.getShort("date_status"));
			meetingDate.setCountParticipants(rs.getInt("count"));
			
			return meetingDate;
		}
	};
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** getList: get a list of meeting  ******/
	@Override
	public List<Meeting> getList(int offset, int limit) {
		return jdbcTemplate.query(
				"SELECT * FROM meetings ORDER BY reg_date DESC LIMIT ? OFFSET ?", 
				new Object[] {limit, offset}, meetingRowMapper);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** getCountMyClubMeeting: get a size of rows of the meetings of my club  ******/
	@Override
	public int getCountMyClubMeeting(String clubName, int meetingStatus) {
		
		if(meetingStatus == MEETING_STATUS_IGNORE) {
			return jdbcTemplate.queryForInt(
					"SELECT COUNT(*) FROM meetings WHERE club_name = ?", clubName);
		} else {
			return jdbcTemplate.queryForInt(
					"SELECT COUNT(*) FROM meetings WHERE club_name = ? AND meeting_status = ?", 
					clubName, meetingStatus);
		}
	}

	/***** getMyClubMeetingList: get a list of meeting of my club  ******/
	@Override
	public List<Meeting> getMyClubMeetingList(final int offset, final int limit, 
			final String clubName, final int meetingStatus) {
		return jdbcTemplate.query(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				StringBuilder builder = new StringBuilder();
				builder.append("SELECT * FROM meetings WHERE club_name = ? ");
				if(meetingStatus != MEETING_STATUS_IGNORE) {
					builder.append("AND meeting_status = ? ");
				}
				builder.append("ORDER BY reg_date DESC LIMIT ? OFFSET ?");
				String sql = builder.toString();
				
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, clubName);
				
				if(meetingStatus == MEETING_STATUS_IGNORE) {
					ps.setInt(2, limit);
					ps.setInt(3, offset);
				} else {
					ps.setInt(2, meetingStatus);
					ps.setInt(3, limit);
					ps.setInt(4, offset);
				}
				
				return ps;
			}
		}, meetingRowMapper);
	}
	
	/***** getMeetingByDateId ******/
	@Override
	public Meeting getMeetingByDateId(int dateId) {
		int meetingId = jdbcTemplate.queryForInt(
				"SELECT meeting_id FROM meeting_dates WHERE date_id = ?", dateId);
		return get(meetingId);
	}

	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** joinMeeting: join a meeting  ******/
	@Override
	public int joinMeeting(int dateId, String memberName) {
		// empty string filtering
		if(memberName.equals(""))
			return 0;
		
		if(!isExistingMemberName(dateId, memberName)) {
			return jdbcTemplate.update(
					"INSERT INTO meeting_members VALUES (?, ?, NOW())", dateId, memberName);
		} else {
			return 0;
		}
	}
	
	/***** isExistingMemberNo: check if he or she is a existing participant   ******/
	private boolean isExistingMemberName(int dateId, String memberName) {
		if(jdbcTemplate.queryForInt(
				"SELECT COUNT(*) FROM meeting_members WHERE date_id = ? AND user_name = ?",
				dateId, memberName) == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/***** getMeetingParticipants ******/
	@Override
	public List<User> getMeetingParticipants(int dateId, int offset, int limit) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT u.user_name, u.nickname FROM users u, meeting_members m ")
		.append("WHERE u.user_name = m.user_name AND m.date_id = ? LIMIT ? OFFSET ?");
		String sql = builder.toString();
		
		return jdbcTemplate.query(sql, new Object[] {dateId, limit, offset}, 
				new RowMapper<User>() {
			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User user = new User();
				user.setUserName(rs.getString("user_name"));
				user.setNickname(rs.getString("nickname"));
				return user;
			}
		});
	}
	
	/***** getMeetingParticipantsCount ******/
	@Override
	public int getMeetingParticipantsCount(int dateId) {
		return jdbcTemplate.queryForInt(
				"SELECT COUNT(*) FROM meeting_members WHERE date_id = ?",
				dateId);
	}

	/***** setMeetingStatus: set a meeting status  ******/
	@Override
	public int setMeetingStatus(int meetingId, short status) {
		return jdbcTemplate.update(
				"UPDATE meetings SET meeting_status = ? WHERE meeting_id = ?", 
				status, meetingId);
	}
	
	/***** updateMeetingDate: set information of a meeting date  ******/
	@Override
	public int updateMeetingDate(MeetingDate meetingDate) {
		StringBuilder builder = new StringBuilder();
		builder.append("UPDATE meeting_dates SET recommended_date = ?, ")
		.append("recommended_time = ?, date_status = ? WHERE date_id = ?");
		String sql = builder.toString();
		
		return jdbcTemplate.update(sql, 
				meetingDate.getRecommendedDate(), meetingDate.getRecommendedTime(), 
				meetingDate.getDateStatus(), meetingDate.getDateId());
	}
	
	/***** setMeetingDateStatus  ******/
	@Override
	public int setMeetingDateStatus(int dateId, short status) {
		String sql = "UPDATE meeting_dates SET date_status = ? WHERE date_id = ?";
		return jdbcTemplate.update(sql, status, dateId);
	}
	
	/***** setMeetingDateStatusByMeetingId  ******/
	@Override
	public int setMeetingDateStatusByMeetingId(int meetingId, short status) {
		String sql = "UPDATE meeting_dates SET date_status = ? WHERE meeting_id = ?";
		return jdbcTemplate.update(sql, status, meetingId);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// ResultSetExtractors and RowMappers
	
	/* meetingResultSetExtractor */
	private ResultSetExtractor<Meeting> meetingResultSetExtractor = new ResultSetExtractor<Meeting>() {
		
		@Override
		public Meeting extractData(ResultSet rs) throws SQLException, DataAccessException {
			Meeting meeting = null;
			
			if(rs.next()) {
				meeting = makeMeetingFromResultSet(rs);
			} else {
				meeting = new Meeting(); // empty object
			}
			return meeting;
		}
	};
	
	/* meetingRowMapper */
	private RowMapper<Meeting> meetingRowMapper = new RowMapper<Meeting>() {
		@Override
		public Meeting mapRow(ResultSet rs, int rowNum) throws SQLException {
			Meeting meeting = makeMeetingFromResultSet(rs);
			return meeting;
		}
	};
	
	/* makeMeetingFromResultSet */
	private Meeting makeMeetingFromResultSet(ResultSet rs) throws SQLException {
		Meeting meeting = new Meeting();
		
		meeting.setMeetingId(rs.getInt("meeting_id"));
		meeting.setClubName(rs.getString("club_name"));
		meeting.setTitle(rs.getString("title"));
		meeting.setAdministratorName(rs.getString("administrator_name"));
		meeting.setIntroduction(rs.getString("introduction"));
		meeting.setLocation(rs.getString("location"));
		meeting.setMeetingStatus(rs.getShort("meeting_status"));
		meeting.setRegDate(rs.getTimestamp("reg_date"));
		
		return meeting;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// utils
	/* checkMeetingParameter: 3개의 파라미터는 조사
	 * 그 외의 파라미터는 개별 메소드에서 처리 */
	private boolean checkMeetingParameter(Meeting meeting) {
		boolean result = false;
		
		if(!meeting.getClubName().equals("") && 
				!meeting.getTitle().equals("") && !meeting.getIntroduction().equals("")) {
			result = true;
		}
		
		return result;
	}
	
	/***** getLastInsertId: get a last inserted meeting id  ******/
	@Override
	public int getLastInsertId() {
		return jdbcTemplate.queryForInt("SELECT LAST_INSERT_ID()");
	}
	
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
		jdbcTemplate.update("DELETE FROM meeting_members");
		jdbcTemplate.update("DELETE FROM meeting_dates");
		return jdbcTemplate.update("DELETE FROM meetings");
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// temporary
	/***** getMaxMeetingId: get a max meeting id of the meetings table  ******/
	@Override
	public int getMaxMeetingId() {
		return jdbcTemplate.queryForInt("SELECT MAX(meeting_id) FROM meetings");
	}

	@Override
	public int delete(String clubName) {
		return 0;
	}
}
