package com.mamascode.controller;

/****************************************************
 * ClubMeetingController
 *
 * annotation 기반 핸들러 
 * 관리 url: /club/meeting
 * 
 * 동아리 모임 관련 입력/화면 처리
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.mamascode.model.Meeting;
import com.mamascode.model.MeetingDate;
import com.mamascode.model.Notice;
import com.mamascode.model.Reply;
import com.mamascode.model.User;
import com.mamascode.model.utils.ProcessingResult;
import com.mamascode.service.ClubService;
import com.mamascode.service.MeetingService;
import com.mamascode.service.NoticeService;
import com.mamascode.service.UserService;
import com.mamascode.utils.ListHelper;
import com.mamascode.utils.SecurityUtil;
import com.mamascode.utils.SessionUtil;
import com.mamascode.utils.Validation;
import com.mamascode.model.Club;
import com.mamascode.model.utils.PopupInfo;

@Controller
@SessionAttributes({"meeting"})
@RequestMapping("/club/meeting")
public class ClubMeetingController {
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// 서비스 인터페이스
	@Autowired private ClubService clubService;
	@Autowired private UserService userService;
	@Autowired private MeetingService meetingService;
	@Autowired private NoticeService noticeService;
	
	public void setClubService(ClubService clubService) {
		this.clubService = clubService;
	}
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	public void setMeetingService(MeetingService meetingService) {
		this.meetingService = meetingService;
	}
	
	public void setNoticeService(NoticeService noticeService) {
		this.noticeService = noticeService;
	}

	// controller methods //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// 동아리 모임
	
	////////////////////////////////////////////////////////////////////////////////
	// 동아리 모임 개설
	/* openMeetingForm: 모임 개설 폼(GET) */
	@RequestMapping(value="/{clubName}/openMeeting", method=RequestMethod.GET)
	public String openMeetingForm(@PathVariable String clubName, Model model, HttpSession session) {
		// 동아리 정보
		Club club = clubService.getClubInformation(clubName);
		
		// 모임 정보
		Meeting meeting = new Meeting();
		meeting.setAdministratorName(SessionUtil.getLoginUserName(session));
		
		// 모델 바인딩
		bindUpdateObjectToModel(club, meeting, session, model, OpenMeetingCheckAuthCallback);
		
		return "forms/open_meeting_form";
	}
	
	/* openMeeting: 모임 개설(POST) */
	@RequestMapping(value="/{clubName}/openMeeting", method=RequestMethod.POST)
	public String openMeeting(@PathVariable String clubName, 
			@RequestParam("title") String title, 
			@RequestParam("administratorName") String administratorName,
			@RequestParam("location") String location, 
			@RequestParam("introduction") String introduction,
			@RequestParam("JsonMeetingDates") String JsonMeetingDates,
			SessionStatus sessionStatus, Model model, HttpSession session) {
		
		// 필터링 및 meeting 모델 객체 생성(새로 모임을 생성하기 때문에 meeting 객체로는 null을 전달)
		Meeting meeting = filteringMeetingModel(null, title, administratorName, 
				location, introduction, clubName);
		
		// 회원들에게 제안할 날짜 정보 목록
		List<MeetingDate> meetingDates = new ArrayList<MeetingDate>();
		
		// JSON 문자열 파싱
		JSONParser parser = new JSONParser();
		
		try {
			if(meeting != null) {
				// meeting이 null이 아닌 경우
				JSONObject jobj = (JSONObject) parser.parse(JsonMeetingDates);
				JSONArray jarr = new JSONArray();
				jarr = (JSONArray) jobj.get("MeetingDates");
				
				for(Object obj : jarr) {
					JSONObject JsonDate = (JSONObject) obj;
					String date = (String) JsonDate.get("date");
					String time = (String) JsonDate.get("time");
					
					MeetingDate meetingDate = new MeetingDate();
					meetingDate.setRecommendedDate(Validation.strToDate(date));
					meetingDate.setRecommendedTime(time);
					
					meetingDates.add(meetingDate);
				}
				
				// 파싱된 날짜 정보를 모임 정보에 취합
				meeting.setMeetingDates(meetingDates);
			}
			
			if(meeting == null || meeting.getMeetingDates().size() < 1 || !meetingService.openNewMeeting(meeting)) {
				/* 오류 발생!!! 폼 재출력(사용자가 기존에 입력한 정보를 유지 - 날짜 정보 제외) */ 
				// 클럽 정보
				Club club = clubService.getClubInformation(clubName);
				
				// 모델 바인딩
				bindUpdateObjectToModel(club, meeting, session, model, OpenMeetingCheckAuthCallback);
								
				return "forms/open_meeting_form";
			}
			
			/* 성공! */
			sessionStatus.setComplete();	// 세션 정리
			return "redirect:/club/" + clubName + "/clubMain";	// 성공 시 동아리 메인 화면으로
				
		} catch (ParseException e) {
			// JSON 파싱 예외: 에러 페이지
			e.printStackTrace();
			model.addAttribute("ex", e);
			return "errors/json_parsing_error";
		}
	}
	
	// 모임 개설 접근 권한 체크 콜백
	private CheckAuthorityCallback OpenMeetingCheckAuthCallback = new CheckAuthorityCallback() {
		@Override
		public boolean checkAuth(String clubName, Meeting meeting, HttpSession session) {
			return userService.isThisUserClubMaster(SessionUtil.getLoginUserName(session), clubName)
					|| userService.isThisUserClubCrew(SessionUtil.getLoginUserName(session), clubName);
		}
	};
	
	////////////////////////////////////////////////////////////////////////////////
	// 동아리 모임 날짜
	/* decideMeetingDateForm: 동아리 모임 날짜 확정 폼(팝업) */
	@RequestMapping(value="/{clubName}/decide", method=RequestMethod.GET)
	public String decideMeetingDateForm(@PathVariable String clubName, Model model,
			@RequestParam("id") int meetingId, @RequestParam("dateId") int dateId,
			HttpSession session) {
		
		// 필요한 정보들
		Meeting meeting = meetingService.getMeeting(meetingId);
		MeetingDate meetingDate = null;
		
		for(MeetingDate date : meeting.getMeetingDates()) {
			if(date.getDateId() == dateId)
				meetingDate = date;
		}
		
		/////////// 팝업 정보
		String title = "동아리 모임 날짜 확정";	// 타이틀
		
		// 코멘트
		StringBuilder builder = new StringBuilder();
		builder.append("이 모임의 날짜를 <br />").append(meetingDate.getRecommendedDate()).append(" ")
		.append(meetingDate.getRecommendedTime()).append("으로<br />확정하시겠습니까?");
		String comment = builder.toString();
		
		String url = "club/meeting/" + clubName + "/decide";	// post action url
		boolean access = SessionUtil.isLoginStatus(session) &&
				(userService.isThisUserClubMaster(SessionUtil.getLoginUserName(session), clubName))
						|| SessionUtil.getLoginUserName(session).equals(meeting.getAdministratorName());
		
		PopupInfo pInfo = new PopupInfo(title, comment, url, access);
		
		// hidden fields 
		Map<String, Object> hiddens = new HashMap<String, Object>();
		hiddens.put("meetingId", meeting.getMeetingId());
		hiddens.put("dateId", meetingDate.getDateId());		
		pInfo.setHiddens(hiddens);
		
		// 모델 바인딩(팝업 객체)
		model.addAttribute("popupInfo", pInfo);
		
		return "forms/popup/popup_basic";
	}
	
	/* decideMeetingDate: 동아리 모임 날짜 확정 */
	@RequestMapping(value="/{clubName}/decide", method=RequestMethod.POST)
	public String decideMeetingDate(@PathVariable String clubName,
			@RequestParam("meetingId") int meetingId, 
			@RequestParam("dateId") int dateId, Model model) {
		// 처리 결과 정보 객체
		ProcessingResult result = new ProcessingResult(
				"동아리 모임 날짜 확정", "동아리 모임 날짜 확정", 
				"처리가 완료되었습니다", "처리 중 오류 발생");
		
		if(meetingService.decideMeetingDate(meetingId, dateId)) {
			result.setResult(true);
			
			//************ 동아리 회원들에게 알림 전송
			// 동아리 회원 이름 가져오기
			List<String> clubMemberNames = clubService.getClubMemberNames(clubName);
			Meeting meeting = meetingService.getMeeting(meetingId);
			
			// 알림 내용
			String title = (meeting.getTitle().length() <= 20) ? meeting.getTitle() :
				(meeting.getTitle().substring(0, 20) + "...");
			String noticeMsg = new StringBuilder().append("다음 모임의 날짜가 확정되었습니다: ")
					.append(title).toString();
			// 알림 url(모임 글 읽기)
			String noticeUrl = new StringBuilder().append("club/meeting/")
					.append(clubName).append("/meetingDetail?id=").append(meetingId).toString();
			
			for(String memberName : clubMemberNames) {
				// 알림 객체
				Notice notice = new Notice(memberName, noticeMsg, noticeUrl);
				
				// 알림 삽입
				noticeService.writeNotice(notice);
			}
		} else {
			// 실패
			result.setErrorCause("알 수 없는 오류");
		}
		
		model.addAttribute("result", result);	// 처리 결과
		return "results/processing_result";
	}
	
	////////////////////////////////////////////////////////////////////////////////
	// 동아리 모임 정보
	/* getMeeting: 동아리 모임 세부 정보 */
	@RequestMapping(value="/{clubName}/meetingDetail", method=RequestMethod.GET)
	public String getMeeting(
			@RequestParam(value="id", required=false) int meetingId, 
			@PathVariable String clubName, Model model, HttpSession session) {
		
		boolean checkClubMember = false;	// 동아리 회원인지 체크
		boolean checkAdmin = false;
		Meeting meeting = meetingService.getMeeting(meetingId);	// 모임 정보
		Club club = clubService.getClubInformation(clubName);	// 동아리 정보
		List<Reply> meetingReplyList = meetingService.readMeetingReplies(meetingId); // 게시물에 대한 댓글
		
		if(session != null && session.getAttribute(SessionUtil.loginUserNameAttr) != null) {
			String loginUserName = (String) session.getAttribute(SessionUtil.loginUserNameAttr);
			
			// 사용자가 동아리 회원인지
			checkClubMember = clubService.isThisUserInThisClub(clubName, loginUserName);
			
			// 사용자가 모임 관리 권한이 있는 회원인지
			if(userService.isThisUserClubMaster(SessionUtil.getLoginUserName(session), clubName) || 
					(meeting.getAdministratorName() != null &&
					meeting.getAdministratorName().equals(loginUserName))) {
				checkAdmin = true;
			}
		}
		
		// 모델 바인딩
		model.addAttribute("checkClubMember", checkClubMember);
		model.addAttribute("checkAdmin", checkAdmin);
		model.addAttribute("meeting", meeting);
		model.addAttribute("meetingReplyList", meetingReplyList);
		model.addAttribute("club", club);
		
		return "view_meeting_detail";
	}
	
	/* meetingList: 동아리 모임 목록 */
	@RequestMapping(value="/{clubName}/meetingList", method=RequestMethod.GET)
	public String meetingList(@PathVariable String clubName, 
			@RequestParam(value="page", required=false, defaultValue="1") int page,
			Model model, HttpSession session) {
		// 접근 권한 체크(동아리 회원 여부)
		boolean checkClubMember = false;
		boolean checkLogin = false;
		
		checkClubMember = clubService.isThisUserInThisClub(clubName, 
				SessionUtil.getLoginUserName(session));
		checkLogin = SessionUtil.isLoginStatus(session);
		
		// 모임 목록(모임 상태 필터링은 무시)
		ListHelper<Meeting> listHelper = 
				meetingService.getMyClubMeetingList(clubName, 
						MeetingService.MEETING_STATUS_IGNORE, page, 20);
		
		// 모델 바인딩
		model.addAttribute("checkClubMember", checkClubMember);
		model.addAttribute("checkLogin", checkLogin);
		model.addAttribute("meetingListHelper", listHelper);
		
		return "meeting_list";
	}
	
	////////////////////////////////////////////////////////////////////////////////
	// 동아리 모임 참가
	/* participateMeetingForm: 동아리 모임 참가 폼(팝업) */
	@RequestMapping(value="/{clubName}/participate", method=RequestMethod.GET)
	public String participateMeetingForm(@PathVariable String clubName,
			@RequestParam("id") int meetingId, 
			@RequestParam("dateId") int dateId, Model model, HttpSession session) {
		// 필요한 정보들
		Meeting meeting = meetingService.getMeeting(meetingId);
		MeetingDate meetingDate = null;
		
		for(MeetingDate date : meeting.getMeetingDates()) {
			if(date.getDateId() == dateId)
				meetingDate = date;
		}
				
		/////////// 팝업 정보
		String title = "동아리 모임 참가";	// 타이틀
		
		// 코멘트
		StringBuilder builder = new StringBuilder();
		builder.append(meetingDate.getRecommendedDate()).append(" ")
		.append(meetingDate.getRecommendedTime()).append(" 모임에 참가하시겠습니까?");
		String comment = builder.toString();
		
		String url = "club/meeting/" + clubName + "/participate";	// post action url
		
		// 접근 권한 체크: 로그인 상태 & 동아리 멤버
		String loginUserName = (String) session.getAttribute(SessionUtil.loginUserNameAttr);
		boolean access = (SessionUtil.isLoginStatus(session) 
				&& clubService.isThisUserInThisClub(clubName, loginUserName));
		
		PopupInfo pInfo = new PopupInfo(title, comment, url, access);
		
		// hidden fields 
		Map<String, Object> hiddens = new HashMap<String, Object>();
		hiddens.put("userName", loginUserName);
		hiddens.put("dateId", meetingDate.getDateId());		
		pInfo.setHiddens(hiddens);
		
		// 모델 바인딩(팝업 객체)
		model.addAttribute("popupInfo", pInfo);
		
		return "forms/popup/popup_basic";
	}
	
	/* participateMeeting: 동아리 모임 참가 처리(POST) */
	@RequestMapping(value="/{clubName}/participate", method=RequestMethod.POST)
	public String participateMeeting(@RequestParam("userName") String userName,
			@RequestParam("dateId") int dateId, Model model) {
		// 처리 결과 정보 객체
		ProcessingResult result = new ProcessingResult(
				"동아리 모임 참가", "동아리 모임 참가", 
				"참가 신청이 완료되었습니다", "처리 중 오류 발생");
		
		if(meetingService.joinMeeting(dateId, userName)) {
			result.setResult(true);
		} else {
			result.setErrorCause("이미 참석하기로 한 모임입니다");
		}
		
		model.addAttribute("result", result);
		
		return "results/processing_result";
	}
	
	/* participateMeeting: 동아리 모임 참가자 명단 */
	@RequestMapping(value="/{clubName}/participants", method=RequestMethod.GET)
	public String getParticipants(@PathVariable String clubName,
			@RequestParam(value="dateId", required=true, defaultValue="1") int dateId,
			@RequestParam(value="page", required=true, defaultValue="1") int page,
			Model model, HttpSession session) {
		// 모임 정보
		Meeting meeting = meetingService.getMeetingByDateId(dateId);
		MeetingDate theDate = null;
		for(MeetingDate date : meeting.getMeetingDates()) {
			if(date.getDateId() == dateId)
				theDate = date;
		}
		
		// 참가자 명단
		ListHelper<User> participantListHelper = meetingService.getParticipants(dateId, page, 20);
		
		// 접근권한: 동아리 멤버인지 확인
		String loginUserName = (String) session.getAttribute(SessionUtil.loginUserNameAttr);
		boolean checkMember = (SessionUtil.isLoginStatus(session) 
				&& clubService.isThisUserInThisClub(clubName, loginUserName));
		
		model.addAttribute("checkMember", checkMember);
		model.addAttribute("theDate", theDate);
		model.addAttribute("meeting", meeting);
		model.addAttribute("clubName", clubName);
		model.addAttribute("participantListHelper", participantListHelper);
		
		return "results/meeting_participants";
	}
	
	////////////////////////////////////////////////////////////////////////////////
	// 동아리 모임 내용 수정
	/* modifyMeetingForm: 동아리 모임 내용 수정 폼(GET) */
	@RequestMapping(value="/{clubName}/modifyMeeting/{meetingId}", method=RequestMethod.GET)
	public String modifyMeetingForm(@PathVariable String clubName, 
			@PathVariable int meetingId, Model model, HttpSession session) {
		Club club = clubService.getClubInformation(clubName);	// 동아리 정보
		Meeting meeting = meetingService.getMeeting(meetingId);	// 모임 정보
		
		// 업데이트 모델 바인딩
		bindUpdateObjectToModel(club, meeting, session, model, modifyMeetingCheckAuthCallback);
		
		return "forms/modify_meeting_form";
	}
	
	/* modifyMeetingForm: 동아리 모임 내용 수정 처리(POST) */
	@RequestMapping(value="/{clubName}/modifyMeeting/{meetingId}", method=RequestMethod.POST)
	public String modifyMeeting(@PathVariable String clubName,
			@PathVariable int meetingId, @RequestParam("title") String title,
			@RequestParam("location") String location, 
			@RequestParam("introduction") String introduction,
			SessionStatus sessionStatus, Model model, HttpSession session) {
		Meeting meeting = meetingService.getMeeting(meetingId);
		
		if(meeting.getMeetingId() != -1) {
			// 파라미터 필터링 후 meeting 모델 객체 생성(administratorName은 체크할 필요 없음)
			meeting = filteringMeetingModel(meeting, title, "", location, 
					introduction, clubName);
		} else {
			// 잘못된 meetingId(실제로는 실행되지 않을 구문 --> view에서 걸러준다)
			meeting = null;
		}
		
		if(meeting == null || !meetingService.setMeeting(meeting)) {
			// 오류 발생시 모델 정보를 새로 바인딩해서 기존 폼 내용 출력
			Club club = clubService.getClubInformation(clubName);	// 동아리 정보
			
			if(meeting == null)
				meeting = new Meeting();
			
			bindUpdateObjectToModel(club, meeting, session, model, modifyMeetingCheckAuthCallback);
			return "forms/modify_meeting_form";
		}
		
		/* 성공! */
		sessionStatus.setComplete();	// 세션 정리
		// 성공 시 모임 상세 정보로
		return "redirect:/club/meeting/" + clubName + "/meetingDetail?id=" 
				+ meeting.getMeetingId();
	}
	
	// 모임 상세 정보 수정 접권 권한 체크 콜백
	private CheckAuthorityCallback modifyMeetingCheckAuthCallback = new CheckAuthorityCallback() {
		
		@Override
		public boolean checkAuth(String clubName, Meeting meeting, HttpSession session) {
			return meeting.getAdministratorName().equals(SessionUtil.getLoginUserName(session)) || 
					userService.isThisUserClubMaster(SessionUtil.getLoginUserName(session), clubName);
		}
	};
	
	////////////////////////////////////////////////////////////////////////////////
	// 동아리 모임 삭제
	/* deleteMeetingPopupForm: 동아리 모임 삭제 폼(팝업) */
	@RequestMapping(value="/{clubName}/deleteMeeting/{meetingId}", method=RequestMethod.GET)
	public String deleteMeetingPopupForm(@PathVariable String clubName, 
			@PathVariable int meetingId, Model model, HttpSession session) {
		Meeting meeting = meetingService.getMeeting(meetingId);
		
		/////////// 팝업 정보
		String title = "동아리 모임 삭제: #" + meeting.getMeetingId();	// 타이틀
		
		// 코멘트
		String comment = "이 동아리 모임을 삭제하겠습니까?: " + meeting.getTitle();
		
		String url = "club/meeting/" + clubName + "/deleteMeeting/" + meetingId;	// post action url
		
		// 접근 권한 체크: 로그인 멤버 & 모임 개설자 or 동아리 마스터
		String loginUserName = (String) session.getAttribute(SessionUtil.loginUserNameAttr);
		boolean access = (SessionUtil.isLoginStatus(session) 
				&& (meeting.getAdministratorName().equals(loginUserName) || 
						userService.isThisUserClubMaster(SessionUtil.getLoginUserName(session), clubName)));
		
		PopupInfo pInfo = new PopupInfo(title, comment, url, access);
		
		// hidden fields 
		Map<String, Object> hiddens = new HashMap<String, Object>();
		hiddens.put("meetingId", meetingId);
		pInfo.setHiddens(hiddens);
		
		// 모델 바인딩(팝업 객체)
		model.addAttribute("popupInfo", pInfo);
		
		return "forms/popup/popup_basic";
	}
	
	/* deleteMeeting: 동아리 모임 삭제 처리 */
	@RequestMapping(value="/{clubName}/deleteMeeting/{meetingId}", method=RequestMethod.POST)
	public String deleteMeeting(@PathVariable String clubName, 
			@PathVariable int meetingId, Model model, HttpSession session) {
		// 처리 결과 정보 객체
		ProcessingResult result = new ProcessingResult(
				"동아리 모임 삭제", "동아리 모임 삭제", 
				"모임이 삭제 되었습니다", "처리 중 오류 발생");
		String successUrl = "club/" + clubName + "/clubMain";
		result.setSuccessUrl(successUrl);
		
		if(meetingService.deleteMeeting(meetingId)) {
			result.setResult(true);
		} else {
			result.setErrorCause("내부 오류: 알 수 없는 오류");
		}
		
		model.addAttribute("result", result);
		
		// 성공하면 해당 모임 정보가 삭제되므로, 기존 모임 상세 정보 화면을 리프레시하지 않고
		// 동아리 메인으로 리다이렉트시킨다. processing_result_location_assign.jsp가
		// 이런 역할을 하는 뷰 템플릿. 확인을 누르면 successUrl로 리다이렉팅함.
		return "results/processing_result_location_assign";
	}
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// 내부 메소드, 인터페이스(콜백)들
	
	/* bindUpdateObjectToModel: 모임 생성 및 수정 시 사용할 모델 객체 설정 및 바인딩 
	 * 모임 생성 폼이나 수정 폼에서 사용할 모델 객체들을 바인딩
	 * 접근 권한을 체크한 후 바인딩해주는 작업도 포함되어 있음
	 * 권한 마다 조건이 다르므로, 조건 체크는 템플릿/콜백 패턴으로 구현함
	 * CheckAuthorityCallback 인터페이스 참조
	 * */
	private void bindUpdateObjectToModel(Club club, Meeting meeting, 
			HttpSession session, Model model, CheckAuthorityCallback checkAuthCallback) {
		// 접근 권한 체크
		boolean isLoginUser = SessionUtil.isLoginStatus(session);	// 로그인한 사용자인지
		
		// 사용자가 수정 권한이 있는지: CheckAuthorityCallback 인터페이스 사용			
		boolean checkAuth = checkAuthCallback.checkAuth(club.getClubName(), meeting, session);
		
		// 모델 바인딩
		model.addAttribute("club", club);
		model.addAttribute("meeting", meeting);
		model.addAttribute("isLoginUser", isLoginUser);
		model.addAttribute("checkAuth", checkAuth);
	}
	
	/* filteringMeetingModel: Meeting 객체 생성을 위한 파라미터 필터링 및 객체 생성 
	 * 파라미터 중 meeting이 null로 넘어오는 경우는 기존 meeting 정보가 없을 때,
	 * 즉 모임을 새로 생성하는 경우 ******************************************/
	private Meeting filteringMeetingModel(Meeting meeting, String title, String administratorName,
			String location, String introduction, String clubName) {
		boolean checkParams = false;
		
		// 파라미터 필터링(xss 필터링)
		title = SecurityUtil.replaceScriptTag(title, false, null);
		administratorName = SecurityUtil.replaceScriptTag(administratorName, false, null);
		location = SecurityUtil.replaceScriptTag(location, false, null);
		introduction = SecurityUtil.replaceJavaScriptTag(introduction);
		clubName = SecurityUtil.replaceScriptTag(clubName, false, null);
		
		// 빈 문자열 체크: MySQL의 특성상 DAO에서도 중복 체크함(여기서 통과 못하면 DAO는 호출 자체가 안 됨)
		if(Validation.notEmptyString(title) && 
				Validation.notEmptyString(location) && Validation.notEmptyString(introduction)) {
			if(administratorName.equals("")) {
				// administratorName이 빈 문자열이면 여기서 체크 종료(호출자에서 책임)
				checkParams = true;
			} else {
				// 빈 문자열이 아니라면 체크해서 결정
				if(Validation.notEmptyString(administratorName)) {
					checkParams = true;
				} // 필터링 오류 시 아무것도 하지 않음(meeting 객체는 null)
			}
		}
			
		// 필터링이 끝난 모임 정보 객체 설정
		if(checkParams == true) {
			// meeting 객체가 null이라면 새로 생성
			// null이 아니라면 호출자 측에서 기존의 meeting을 넘겨준 경우
			if(meeting == null)
				meeting = new Meeting();
			
			meeting.setTitle(title);
			
			if(!administratorName.equals(""))
				meeting.setAdministratorName(administratorName);
			
			meeting.setLocation(location);
			meeting.setIntroduction(introduction);
			
			if(meeting.getClubName() == null || meeting.getClubName().equals(""))
				meeting.setClubName(clubName);
		} else {
			// 필터링 오류 시 null 객체 리턴
			meeting = null;
		}
		
		return meeting;
	}
	
	/* 인터페이스 CheckAuthorityCallback: 접근 권한 체크 
	 * 뷰마다 혹은 작업마다 접근 권한 조건이 다르므로 접근 권한에 대한 모델을 바인딩할 메소드에서는
	 * 이 인터페이스를 구현한다. checkAuth 메소드를 각 로직에 맞게 구현할 것. */
	private interface CheckAuthorityCallback {
		boolean checkAuth(String clubName, Meeting meeting, HttpSession session);
	}
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// 댓글 추가, 조회, 수정, 삭제
	
	/* writeReply: 댓글 쓰기(Ajax) */
	@RequestMapping(value="/{clubName}/writeReply/{meetingId}", method=RequestMethod.POST)
	@ResponseBody
	public boolean writeReply(@PathVariable String clubName, @PathVariable int meetingId,
			@RequestParam("writerName") String writerName, @RequestParam("content") String content,
			HttpSession session) throws UnsupportedEncodingException {	
		// 댓글 내용 디코딩(utf-8): 자동으로는 되지 않는다
		content = URLDecoder.decode(content, "utf-8");
		
		// 파라미터 검증 및 필터링(작성자 이름과 내용) - 이름: 필수입력, 내용: 필수입력 & 500자 이내
		if(writerName == null || content == null || writerName.length() == 0 || 
				content.length() == 0 || content.length() > 500) {
			return false;
		}
		
		// 모든 태그 제거
		writerName = SecurityUtil.replaceScriptTag(writerName, false, null);
		content = SecurityUtil.replaceScriptTag(content, false, null);
		
		Reply clubReply = new Reply();
		clubReply.setTargetId(meetingId);
		clubReply.setWriterName(writerName);
		clubReply.setContent(content);
		
		boolean result = false;
		
		if(!SessionUtil.isLoginStatus(session))
			return result;
		
		result = meetingService.writeNewMeetingReply(clubReply);
		Meeting meeting = meetingService.getMeeting(meetingId); // 모임 정보
		
		if(result && !writerName.equals(meeting.getAdministratorName())) {
			////////// 알림 전송
			// 알림 내용
			String noticeMsg = new StringBuilder().append("모임 공지글에 댓글이 달렸습니다").toString();
			// 알림 url(모임 공지글 보기)
			String noticeUrl = new StringBuilder().append("club/meeting/")
					.append(clubName).append("/meetingDetail?id=").append(meetingId).toString();
			
			// 알림 객체
			Notice notice = new Notice(meeting.getAdministratorName(), noticeMsg, noticeUrl);
			
			// 알림 삽입
			noticeService.writeNotice(notice);
		}
		
		return result;
	}
	
	/* readReplies: 댓글 가져오기(Ajax) */
	@RequestMapping(value="/{clubName}/readReplies/{meetingId}", method=RequestMethod.GET)
	@ResponseBody
	public List<Reply> readReplies(@PathVariable String clubName, @PathVariable int meetingId) {
		List<Reply> repliesList = meetingService.readMeetingReplies(meetingId);
		
		if(repliesList == null) {
			repliesList = new ArrayList<Reply>();
		}
		
		return repliesList;
	}
	
	/* deleteReply: 댓글 삭제(Ajax) */
	@RequestMapping(value="/{clubName}/deleteReply/{replyId}", method=RequestMethod.POST)
	@ResponseBody
	public boolean deleteReply(@PathVariable String clubName, @PathVariable int replyId,
			HttpSession session, Model model) {
		Reply meetingReply = meetingService.readMeetingReply(replyId);
		
		if(meetingReply.getWriterName() != null && 
				(userService.isThisUserClubMaster(SessionUtil.getLoginUserName(session), clubName) || 
						meetingReply.getWriterName().equals(SessionUtil.getLoginUserName(session))))
			return meetingService.deleteMeetingReply(replyId);
		
		return false;			
	}
}
