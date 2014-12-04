package com.mamascode.service;

/****************************************************
 * MeetingService: interface
 * 선언적 트랜잭션 적용
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mamascode.model.Meeting;
import com.mamascode.model.MeetingDate;
import com.mamascode.model.Reply;
import com.mamascode.model.User;
import com.mamascode.utils.ListHelper;

@Transactional(propagation=Propagation.SUPPORTS, readOnly=true) // 기본 전파 속성:  Supports, 읽기 전용
public interface MeetingService {
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// constants
	final static int MEETING_STATUS_IGNORE = -1;
	final static int MEETING_STATUS_DEFAULT = 0;
	final static int MEETING_STATUS_CONFIRMED = 1;
	final static int MEETING_STATUS_CANCELED = 2;
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// interface methods
	
	/////// create, update
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean openNewMeeting(Meeting meeting);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean setMeeting(Meeting meeting);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean setMeetingDate(MeetingDate meetingDate);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean setMeetingStatus(int meetingId, short status);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean deleteMeeting(int meetingId);
	
	/////// information
	Meeting getMeeting(int meetingId);
	Meeting getMeetingByDateId(int dateId);
	ListHelper<Meeting> getMeetingList(int page, int perPage);
	ListHelper<Meeting> getMyClubMeetingList(String clubName, 
	int meetingStatus, int page, int perPage);
	
	/////// join a meeting
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean joinMeeting(int dateId, String memberName);
	
	ListHelper<User> getParticipants(int dateId, int page, int perPage);
	
	/////// decide a meeting date
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public boolean decideMeetingDate(int meetingId, int dateId);
	
	/////// Reply
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean writeNewMeetingReply(Reply reply);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean deleteMeetingReply(int replyId);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean updateMeetingReply(Reply reply);
	
	List<Reply> readMeetingReplies(int meetingId);
	Reply readMeetingReply(int replyId);
}
