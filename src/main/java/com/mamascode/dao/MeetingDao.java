package com.mamascode.dao;

/****************************************************
 * MeetingDao: interface
 * Date access object
 * 
 * Model: Meeting
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import java.util.List;

import com.mamascode.model.Meeting;
import com.mamascode.model.MeetingDate;
import com.mamascode.model.User;

public interface MeetingDao {
	///////// constant
	final static int MEETING_STATUS_IGNORE = -1;
	final static int MEETING_STATUS_DEFAULT = 0;
	final static int MEETING_STATUS_CONFIRMED = 1;
	final static int MEETING_STATUS_CANCELED = 2;
	
	///////// Create, Update, Delete
	int create(Meeting meeting);	// create a meeting
	int update(Meeting meeting);	// update a meeting information
	int delete(int meetingId);		// delete a meeting
	int delete(String clubName);	
	
	///////// get a size of rows of the meetings table
	int getCount();
	
	///////// get meeting information
	Meeting get(int meetingId);
	
	///////// meeting list
	List<Meeting> getList(int offset, int limit);
	
	///////// set a status of a meeting date
	public int setMeetingDateStatus(int dateId, short status);
	public int setMeetingDateStatusByMeetingId(int meetingId, short status);
	
	///////// get meeting information of my club
	int getCountMyClubMeeting(String clubName, int meetingStatus);
	List<Meeting> getMyClubMeetingList(int offset, int limit, String clubName, int meetingStatus);
	Meeting getMeetingByDateId(int dateId);
	
	///////// join a meeting and set a meeting status, update meeting date
	int joinMeeting(int dateId, String memberName); // join a meeting
	int setMeetingStatus(int meetingId, short status); // set a meeting status
	int updateMeetingDate(MeetingDate meetingDate);	// set information of a meeting date
	int getMeetingParticipantsCount(int dateId);
	List<User> getMeetingParticipants(int dateId, int offset, int limit);
	
	///////// temporary
	int getLastInsertId(); // get a last inserted meeting id
	int getMaxMeetingId(); // get a max meeting id of the meetings table
}
