package com.mamascode.controller;

/****************************************************
 * ClubBasciController
 *
 * annotation 기반 핸들러 
 * 관리 url: /club
 * 
 * 동아리 기본 정보 관련 입력/화면 처리
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

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.mamascode.model.ClubArticle;
import com.mamascode.model.ClubCategory;
import com.mamascode.model.ClubJoinInfo;
import com.mamascode.model.Meeting;
import com.mamascode.model.Notice;
import com.mamascode.model.User;
import com.mamascode.model.utils.PopupInfo;
import com.mamascode.model.utils.ProcessingResult;
import com.mamascode.service.ClubService;
import com.mamascode.service.MeetingService;
import com.mamascode.service.NoticeService;
import com.mamascode.service.UserService;
import com.mamascode.utils.ListHelper;
import com.mamascode.utils.SecurityUtil;
import com.mamascode.utils.SessionUtil;
import com.mamascode.model.Club;
import com.mamascode.service.ClubArticleService;

@Controller
@SessionAttributes({"club", "meeting", "grandCategories"})
@RequestMapping("/club")
public class ClubBasciController {
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// 서비스 인터페이스
	@Autowired private ClubService clubService;
	@Autowired private UserService userService;
	@Autowired private MeetingService meetingService;
	@Autowired private ClubArticleService articleService;
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
	
	public void setArticleService(ClubArticleService articleService) {
		this.articleService = articleService;
	}
	
	public void setNoticeService(NoticeService noticeService) {
		this.noticeService = noticeService;
	}
	
	// controller methods //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// 동아리 생성
	
	/* makeClubForm: 동아리 생성 폼 */
	@RequestMapping(value="/makeClub", method=RequestMethod.GET)
	public String makeClubForm(Model model, HttpSession session) {
		// 동아리 master를 현재 로그인한 사용자로 설정
		boolean checkLogin = SessionUtil.isLoginStatus(session);
		Club club = new Club();
		club.setMasterName(SessionUtil.getLoginUserName(session));
		
		// 모델 바인딩
		model.addAttribute("checkLogin", checkLogin);	// 로그인 체크
		model.addAttribute("club", club);	// 동아리 정보
		model.addAttribute("grandCategories", clubService.getClubGrandCategories());	// 대분류 카테고리 정보
		
		return "forms/make_club_form";
	}
	
	/* makeClub: 동아리 생성(POST)
	 * 파라미터에 대해서 JSR-303 검증 기능 사용 */
	@RequestMapping(value="/makeClub", method=RequestMethod.POST)
	public String makeClub(@ModelAttribute @Valid Club club,
			BindingResult bindingResult, SessionStatus sessionStatus, 
			Model model, HttpSession session) {
		
		// 파라미터 필터링
		String clubName = SecurityUtil.replaceScriptTag(club.getClubName(), false, null);
		if(!clubName.equals("")) club.setClubName(clubName);
		
		// 에러 체크 후 신규 동아리 생성
		if(bindingResult.hasErrors() || 
				!SessionUtil.isLoginStatus(session)
				|| !clubService.createNewClub(club)) {
			// 로그인 체크
			boolean checkLogin = SessionUtil.isLoginStatus(session);
			model.addAttribute("checkLogin", checkLogin);	// 로그인 체크
			
			return "forms/make_club_form";	// 오류가 있다면 다시 폼을 보여준다
		}
		
		sessionStatus.setComplete();	// 세션 정리
		return "redirect:/";	// 메인으로 리다이렉트
	}
	
	/* getCategories: 카테고리 정보(Ajax + JSON) 
	 * 동아리 생성 시에 사용 - 대분류가 선택되면 그에 따라 하위 카테고리를 보여준다 */
	@RequestMapping(value="/getCategories/{parentCatId}", method=RequestMethod.GET)
	@ResponseBody
	public List<ClubCategory> getCategories(@PathVariable int parentCatId) {
		return clubService.getClubChildCategories(parentCatId);
	}
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// 동아리 정보
	
	/* getClubMain: 동아리 메인 화면(GET) */
	@RequestMapping(value="/{clubName}/clubMain", method=RequestMethod.GET)
	public String getClubMain(@PathVariable String clubName, HttpSession session, Model model) {
		// 동아리 정보
		Club club = clubService.getClubInformation(clubName);
		
		// 동아리 모임 목록(최근 5개)
		ListHelper<Meeting> meetingListHelper = meetingService.getMyClubMeetingList(
				clubName, MeetingService.MEETING_STATUS_IGNORE, 1, 5);
		
		// 로그인 사용자에 대한 정보 조회
		// 사용자가 동아리 회원인지, 가입 신청을 했는지에 따라 화면 처리를 다르게 해야 하기 때문에
		boolean checkClubMember = false;
		boolean checkMaster = false;
		boolean checkCrew = false;
		
		if(session != null && session.getAttribute(SessionUtil.loginUserNameAttr) != null) {
			// 사용자가 동아리 회원인지
			checkClubMember = clubService.isThisUserInThisClub(clubName, SessionUtil.getLoginUserName(session));
			checkMaster = userService.isThisUserClubMaster(SessionUtil.getLoginUserName(session), clubName);
			checkCrew = userService.isThisUserClubCrew(SessionUtil.getLoginUserName(session), clubName);
		}
		
		// 동아리 최근 게시물 목록(최근 게시물 5개만)
		ListHelper<ClubArticle> listHelper = 
				articleService.getArticlesForClub(clubName, 1, 5);
		
		// model binding
		model.addAttribute("club", club);
		model.addAttribute("checkClubMember", checkClubMember);
		model.addAttribute("checkMaster", checkMaster);
		model.addAttribute("checkCrew", checkCrew);
		model.addAttribute("meetingListHelper", meetingListHelper);
		model.addAttribute("articleListHelper", listHelper);
		
		return "club_main";
	}	
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// 동아리 회원 가입
	
	/* joinClubForm: 가입 신청 폼(GET) */
	@RequestMapping(value="/joinClub", method=RequestMethod.GET)
	public String joinClubForm(@RequestParam("clubName") String clubName, 
			Model model, HttpSession session) {
		String loginUserName  = 
					(String) session.getAttribute(SessionUtil.loginUserNameAttr);
		boolean checkLogin = SessionUtil.isLoginStatus(session);
		
		// 가입 정보
		ClubJoinInfo joinInfo = new ClubJoinInfo();
		joinInfo.setClubName(clubName);
		joinInfo.setUserName(loginUserName);
		model.addAttribute("joinInfo", joinInfo);
		
		// 동아리 정보
		Club club = clubService.getClubInformation(clubName);
		model.addAttribute("checkLogin", checkLogin);
		model.addAttribute("club", club);
		
		return "forms/join_club_form";
	}
	
	/* applyClub: 동아리 가입 신청 처리(POST) */
	@RequestMapping(value="/joinClub", method=RequestMethod.POST)
	public String applyClub(@RequestParam("clubName") String clubName, 
			@RequestParam("userName") String userName, 
			@RequestParam("comment") String comment, 
			SessionStatus sessionStatus, Model model) {
		boolean checkParametes = false;
		
		// 처리 결과 정보 객체
		ProcessingResult result = new ProcessingResult(
				"동아리 가입 신청 결과", "", 
				"동아리 가입 신청이 완료되었습니다.", "동아리 가입 신청이 실패하였습니다");
		result.setSuccessUrl("user/mypage/" + userName);		// 성공시 리다이렉트 url
		result.setFailUrl("club/" + clubName + "/clubMain");	// 실패시 리다이렉트 url
		
		// 파라미터 체크 및 필터링: 필수항목 체크, 스크립트 필터링
		if(!clubName.equals("") && !userName.equals("") && 
				!comment.equals("") && comment.length() <= 200) {
			checkParametes = true;
			
			// 필터링(xss)
			clubName = SecurityUtil.replaceScriptTag(clubName, false, null);
			userName = SecurityUtil.replaceScriptTag(userName, false, null);
			comment = SecurityUtil.replaceScriptTag(comment, false, null);
		}
		
		// 실행
		if(checkParametes && clubService.checkClubMemberCount(clubName)) {
			if(clubService.applyToClub(clubName, userName, comment)) {
				result.setResult(true);	// 정상적으로 신청이 등록됨
				
				// 동아리 정보 조회
				Club club = clubService.getClubInformation(clubName);
				
				// 동아리 마스터에게 알림 전송
				// 알림 내용
				String noticeMsg = new StringBuilder().append("[").append(clubName)
						.append(" 동아리 가입 신청 ] - 신청자: ").append(userName).toString();
				// 알림 url(동아리 메인)
				String noticeUrl = new StringBuilder().append("club/admin/").append(clubName)
						.append("/main").toString();
				
				// 알림 객체
				Notice notice = new Notice(club.getMasterName(), noticeMsg, noticeUrl);
				
				// 알림 삽입
				noticeService.writeNotice(notice);
			} else {
				// 실패: 오류 원인을 모델에 바인딩
				result.setErrorCause("알 수 없는 오류");
			}
		} else if(!checkParametes) {
			// 실패: 파라미터 입력 오류
			result.setErrorCause("필수항목이 입력되지 않음");
		} else if(!clubService.checkClubMemberCount(clubName)) {
			// 실패: 동아리 정원 초과
			result.setErrorCause("동아리 가입 정원 초과");
		}
		
		// 모델 바인딩
		model.addAttribute("result", result);	// 신청 처리 결과
		
		sessionStatus.setComplete();	// 세션 정리
		return "/results/processing_result_redirect";	// 결과 화면으로
	}
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// 동아리 회원 탈퇴
	
	/* leaveClubForm: 동아리 회원 탈퇴 폼(팝업 용) */
	@RequestMapping(value="/{clubName}/leaveClub", method=RequestMethod.GET)
	public String leaveClubForm(@PathVariable String clubName,
			HttpSession session, Model model) {
		/////////// 팝업 정보
		String title = "동아리 회원 탈퇴" + clubName;	// 타이틀
		String comment = clubName + "에서 탈퇴 하겠습니까??<br />동아리에서 작성한 게시물은 삭제되지 않습니다!";	// 코멘트
		String url = "club/" + clubName + "/leaveClub";	// post action url
		
		// 접근 권한: 로그인 상태
		boolean access = SessionUtil.isLoginStatus(session);
		
		PopupInfo pInfo = new PopupInfo(title, comment, url, access);
		
		// hidden fields 
		pInfo.setHiddens(new HashMap<String, Object>());
		
		// 모델 바인딩(팝업 객체)
		model.addAttribute("popupInfo", pInfo);
		
		return "forms/popup/popup_basic";
	}
	
	/* leaveClub: 동아리 회원 탈퇴 처리 */	
	@RequestMapping(value="/{clubName}/leaveClub", method=RequestMethod.POST)
	public String leaveClub(@PathVariable String clubName,
			HttpSession session, Model model) {
		// 처리 결과 정보 객체
		ProcessingResult result = new ProcessingResult(
				"동아리 회원 탈퇴", "동아리 강제 탈퇴", 
				"탈퇴 처리가 완료되었습니다", "탈퇴 처리를 완료하지 못했습니다");
		
		if(!userService.isThisUserClubMaster(SessionUtil.getLoginUserName(session), clubName)) {
			if(userService.isThisUserClubCrew(SessionUtil.getLoginUserName(session), clubName)) {
				// 회원이 동아리 운영진이면
				if(!clubService.dismissClubCrew(clubName, SessionUtil.getLoginUserName(session))) {
					// 실패: 운영진 해임 중 알 수 없는 오류 발생
					result.setErrorCause("알 수 없는 오류: 운영진 해임 처리 중");
				} else {
					// 동아리 운영진 해임 처리가 끝나면 회원 탈퇴 처리
					if(clubService.leaveClub(clubName, SessionUtil.getLoginUserName(session))) {
						result.setResult(true);	// 가입 처리 성공
					} else {
						// 실패
						result.setErrorCause("알 수 없는 오류: 회원 탈퇴 처리 중");
					}
				}
			} else {
				// 동아리 운영진이 아니면 그대로 탈퇴 처리
				if(clubService.leaveClub(clubName, SessionUtil.getLoginUserName(session))) {
					result.setResult(true);	// 가입 처리 성공
				} else {
					// 실패
					result.setErrorCause("알 수 없는 오류: 회원 탈퇴 처리 중");
				}
			}
		} else {
			// 실패: 회원이 동아리 마스터인 경우
			result.setErrorCause("<br />동아리 마스터는 동아리에서 탈퇴할 수 없습니다.<br />권한을 양도한 후 탈퇴하세요.");
		}
		
		model.addAttribute("result", result);	// 처리 결과
		return "results/processing_result";
	}
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// 동아리 목록
	
	/* clubListAll: 동아리 목록 전체 */
	@RequestMapping(value="/clubList", method=RequestMethod.GET)
	public String clubListAll(
			@RequestParam(value="page", required=false, defaultValue="1") int page,
			Model model) {
		ListHelper<Club> clubListHelper = clubService.getClubList(
				ClubService.LIST_ALL, page, "", 15, ClubService.ORDER_BY_DATE_DESC);
		boolean category = false;
		
		List<ClubCategory> childCategories = clubService.getClubGrandCategories();
		
		model.addAttribute("clubListHelper", clubListHelper);
		model.addAttribute("category", category);
		model.addAttribute("childCategories", childCategories);
		
		return "club_list";
	}
	
	/* clubListByCategory: 동아리 목록(카테고리 별) */
	@RequestMapping(value="/clubListByCat/{categoryId}", method=RequestMethod.GET)
	public String clubListByCategory(
			@RequestParam(value="page", required=false, defaultValue="1") int page,
			@PathVariable short categoryId, Model model) {
		ListHelper<Club> clubListHelper = clubService.getClubList(
				ClubService.LIST_CATEGORY_FILTERING, page, categoryId, 15, ClubService.ORDER_BY_DATE_DESC);
		boolean category = true;
		
		ClubCategory clubCategory = null;		
		ClubCategory parentCategory = null;
		
		clubCategory = clubService.getClubCategory(categoryId);
		if(clubCategory != null)
			parentCategory = clubService.getClubCategory(clubCategory.getParentCategoryId());
		
		List<ClubCategory> childCategories = clubService.getClubChildCategories(categoryId);
		
		model.addAttribute("clubListHelper", clubListHelper);
		model.addAttribute("category", category);
		model.addAttribute("clubCategory", clubCategory);
		model.addAttribute("parentCategory", parentCategory);
		model.addAttribute("childCategories", childCategories);
		
		return "club_list";	
	}
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// 동아리 회원 목록
	@RequestMapping(value="/{clubName}/clubMemberList", method=RequestMethod.GET)
	public String clubMemberList(@PathVariable String clubName,
			@RequestParam(value="page", required=false, defaultValue="1") int page,
			Model model, HttpSession session) {
		// 동아리 회원 목록
		ListHelper<User> clubMembers = clubService.getClubMembers(page, clubName, 15);
		
		// 접근 권한 체크
		boolean checkMember = clubService.isThisUserInThisClub(
				clubName, SessionUtil.getLoginUserName(session));
				
		// 동아리 기본 정보
		Club club = clubService.getClubInformation(clubName);
		
		// 모델 바인딩
		model.addAttribute("clubMembers", clubMembers);
		model.addAttribute("checkMember", checkMember);
		model.addAttribute("club", club);
		
		return "club_member_list";
	}
}
