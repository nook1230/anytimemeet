package com.mamascode.service;

/****************************************************
 * NoticeServiceImpl: NoticeService 구현
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

import com.mamascode.dao.NoticeDao;
import com.mamascode.model.Notice;
import com.mamascode.utils.ListHelper;

@Service
public class NoticeServiceImpl implements NoticeService {
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// Dao
	private @Autowired NoticeDao noticeDao;
	
	public void setNoticeDao(NoticeDao noticeDao) {
		this.noticeDao = noticeDao;
	}
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// constructor(default)
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// implemented methods	

	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////	
	/***** writeNotice: 새 알림 ******/
	@Override
	public int writeNotice(Notice notice) {
		return noticeDao.writeNotice(notice);
	}

	/***** deleteNotice: 알림 삭제 ******/
	@Override
	public int deleteNotice(int noticeId) {
		return noticeDao.deleteNotice(noticeId);
	}

	/***** deleteNotice: 사용자 알림 삭제 ******/
	@Override
	public int deleteNotice(String userName) {
		return noticeDao.deleteNotice(userName);
	}

	/***** readNotice: 알림 읽음 표시 ******/
	@Override
	public int readNotice(int noticeId) {
		return noticeDao.readNotice(noticeId);
	}

	/***** readNoticesOfUser: 사용자 알림 읽음 표시 ******/
	@Override
	public int readNoticesOfUser(String userName) {
		return noticeDao.readNoticesOfUser(userName);
	}

	/***** getNotices: 사용자 알림 목록 ******/
	@Override
	public ListHelper<Notice> getNotices(String userName, int page, int perPage, int read) {
		int totalCount = noticeDao.getCount(userName, read); // 총 개수
		ListHelper<Notice> noticeListHelper = new ListHelper<Notice>(
				totalCount, page, perPage); // 리스트 헬퍼
		
		List<Notice> noticeList = noticeDao.getNotices(userName, 
				noticeListHelper.getOffset(), noticeListHelper.getObjectPerPage(), read);
		noticeListHelper.setList(noticeList); // 알림 리스트 설정
		
		return noticeListHelper;
	}
}
