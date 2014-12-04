package com.mamascode.service;

/****************************************************
 * NoticeService: interface
 * 트랜잭션 미적용(알림은 무결성을 유지할 필요 없음)
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import com.mamascode.model.Notice;
import com.mamascode.utils.ListHelper;

public interface NoticeService {
	///////// const
	final static int NOTICE_READ_IGNORE = 0;
	final static int NOTICE_READ_READED = 1;
	final static int NOTICE_READ_UNREADED = 2;
	
	final static short NOTICE_TYPE_GENERAL = 1;
	final static short NOTICE_TYPE_MASTER = 2;
	
	///////// Create, Update, Delete
	int writeNotice(Notice notice);
	int deleteNotice(int noticeId);
	int deleteNotice(String userName);
	int readNotice(int noticeId);
	int readNoticesOfUser(String userName);
	
	///////// query
	ListHelper<Notice> getNotices(String userName, int page, int perPage, int read);
}
