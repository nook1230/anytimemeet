package com.mamascode.service;

/****************************************************
 * MeetingServiceImpl: MeetingService 구현
 * 
 * Spring component(@Service)
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mamascode.dao.MeetingDao;
import com.mamascode.dao.MeetingReplyDao;
import com.mamascode.model.Meeting;
import com.mamascode.model.MeetingDate;
import com.mamascode.model.Reply;
import com.mamascode.model.User;
import com.mamascode.utils.ListHelper;

@Service
public class MeetingServiceImpl implements MeetingService {
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// Dao
	@Autowired MeetingDao meetingDao;
	@Autowired MeetingReplyDao meetingReplyDao;
	
	public void setMeetingDao(MeetingDao meetingDao) {
		this.meetingDao = meetingDao;
	}
	
	public void setMeetingReplyDao(MeetingReplyDao meetingReplyDao) {
		this.meetingReplyDao = meetingReplyDao;
	}
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// constructor(default)
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// implemented methods
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** openNewMeeting: open(write) a new meeting  ******/
	@Override
	public boolean openNewMeeting(Meeting meeting) {
		int result = meetingDao.create(meeting);
		
		if(result == 1)
			return true;
		
		return false;
	}
	
	/***** setMeeting: set meeting information  ******/
	@Override
	public boolean setMeeting(Meeting meeting) {
		int result = meetingDao.update(meeting);
		
		if(result == 1)
			return true;
		
		return false;
	}
	
	/***** setMeetingDate: set a date of meeting  ******/
	@Override
	public boolean setMeetingDate(MeetingDate meetingDate) {
		int result = meetingDao.updateMeetingDate(meetingDate);
		
		if(result == 1)
			return true;
		
		return false;
	}
	
	/***** setMeetingStatus: set meeting status  ******/
	@Override
	public boolean setMeetingStatus(int meetingId, short status) {
		int result = meetingDao.setMeetingStatus(meetingId, status);
		
		if(result == 1)
			return true;
		
		return false;
	}
	
	/***** decideMeetingDate ******/
	@Override
	public boolean decideMeetingDate(int meetingId, int dateId) {
		// 모든 날짜의 상태를 2로 변경(화면에서 보이지 않는 상태)
		meetingDao.setMeetingDateStatusByMeetingId(meetingId, (short) 2);
		
		int result = meetingDao.setMeetingDateStatus(dateId, (short) 1);
		
		if(result == 1)
			return true;
		
		return false;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** deleteMeeting ******/
	@Override
	public boolean deleteMeeting(int meetingId) {
		// 모임 공지 글에 있는 댓글 삭제
		meetingReplyDao.deleteReplyOfMeeting(meetingId);
		
		// 모임 글 삭제
		int result = meetingDao.delete(meetingId);
		
		if(result == 1)
			return true;
		return false;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** getMeeting: get meeting information  ******/
	@Override
	public Meeting getMeeting(int meetingId) {
		return meetingDao.get(meetingId);
	}	

	/***** getMeetingByDateId: get meeting information  ******/
	@Override
	public Meeting getMeetingByDateId(int dateId) {
		return meetingDao.getMeetingByDateId(dateId);
	}

	/***** getMeetingList: get a total meeting list  ******/
	@Override
	public ListHelper<Meeting> getMeetingList(int page, int perPage) {
		int count = meetingDao.getCount();
		List<Meeting> meetings = null;
		ListHelper<Meeting> listHelper = new ListHelper<Meeting>(count, page, perPage);
		
		meetings = meetingDao.getList(listHelper.getOffset(), listHelper.getObjectPerPage());
		
		listHelper.setList(meetings);
		return listHelper;
	}
	
	/***** getMyClubMeetingList: get a meeting list of my club  ******/
	@Override
	public ListHelper<Meeting> getMyClubMeetingList(String clubName, int meetingStatus, 
			int page, int perPage) {
		int count = meetingDao.getCountMyClubMeeting(clubName, meetingStatus);
		List<Meeting> meetings = null;
		ListHelper<Meeting> listHelper = new ListHelper<Meeting>(count, page, perPage);
		
		meetings = meetingDao.getMyClubMeetingList(
				listHelper.getOffset(), listHelper.getObjectPerPage(), 
				clubName, meetingStatus);
		
		if(meetings != null) {
			for(Meeting meeting : meetings) {
				meeting.setRepliesCount(meetingReplyDao.getCountReplies(meeting.getMeetingId()));
			}
		}
		
		listHelper.setList(meetings);
		return listHelper;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** joinMeeting: join a meeting that is held on the date has a given dateId  ******/
	@Override
	public boolean joinMeeting(int dateId, String clubName) {
		int result = meetingDao.joinMeeting(dateId, clubName);
		
		if(result == 1)
			return true;
		
		return false;
	}
	
	/***** getParticipants: get a list of participants of a meeting  ******/
	@Override
	public ListHelper<User> getParticipants(int dateId, int page, int perPage) {
		int totalCount = meetingDao.getMeetingParticipantsCount(dateId);
		
		ListHelper<User> listHelper = new ListHelper<User>(totalCount, page, perPage);
		
		List<User> participants = meetingDao.getMeetingParticipants(
				dateId, listHelper.getOffset(), listHelper.getObjectPerPage());
		
		listHelper.setList(participants);
		return listHelper;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// 댓글 관리
	
	/***** writeNewMeetingReply: 새 댓글 쓰기 ******/
	@Override
	public boolean writeNewMeetingReply(Reply reply) {
		int result = meetingReplyDao.create(reply);
		
		if(result == 1)
			return true;
		
		return false;
	}
	
	/***** deleteMeetingReply: 댓글 삭제 ******/
	@Override
	public boolean deleteMeetingReply(int replyId) {
		int result = meetingReplyDao.delete(replyId);
		
		if(result == 1)
			return true;
		
		return false;
	}
	
	/***** updateMeetingReply: 댓글 수정 ******/
	@Override
	public boolean updateMeetingReply(Reply reply) {
		int result = meetingReplyDao.update(reply);
		
		if(result == 1)
			return true;
		
		return false;
	}
	
	/***** readMeetingReplies: 모임 글에 달린 댓글 가져오기 ******/
	@Override
	public List<Reply> readMeetingReplies(int meetingId) {
		List<Reply> replyList = meetingReplyDao.getReplies(meetingId);
		return replyList;
	}
	
	/***** readMeetingReply: 댓글 보기 ******/
	@Override
	public Reply readMeetingReply(int replyId) {
		return meetingReplyDao.get(replyId);
	}
}
