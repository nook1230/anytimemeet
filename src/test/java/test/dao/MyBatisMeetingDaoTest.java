package test.dao;

/*********************************************
 * MyBatisMeetingDaoTest
 * 
 * test #1: 2014. 10. 29 
 *********************************************/

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mamascode.dao.MeetingDao;
import com.mamascode.model.Meeting;
import com.mamascode.model.MeetingDate;
import com.mamascode.service.MeetingService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/application-config.xml"})
public class MyBatisMeetingDaoTest {
	private static int testCount = 0;
	private Meeting meeting;
	@Autowired private MeetingDao meetingDao;
	@Autowired private MeetingService meetingService;
	private Logger logger = LoggerFactory.getLogger(MyBatisMeetingDaoTest.class);
	
	@Before
	public void setUp() {
		// set a meeting object
		meeting = new Meeting();
		meeting.setClubName("test");
		meeting.setAdministratorName("mmuse1230");
		meeting.setTitle("정기 모임 고고");
		meeting.setIntroduction("공부 하기 싫은 사람, 모여라~");
		meeting.setLocation("종로 인사동");
		
		// set meeting dates
		MeetingDate meetingDate1 = new MeetingDate();
		MeetingDate meetingDate2 = new MeetingDate();
		Calendar cal1 = new GregorianCalendar(2014, 5, 7, 18, 30, 0);
		Calendar cal2 = new GregorianCalendar(2014, 5, 14, 18, 00, 0);
		
		Date date1 = new Date(cal1.getTimeInMillis());
		Date date2 = new Date(cal2.getTimeInMillis());
		
		meetingDate1.setRecommendedDate(date1);
		meetingDate1.setRecommendedTime(new Time(cal1.getTimeInMillis()).toString());
		meetingDate2.setRecommendedDate(date2);
		meetingDate2.setRecommendedTime(new Time(cal2.getTimeInMillis()).toString());
		
		List<MeetingDate> meetingDates = new ArrayList<MeetingDate>();
		meetingDates.add(meetingDate1);
		meetingDates.add(meetingDate2);
		
		meeting.setMeetingDates(meetingDates);
		
		System.out.println("\ntest setup complete! #" + (++testCount));
	}
	
	//@Test
	public void count() {
		assertThat(meetingDao.getMaxMeetingId(), is(1));
		assertThat(meetingDao.getCount(), is(1));
	}
	
	//@Test
	public void createAndDeleteTEST() {
		createMeeting();
		deleteMeeting();
	}
	
	@Test
	public void getMeeting() {
		Meeting meeting = meetingDao.get(meetingDao.getMaxMeetingId());
		assertThat(meeting, is(notNullValue()));
		printMeeting(meeting);
	}
	
	//@Test
	public void getList() {
		createMeeting();
		
		List<Meeting> meetings = meetingDao.getList(0, 10);
		assertThat(meetings.size(), is(2));
		
		printMeetingList(1, "meeting list", meetings);
		
		deleteMeeting();
	}
	
	@Test
	public void getCountMyClubMeeting() {
		assertThat(meetingDao.getCountMyClubMeeting(
				"test", MeetingDao.MEETING_STATUS_IGNORE), is(1));
		
		assertThat(meetingDao.getCountMyClubMeeting(
				"test", MeetingDao.MEETING_STATUS_CONFIRMED), is(0));
	}
	
	public void createMeeting() {
		int count = meetingDao.getCount();
		
		// 한 세션 안에서 실행되어야 하는 메소드들 때문에 테스트는 MeetingService를 이용
		assertTrue(meetingService.openNewMeeting(meeting));
		assertThat(meetingDao.getCount(), is(count+1));
	}
	
	//@Test
	public void deleteMeeting() {
		int count = meetingDao.getCount();
		int maxId = meetingDao.getMaxMeetingId();
		
		// 한 세션 안에서 실행되어야 하는 메소드들 때문에 테스트는 MeetingService를 이용
		assertTrue(meetingService.deleteMeeting(maxId));
		assertThat(meetingDao.getCount(), is(count-1));
	}
	
	private void printMeetingList(int testNo, String testName, List<Meeting> meetings) {
		for(Meeting meeting : meetings) {
			printMeeting(meeting);
		}
	}
	
	private void printMeeting(Meeting meeting) {
		logger.info("title: #{} {}", meeting.getMeetingId(), meeting.getTitle());
		logger.info("introdunction: {}", meeting.getIntroduction());
		
		if(meeting.getMeetingDates() != null) {
			for(MeetingDate meetingDate : meeting.getMeetingDates()) {
				if(meetingDate != null) {
					logger.info("\t#{} | {} {}", meetingDate.getDateId(),
							meetingDate.getRecommendedDate(), meetingDate.getRecommendedTime());
					logger.info("\tstatus: {}", meetingDate.getDateStatus());
					logger.info("\tparticipants: {}", meetingDate.getCountParticipants());
					logger.info("\t----------------------");
				}
			}
		}
		logger.info("");
	}
}
