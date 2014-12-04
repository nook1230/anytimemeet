package com.mamascode.dao;

/****************************************************
 * NoticeDao: interface
 * Date access object
 * 
 * Model: Notice
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import java.util.List;

import com.mamascode.model.Notice;

public interface NoticeDao {
	///////// Create, Update, Delete
	int writeNotice(Notice notice);
	int deleteNotice(int noticeId);
	int deleteNotice(String userName);
	int readNotice(int noticeId);
	int readNoticesOfUser(String userName);
	
	///////// query
	int getCount();
	int getCount(String userName);
	int getCount(String userName, int read);
	List<Notice> getNotices(String userName, int offset, int limit, int read);
	
	///////// for test
	int deleteNoticeAll();
}
