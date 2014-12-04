package test.dao;

/*********************************************
 * MyBatisNoticeDaoTest
 * 
 * test #1: 2014. 11. 12
 *********************************************/

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mamascode.dao.NoticeDao;
import com.mamascode.model.Notice;
import com.mamascode.service.NoticeService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/application-config.xml"})
public class MyBatisNoticeDaoTest {
	private static int testCount = 0;
	@Autowired private NoticeDao noticeDao;
	
	private Logger logger = LoggerFactory.getLogger(MyBatisClubDaoTest.class);
	
	private Notice notice1;
	private Notice notice2;
	
	/////////////////////////////////////////////////////////////////////////
	// test setup
	
	@Before
	public void setUp() {
		notice1 = new Notice();
		notice2 = new Notice();
		
		notice1.setUserName("mmuse1230");
		notice1.setNoticeMsg("동아리에 가입하셨습니당");
		notice1.setNoticeType((short) 1); // 일반
		notice1.setNoticeUrl("club/nomorealonemeal/clubMain");
		notice1.setExtra("nomorealonemeal");
		
		notice2.setUserName("mmuse1230");
		notice2.setNoticeMsg("그냥 한 번 불러봤습니당");
		notice2.setNoticeType((short) 2); // 마스터
		
		System.out.println("test setup complete! #" + (++testCount));
	}
	
	/////////////////////////////////////////////////////////////////////////
	// test
	@Test
	public void totalTest() {
		noticeDao.deleteNoticeAll();
		assertThat(noticeDao.getCount(), is(0));
		
		assertThat(noticeDao.writeNotice(notice1), is(1));
		assertThat(noticeDao.writeNotice(notice2), is(1));
		
		assertThat(noticeDao.getCount(), is(2));
		
		List<Notice> notices = noticeDao.getNotices("mmuse1230", 0, 10, NoticeService.NOTICE_READ_UNREADED);
		assertThat(notices.size(), is(2));
		printNotices(1, "notice list test", notices);
		
		assertThat(noticeDao.readNoticesOfUser("mmuse1230"), is(2));
		notices = noticeDao.getNotices("mmuse1230", 0, 10, NoticeService.NOTICE_READ_READED);
		assertTrue(notices.get(0).isNoticeRead());
		printNotices(2, "notice read test", notices);
		
		notices = noticeDao.getNotices("mmuse1230", 0, 10, NoticeService.NOTICE_READ_IGNORE);
		printNotices(3, "print notice", notices);
		
		assertThat(noticeDao.deleteNotice("mmuse1230"), is(2));
		assertThat(noticeDao.getCount(), is(0));
	}
	
	private void printNotices(int testNo, String testTitle, List<Notice> notices) {
		logger.info("---------------------------------------------------");
		logger.info("#{} {}", testNo, testTitle);
		for (Notice notice : notices) {
			printNotice(notice);
		}
		logger.info("---------------------------------------------------");
	}
	
	private void printNotice(Notice notice) {
		logger.info("\t#{} [{}]", notice.getNoticeId(), notice.getUserName());
		logger.info("\t{}", notice.getNoticeDate());
		logger.info("\t\"{}\"", notice.getNoticeMsg());
		
		String type = (notice.getNoticeType() == 1) ? "G" : "M";
		logger.info("\ttype: {}, extra: {}, read: {}", type, notice.getExtra(), notice.isNoticeRead());
		logger.info("\turl: {}", notice.getNoticeUrl());
		logger.info("");
	}
}
