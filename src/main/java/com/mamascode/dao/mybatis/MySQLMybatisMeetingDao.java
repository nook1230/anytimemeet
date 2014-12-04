package com.mamascode.dao.mybatis;

/****************************************************
 * [MySQLMybatisMeetingDao] - MeetingDao 인터페이스 구현
 * MyBatis를 이용한 Data Access Object
 * 
 * Repository: MySQL 
 * 주요 처리 테이블: meetings, meeting_members, meeting_dates 등
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

import com.mamascode.dao.MeetingDao;
import com.mamascode.exceptions.UpdateResultCountNotMatchException;
import com.mamascode.model.Meeting;
import com.mamascode.model.MeetingDate;
import com.mamascode.model.User;

@Repository
public class MySQLMybatisMeetingDao implements MeetingDao {
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
	private final String NAMESPACE = "com.mamascode.mybatis.mapper.MeetingMapper";
	
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
	
	/***** create: create a new meeting ******/
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
		return sqlSessionTemplate.insert(getMapperId("insertNewMeeting"), meeting);	
	}
	
	// addMeetingDates: internal method... insert meeting dates information to DB
	private int addMeetingDates(List<MeetingDate> meetingDates) {
		int count = 0;
		
		for(int i = 0; i < meetingDates.size(); i++) {
			MeetingDate meetingDate = meetingDates.get(i);
			
			int result = sqlSessionTemplate.insert(getMapperId("insertNewMeetingDate"), meetingDate);
			
			if(result == 1)
				count++;
		}
		
		return count;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	
	/***** update: update a meeting ******/
	@Override
	public int update(Meeting meeting) {
		int result = sqlSessionTemplate.update(getMapperId("updateMeeting"), meeting);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException(
					"MySQLMybatisMeetingDao::update() result is not 1");
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	
	/***** delete: delete a meeting ******/
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
	
	// getDateIds: meetingId에 해당하는 모임 날짜의 식별 번호 가져오기 
	private List<Integer> getDateIds(int meetingId) {
		// get meeting date identification numbers for deletion
		return sqlSessionTemplate.selectList(getMapperId("selectDateIds"), meetingId);
	}
	
	// deleteMeetingMembers: dateId에 참석하기로 한 참석자 정보 모두 삭제
	private int deleteMeetingMembers(int dateId) {
		return sqlSessionTemplate.delete(getMapperId("deleteMeetingMembers"), dateId);
	}
	
	// deleteMeetingDates: meetingId에 해당하는 모임 날짜 모두 삭제
	private int deleteMeetingDates(int meetingId) {
		return sqlSessionTemplate.delete(getMapperId("deleteMeetingDates"), meetingId);
	}
	
	// deleteMeeting: 모임 공지 글 삭제 
	private int deleteMeeting(int meetingId) {
		return sqlSessionTemplate.delete(getMapperId("deleteMeeting"), meetingId);
	}
	
	// getMeetingMembersCount: dateId의 날짜에 참석하기로 한 참석자 수
	private int getMeetingMembersCount(int dateId) {
		return sqlSessionTemplate.selectOne(getMapperId("selectMeetingMembersCount"), dateId);
	}
	
	/***** delete: delete a meeting(for clubName) ******/
	@Override
	public int delete(String clubName) {
		List<Integer> ids = getMeetingIdsForMyClub(clubName);
		int count = 0;
		
		for(int id : ids) {
			count += delete(id);
		}
		
		return count;
	}
	
	// getMeetingIdsForMyClub: 해당 동아리의 모든 모임글 식별번호 가져오기
	private List<Integer> getMeetingIdsForMyClub(String clubName) {
		return sqlSessionTemplate.selectList(getMapperId("selectMeetingIdForMyClub"), clubName);
	}

	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** getCount: get a size of rows of the meetings table ******/
	@Override
	public int getCount() {
		return sqlSessionTemplate.selectOne(getMapperId("selectCount"));
	}
	
	/***** get: get a meeting information ******/
	@Override
	public Meeting get(int meetingId) {
		// get information for a meeting
		Meeting meeting = sqlSessionTemplate.selectOne(getMapperId("selectMeeting"), meetingId);
		
		// get information for meeting dates
		List<MeetingDate> meetingDates = sqlSessionTemplate.selectList(
				getMapperId("selectMeetingDates"), meetingId);
		
		if(meeting != null)
			meeting.setMeetingDates(meetingDates); // set meeting dates
		
		if(meeting == null)
			meeting = new Meeting();
		
		return meeting;
	}
	
	/***** getMeetingByDateId ******/
	@Override
	public Meeting getMeetingByDateId(int dateId) {
		int meetingId = sqlSessionTemplate.selectOne(
				getMapperId("selectMeetingIdByDateId"), dateId);
		return get(meetingId);
	}

	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	
	/***** getList: get a list of meetings ******/
	@Override
	public List<Meeting> getList(int offset, int limit) {
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		return sqlSessionTemplate.selectList(
				getMapperId("selectMeetingList"), 0, rowBounds);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	
	/***** getCountMyClubMeeting: get a size of rows of the meetings of my club ******/
	@Override
	public int getCountMyClubMeeting(String clubName, int meetingStatus) {
		// 파라미터: 해시 맵
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("clubName", clubName);
		hashmap.put("meetingStatus", meetingStatus);
		
		return sqlSessionTemplate.selectOne(getMapperId("selectCountMyClubMeeting"), hashmap);
	}

	/***** getMyClubMeetingList: get a list of meeting of my club ******/
	@Override
	public List<Meeting> getMyClubMeetingList(int offset, int limit,
			String clubName, int meetingStatus) {
		// 파라미터: 해시 맵
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("clubName", clubName);
		hashmap.put("meetingStatus", meetingStatus);
		
		// offset, limit 설정
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		return sqlSessionTemplate.selectList(
				getMapperId("selectMyClubMeetings"), hashmap, rowBounds);
	}

	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	
	/***** joinMeeting: join a meeting ******/
	@Override
	public int joinMeeting(int dateId, String memberName) {
		// empty string filtering
		if(memberName.equals(""))
			return 0;
		
		// 파라미터: 해시 맵
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("dateId", dateId);
		hashmap.put("memberName", memberName);
		
		if(!isExistingMemberName(dateId, memberName)) {
			return sqlSessionTemplate.insert(getMapperId("insertMeetingMember"), hashmap);
		} else
			return 0;
	}
	
	// isExistingMemberNo: check if he or she is a existing participant
	private boolean isExistingMemberName(int dateId, String memberName) {
		// 파라미터: 해시 맵
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("dateId", dateId);
		hashmap.put("memberName", memberName);
		
		int result = sqlSessionTemplate.selectOne(getMapperId("checkMeetingMember"), hashmap);
		
		if(result == 0)
			return false;
		else
			return true;
	}

	/***** setMeetingStatus: set a meeting status ******/
	@Override
	public int setMeetingStatus(int meetingId, short status) {
		// 파라미터: 해시 맵
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("meetingId", meetingId);
		hashmap.put("status", status);
		
		return sqlSessionTemplate.update(getMapperId("updateMeetingStatus"), hashmap);
	}
	
	/***** updateMeetingDate: set information of a meeting date ******/
	@Override
	public int updateMeetingDate(MeetingDate meetingDate) {
		int result = sqlSessionTemplate.update(getMapperId("updateMeetingDate"), meetingDate);
		
		if(result == 1) {
			return result;
		} else {
			// error: throw runtime exception for transaction roll back
			throw new UpdateResultCountNotMatchException("updateMeetingDate() result is not 1");
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	
	/***** setMeetingDateStatus ******/
	@Override
	public int setMeetingDateStatus(int dateId, short status) {
		// 파라미터: 해시 맵
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("dateId", dateId);
		hashmap.put("status", status);
		
		return sqlSessionTemplate.update(getMapperId("updateMeetingDateStatus"), hashmap);
	}
	
	/***** setMeetingDateStatusByMeetingId ******/
	@Override
	public int setMeetingDateStatusByMeetingId(int meetingId, short status) {
		// 파라미터: 해시 맵
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("meetingId", meetingId);
		hashmap.put("status", status);
		
		return sqlSessionTemplate.update(getMapperId("updateMeetingDateStatusByMeetingId"), hashmap);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////

	/***** getMeetingParticipantsCount ******/
	@Override
	public int getMeetingParticipantsCount(int dateId) {
		return sqlSessionTemplate.selectOne(getMapperId("selectCountMeetingMembers"), dateId);
	}
	
	/***** getMeetingParticipants ******/
	@Override
	public List<User> getMeetingParticipants(int dateId, int offset, int limit) {
		// offset, limit 설정
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		return sqlSessionTemplate.selectList(getMapperId("selectMeetingMembers"), dateId, rowBounds);
	}

	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	
	/***** getLastInsertId: get a last inserted meeting id ******/
	@Override
	public int getLastInsertId() {
		return sqlSessionTemplate.selectOne(getMapperId("selectLastInsertId"));
	}
	
	/***** getMaxMeetingId: get a max meeting id of the meetings table ******/
	@Override
	public int getMaxMeetingId() {
		return sqlSessionTemplate.selectOne(getMapperId("selectMaxMeetingId"));
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

}
