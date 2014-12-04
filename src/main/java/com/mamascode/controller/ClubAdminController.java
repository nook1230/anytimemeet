package com.mamascode.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.mamascode.model.ClubJoinInfo;
import com.mamascode.model.Notice;
import com.mamascode.model.User;
import com.mamascode.model.utils.ProcessingResult;
import com.mamascode.service.ClubService;
import com.mamascode.service.NoticeService;
import com.mamascode.service.UserService;
import com.mamascode.utils.ListHelper;
import com.mamascode.utils.SessionUtil;
import com.mamascode.model.Club;
import com.mamascode.model.utils.PopupInfo;

/****************************************************
 * ClubAdminController
 *
 * annotation 기반 핸들러 
 * 관리 url: /club/admin
 * 동아리 관리 화면 담당
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 * 
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

@Controller
@SessionAttributes({"club"})
@RequestMapping("/club/admin")
public class ClubAdminController {
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// 서비스 인터페이스
	@Autowired private ClubService clubService;
	@Autowired private UserService userService;
	@Autowired private NoticeService noticeService;
	
	public void setClubService(ClubService clubService) {
		this.clubService = clubService;
	}
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	public void setNoticeService(NoticeService noticeService) {
		this.noticeService = noticeService;
	}

	// controller methods //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// 동아리 운영자&운영진 관리 화면
	
	/* adminMain: 운영자 메인 화면 */
	@RequestMapping(value="/{clubName}/main", method=RequestMethod.GET)
	public String adminMain(@PathVariable String clubName,
			@RequestParam(value="page", required=false, defaultValue="1") int page,
			Model model, HttpSession session) {
		// 동아리 정보
		Club club = clubService.getClubInformation(clubName);
		
		// 운영권한 체크: master & crew
		boolean checkMaster = false;
		boolean checkCrew = false;
		if(SessionUtil.isLoginStatus(session)) {
			// 로그인 상태라면, 적절한 운영 권한을 가지고 있는지 체크
			checkMaster = userService.isThisUserClubMaster(SessionUtil.getLoginUserName(session), clubName);
			checkCrew = userService.isThisUserClubCrew(SessionUtil.getLoginUserName(session), clubName);
		}
		
		// 동아리 회원 정보(최근 10명)
		ListHelper<User> clubMembers = clubService.getClubMembers(page, clubName, 10);
		
		// 동아리 가입 신청 회원 + 초대 회원 목록(최근 10개)
		ListHelper<ClubJoinInfo> clubJoinListHelper = 
				clubService.getClubJoinApplications(1, clubName, 10);
		ListHelper<ClubJoinInfo> clubInvListHelper = 
				clubService.getClubJoinInvitations(1, clubName, 10);
		
		// 모델 바인딩
		model.addAttribute("club", club);
		model.addAttribute("checkMaster", checkMaster);
		model.addAttribute("checkCrew", checkCrew);
		model.addAttribute("clubMembers", clubMembers);
		model.addAttribute("clubJoinListHelper", clubJoinListHelper);
		model.addAttribute("clubInvListHelper", clubInvListHelper);
		
		return "club_admin_main";
	}
	
	/* clubSetting: 동아리 설정 페이지 */
	@RequestMapping(value="/{clubName}/setting", method=RequestMethod.GET)
	public String clubSetting(@PathVariable String clubName, 
			Model model, HttpSession session) {
		
		Club club = clubService.getClubInformation(clubName);
		boolean checkMaster = userService.isThisUserClubMaster(
				SessionUtil.getLoginUserName(session), clubName);
		
		model.addAttribute("club", club);
		model.addAttribute("checkMaster", checkMaster);
		model.addAttribute("grandCategories", 
				clubService.getClubGrandCategories()); // 대분류 카테고리 정보
		
		return "club_setting";
	}
	
	/* modifyClubInfo: 동아리 정보 변경 */
	@RequestMapping(value="/{clubName}/setting/modifyClubInfo", method=RequestMethod.POST)
	public String modifyClubInfo(@PathVariable String clubName, 
			@ModelAttribute @Valid Club club,
			BindingResult bindingResult, SessionStatus sessionStatus, 
			Model model, HttpSession session) {
		int currentMemberCount = club.getNumberOfClubMember();
		boolean maxNumCheck = (club.getMaxMemberNum() >= currentMemberCount);
		
		// 에러 체크 후 동아리 정보 수정
		if(!maxNumCheck || bindingResult.hasErrors() || !clubService.setClub(club)) {
			boolean checkMaster = userService.isThisUserClubMaster(
					SessionUtil.getLoginUserName(session), clubName); 
			model.addAttribute("bindingResultError", true);
			model.addAttribute("checkMaster", checkMaster);
			model.addAttribute("maxNumCheck", maxNumCheck);
			
			return "club_setting";
		}
		
		return "redirect:/club/admin/" + clubName + "/setting";
	}
	
	/* inactivateClubForm: 동아리 비활성화 폼(팝업) */
	@RequestMapping(value="/{clubName}/setting/inactivateClub", method=RequestMethod.GET)
	public String inactivateClubForm(@PathVariable String clubName, 
			HttpSession session, Model model) {
		/////////// 팝업 정보
		String title = "동아리 비활성화: " + clubName;	// 타이틀
		String comment = clubName + ": 동아리를 비활성화하겠습니까?";	// 코멘트
		String url = "club/admin/" + clubName + "/setting/inactivateClub";	// post action url
		
		// 접근 권한: master만
		boolean access = userService.isThisUserClubMaster(SessionUtil.getLoginUserName(session), clubName);
		
		PopupInfo pInfo = new PopupInfo(title, comment, url, access);
		
		// hidden fields 
		Map<String, Object> hiddens = new HashMap<String, Object>();
		hiddens.put("clubName", clubName);		
		pInfo.setHiddens(hiddens);
		
		// 모델 바인딩(팝업 객체)
		model.addAttribute("popupInfo", pInfo);
		
		return "forms/popup/popup_basic";
	}
	
	/* inactivateClub: 동아리 비활성화 처리 */
	@RequestMapping(value="/{clubName}/setting/inactivateClub", method=RequestMethod.POST)
	public String inactivateClub(@PathVariable String clubName, 
			HttpSession session, Model model) {
		// 처리 결과 정보 객체
		ProcessingResult result = new ProcessingResult(
				"동아리 비활성화", "동아리 비활성화", 
				"동아리가 비활성화 되었습니다", "처리 중 오류 발생");
		String successUrl = "";
		result.setSuccessUrl(successUrl);
		
		if(clubService.activateClub(clubName, ClubService.INACTIVATE) == ClubService.INACTIVATE) {
			result.setResult(true);
			
			//************ 동아리 회원들에게 알림 전송
			// 동아리 회원 이름 가져오기
			List<String> clubMemberNames = clubService.getClubMemberNames(clubName);
			// 알림 내용
			String noticeMsg = new StringBuilder().append("동아리 ").append(clubName)
					.append("(이)가 비활성화되었습니다.").toString();
			// 알림 url(동아리 메인)
			String noticeUrl = new StringBuilder().append("club/").append(clubName)
					.append("/clubMain").toString();
			
			for(String memberName : clubMemberNames) {
				// 알림 객체
				Notice notice = new Notice(memberName, noticeMsg, noticeUrl);
				
				// 알림 삽입
				noticeService.writeNotice(notice);
			}
		} else {
			result.setErrorCause("내부 오류: 알 수 없는 오류");
		}
				
		model.addAttribute("result", result);
		
		// 결과 통보 및 리다이렉트
		return "results/processing_result_location_assign";
	}
	
	/* activateClubForm: 동아리 활성화 폼 */
	@RequestMapping(value="/{clubName}/setting/activateClub", method=RequestMethod.GET)
	public String activateClubForm(@PathVariable String clubName, 
			HttpSession session, Model model) {
		// 접근 권한: master만
		boolean checkMaster = userService.isThisUserClubMaster(
				SessionUtil.getLoginUserName(session), clubName);
		
		model.addAttribute("checkMaster", checkMaster);
		
		return "forms/activate_club";
	}
	
	/* activateClub: 동아리 활성화 처리 */
	@RequestMapping(value="/{clubName}/setting/activateClub", method=RequestMethod.POST)
	public String activateClub(@PathVariable String clubName, Model model) {
		String errorCode = "";
		
		if(clubService.activateClub(clubName, ClubService.ACTIVATE) == ClubService.ACTIVATE) {
			//************ 동아리 회원들에게 알림 전송
			// 동아리 회원 이름 가져오기
			List<String> clubMemberNames = clubService.getClubMemberNames(clubName);
			
			// 알림 내용
			String noticeMsg = new StringBuilder().append("동아리 ").append(clubName)
					.append("(이)가 활성화되었습니다.").toString();
			// 알림 url(동아리 메인)
			String noticeUrl = new StringBuilder().append("club/").append(clubName)
					.append("/clubMain").toString();
			
			for(String memberName : clubMemberNames) {
				// 알림 객체
				Notice notice = new Notice(memberName, noticeMsg, noticeUrl);
				
				// 알림 삽입
				noticeService.writeNotice(notice);
			}
			
			errorCode = "activate success";
		} else {
			errorCode = "activate failure";
		}
		
		model.addAttribute("club", clubService.getClubInformation(clubName));
		model.addAttribute("errorCode", errorCode);
		
		// 결과 통보 및 리다이렉트
		return "results/activate_club_result";
	}
	
	////////////////////////////////////////////////////////////////////////////////
	
	/* transferMasterPre: 마스터 권한 양도 1단계 화면(검색 폼) */
	@RequestMapping(value="/{clubName}/setting/transferMasterPre", method=RequestMethod.GET)
	public String transferMasterPre(@PathVariable String clubName, HttpSession session, Model model) {
		// 접근 권한: master만
		boolean checkMaster = userService.isThisUserClubMaster(
				SessionUtil.getLoginUserName(session), clubName);
		
		// 모델 바인딩
		model.addAttribute("club", clubService.getClubInformation(clubName));
		model.addAttribute("checkMaster", checkMaster);
		
		return "forms/transfer_master";
	}
	
	/* transferMasterFrom: 마스터 권한 양도 폼(팝업) */
	@RequestMapping(value="/{clubName}/setting/transferMaster", method=RequestMethod.GET)
	public String transferMasterFrom(@PathVariable String clubName,
			@RequestParam("newMasterName") String newMasterName,
			HttpSession session, Model model) {
		/////////// 팝업 정보
		String title = "동아리 마스터 권한 양도: " + clubName;	// 타이틀
		String comment = newMasterName + "에게 동아리를 양도하겠습니까?";	// 코멘트
		String url = "club/admin/" + clubName + "/setting/transferMaster";	// post action url
		
		// 접근 권한: master만
		boolean access = userService.isThisUserClubMaster(SessionUtil.getLoginUserName(session), clubName);
		
		PopupInfo pInfo = new PopupInfo(title, comment, url, access);
		
		// hidden fields 
		Map<String, Object> hiddens = new HashMap<String, Object>();
		hiddens.put("newMasterName", newMasterName);		
		pInfo.setHiddens(hiddens);
		
		// 모델 바인딩(팝업 객체)
		model.addAttribute("popupInfo", pInfo);
		
		return "forms/popup/popup_basic";
	}
	
	/* transferMaster: 마스터 권한 양도 처리 */
	@RequestMapping(value="/{clubName}/setting/transferMaster", method=RequestMethod.POST)
	public String transferMaster(@PathVariable String clubName,
			@RequestParam("newMasterName") String newMasterName,
			HttpSession session, Model model) {
		// 처리 결과 정보 객체
		ProcessingResult result = new ProcessingResult(
				"동아리 마스터 권한 양도", "동아리 마스터 권한 양도", 
				"동아리 마스터 권한이 " + newMasterName + "에게 양도되었습니다", "처리 중 오류 발생");
		String successUrl = "club/" + clubName + "/clubMain";
		result.setSuccessUrl(successUrl);
		
		// 양수자가 동아리 회원인지 체크
		if(clubService.isThisUserInThisClub(clubName, newMasterName)) {
			// 양도자가 마스터인지 체크
			if(!userService.isThisUserClubMaster(SessionUtil.getLoginUserName(session), clubName)) {
				// 마스터가 아니라면 오류 메시지 설정 후 종료
				result.setErrorCause("접근 권한 오류");
			} else {
				if(userService.isThisUserClubCrew(newMasterName, clubName)) {
					// 양수자가 동아리 운영진이라면 운영진에서 해임한다
					if(clubService.dismissClubCrew(clubName, newMasterName)) {
						// 운영진 해임 성공: 마스터 권한 양도
						if(clubService.transferMaster(clubName, newMasterName)) {
							// 알림 전송
							addNoticeTransferMaster(SessionUtil.getLoginUserName(session), 
									newMasterName, clubName);
							result.setResult(true);
						} else {
							// 오류: 다시 운영진으로 임명 후 종료
							clubService.appointClubCrew(clubName, newMasterName);
							result.setErrorCause("내부 오류: 알 수 없는 오류");
						}
					}
				} else {
					// 아니라면 그대로 마스터 권한 양도
					if(clubService.transferMaster(clubName, newMasterName)) {
						// 알림 전송
						addNoticeTransferMaster(SessionUtil.getLoginUserName(session), 
								newMasterName, clubName);
						result.setResult(true);
					} else {
						result.setErrorCause("내부 오류: 알 수 없는 오류");
					}
				}
			}
		} else {
			// 동아리 회원이 아니라면 오류 메시지 설정 후 종료
			result.setErrorCause("권한 양수자가 동아리 회원이 아닙니다");
		}
						
		model.addAttribute("result", result);
		
		// 결과 통보 및 리다이렉트
		return "results/processing_result_location_assign";
	}
	
	/* addNoticeTransferMaster: 동아리 마스터 권한 양도 성공시 알림 전송 */
	private void addNoticeTransferMaster(String oldMasterName, String newMasterName, String clubName) {
		// 이전 마스터에서 알림 전송
		
		// 알림 내용
		String noticeMsg = new StringBuilder().append("동아리 ").append(clubName)
				.append("의 마스터 권한이 ").append(newMasterName).append("님에게 양도되었습니다.").toString();
		
		// 알림 객체
		Notice notice1 = new Notice(oldMasterName, noticeMsg, NoticeService.NOTICE_TYPE_MASTER);
		
		// 알림 삽입
		noticeService.writeNotice(notice1);
		
		// 새로운 마스터에게 알림 전송
		// 알림 내용
		noticeMsg = new StringBuilder().append(newMasterName).append("님에게 동아리 ").append(clubName)
				.append("의 마스터 권한이 양도되었습니다.").toString();
		// 알림 url(동아리 메인)
		String noticeUrl = new StringBuilder().append("club/").append(clubName)
				.append("/clubMain").toString();
		
		// 알림 객체
		Notice notice2 = new Notice(newMasterName, noticeMsg, noticeUrl, NoticeService.NOTICE_TYPE_MASTER);
		
		// 알림 삽입
		noticeService.writeNotice(notice2);
	}
	
	////////////////////////////////////////////////////////////////////////////////
	
	/* approveClubJoin: 가입 신청 승인 폼(팝업 용) */
	@RequestMapping(value="/{clubName}/clubJoin", method=RequestMethod.GET)
	public String approveClubJoin(@PathVariable String clubName, 
			@RequestParam("userName") String userName, HttpSession session, Model model) {
		
		/////////// 팝업 정보
		String title = "동아리 회원 가입 승인 처리: " + clubName;	// 타이틀
		String comment = userName + "의 가입 신청을 승인하시겠습니까?";	// 코멘트
		String url = "club/admin/" + clubName + "/clubJoin";	// post action url
		
		// 접근 권한: master만
		boolean access = userService.isThisUserClubMaster(SessionUtil.getLoginUserName(session), clubName);
		
		PopupInfo pInfo = new PopupInfo(title, comment, url, access);
		
		// hidden fields 
		Map<String, Object> hiddens = new HashMap<String, Object>();
		hiddens.put("userName", userName);
		hiddens.put("clubName", clubName);		
		pInfo.setHiddens(hiddens);
		
		// 모델 바인딩(팝업 객체)
		model.addAttribute("popupInfo", pInfo);
		
		return "forms/popup/popup_basic";
	}
	
	/* joinClub: 가입 신청 승인 & 초대 처리 */	
	@RequestMapping(value="/{clubName}/clubJoin", method=RequestMethod.POST)
	public String joinClub(@PathVariable String clubName, 
			@RequestParam("userName") String userName, Model model) {		
		// 처리 결과 정보 객체
		ProcessingResult result = new ProcessingResult(
				"동아리 회원 가입 승인", "동아리 회원 가입 승인", 
				"가입 처리가 완료되었습니다", "가입 처리를 완료하지 못했습니다");
		
		if(clubService.checkClubMemberCount(clubName)) {
			if(clubService.joinClub(clubName, userName)) {
				result.setResult(true);	// 가입 처리 성공
				
				// 신규 가입 회원에게 알림 전송
				// 알림 내용
				String noticeMsg = new StringBuilder().append(clubName).append(" 동아리에 가입 되었습니다").toString();
				// 알림 url(동아리 메인)
				String noticeUrl = new StringBuilder().append("club/").append(clubName)
						.append("/clubMain").toString();
				
				// 알림 객체
				Notice notice = new Notice(userName, noticeMsg, noticeUrl);
				
				// 알림 삽입
				noticeService.writeNotice(notice);
			} else {
				// 실패
				result.setErrorCause("알 수 없는 오류");
			}
		} else {
			// 실패
			result.setErrorCause("동아리 가입 정원 초과");
		}
		
		model.addAttribute("result", result);	// 처리 결과
		return "results/processing_result";
	}
	
	/* inviteMemberForm: 동아리 초대 폼 */
	@RequestMapping(value="/{clubName}/inviteMember", method=RequestMethod.GET)
	public String inviteMemberForm(@PathVariable String clubName,
			Model model, HttpSession session) {
		// 접근 권한: 동아리 마스터
		boolean checkMaster = userService.isThisUserClubMaster(
				SessionUtil.getLoginUserName(session), clubName);
		
		// 동아리 정보
		model.addAttribute("checkMaster", checkMaster);
		
		return "forms/invite_club_form";
	}
	
	/* inviteMember: 동아리 초대 */
	@RequestMapping(value="/{clubName}/inviteMember", method=RequestMethod.POST)
	public String inviteMember(@PathVariable String clubName, Model model, HttpSession session,
			@RequestParam("userName") String userName, @RequestParam("comment") String comment) {
		User user = userService.getUserAccountByUserName(userName);
		boolean result = false;
		
		if(user.getUserName() != null && !user.getUserName().equals(""))
			result = clubService.inviteUserToClub(clubName, userName, comment);
		
		if(result) {
			// 초대 회원에게 알림 전송
			// 알림 내용
			String noticeMsg = new StringBuilder().append(clubName).append(" 동아리에 초대 받으셨습니다").toString();
			// 알림 url(동아리 메인)
			String noticeUrl = new StringBuilder().append("user/mypage/").append(userName).toString();
			
			// 알림 객체
			Notice notice = new Notice(userName, noticeMsg, noticeUrl);
			
			// 알림 삽입
			noticeService.writeNotice(notice);
		}
		
		return "redirect:/club/admin/" + clubName + "/main";
	}
	
	/* 가입 신청, 초대 관리 화면 */
	@RequestMapping(value="/{clubName}/joinClubAdmin", method=RequestMethod.GET)
	public String joinClubAdmin(@PathVariable String clubName, Model model, HttpSession session,
			/* 동아리 가입 신청 & 초대 목록 페이지 번호 */
			@RequestParam(value="cj_page", required=false, defaultValue="1") int cj_page,
			@RequestParam(value="ci_page", required=false, defaultValue="1") int ci_page) {
		boolean checkMaster = userService.isThisUserClubMaster(SessionUtil.getLoginUserName(session), clubName);
		Club club = clubService.getClubInformation(clubName);
		ListHelper<ClubJoinInfo> clubJoinListHelper = clubService.getClubJoinApplications(cj_page, clubName, 20);
		ListHelper<ClubJoinInfo> clubInvListHelper = clubService.getClubJoinInvitations(ci_page, clubName, 20);
		
		model.addAttribute("checkMaster", checkMaster);
		model.addAttribute("club", club);
		model.addAttribute("clubJoinListHelper", clubJoinListHelper);
		model.addAttribute("clubInvListHelper", clubInvListHelper);
		
		return "club_join_list";
	}
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// 동아리 회원 관리
	
	/* releaseMemberForm: 회원 강제 탈퇴 폼(팝업 용) */
	@RequestMapping(value="/{clubName}/releaseMember", method=RequestMethod.GET)
	public String releaseMemberForm(@PathVariable String clubName, 
			@RequestParam("userName") String userName, HttpSession session, Model model) {
		
		/////////// 팝업 정보
		String title = "동아리 회원 강제 탈퇴 처리: " + clubName;	// 타이틀
		String comment = userName + "(을)를 강제 탈퇴 하겠습니까??";	// 코멘트
		String url = "club/admin/" + clubName + "/releaseMember?userName=" + userName;	// post action url
		
		// 접근 권한: master만
		boolean access = userService.isThisUserClubMaster(SessionUtil.getLoginUserName(session), clubName);
		
		PopupInfo pInfo = new PopupInfo(title, comment, url, access);
		
		// hidden fields 
		Map<String, Object> hiddens = new HashMap<String, Object>();
		pInfo.setHiddens(hiddens);
		
		// 모델 바인딩(팝업 객체)
		model.addAttribute("popupInfo", pInfo);
		
		return "forms/popup/popup_basic";
	}
	
	/* releaseMember: 회원 강제 탈퇴 처리 */	
	@RequestMapping(value="/{clubName}/releaseMember", method=RequestMethod.POST)
	public String releaseMember(@PathVariable String clubName, 
			@RequestParam("userName") String userName, HttpSession session, Model model) {
		// 처리 결과 정보 객체
		ProcessingResult result = new ProcessingResult(
				"동아리 회원 강제 탈퇴", "동아리 회원 강제 탈퇴", 
				"탈퇴 처리가 완료되었습니다", "탈퇴 처리를 완료하지 못했습니다");
		if(!userService.isThisUserClubMaster(userName, clubName)) {
			if(userService.isThisUserClubCrew(userName, clubName)) {
				// 회원이 동아리 운영진이면
				if(!clubService.dismissClubCrew(clubName, userName)) {
					// 실패: 운영진 해임 중 알 수 없는 오류 발생
					result.setErrorCause("알 수 없는 오류: 운영진 해임 처리 중");
				} else {
					// 동아리 운영진 해임 처리가 끝나면 회원 탈퇴 처리
					if(clubService.leaveClub(clubName, userName)) {
						result.setResult(true);	// 탈퇴 처리 성공
						
						// 탈퇴 회원에게 알림 전송
						// 알림 내용
						String noticeMsg = new StringBuilder().append(clubName).append(" 동아리에서 강제탈퇴 되었습니다").toString();
						
						// 알림 객체
						Notice notice = new Notice(userName, noticeMsg);
						
						// 알림 삽입
						noticeService.writeNotice(notice);
					} else {
						// 실패
						result.setErrorCause("알 수 없는 오류: 회원 탈퇴 처리 중");
					}
				}
			} else {
				// 동아리 운영진이 아니면 그대로 탈퇴 처리
				if(clubService.leaveClub(clubName, userName)) {
					result.setResult(true);	// 가입 처리 성공
					
					// 탈퇴 회원에게 알림 전송
					// 알림 내용
					String noticeMsg = new StringBuilder().append(clubName).append(" 동아리에서 강제탈퇴 되었습니다").toString();
					
					// 알림 객체
					Notice notice = new Notice(userName, noticeMsg);
					
					// 알림 삽입
					noticeService.writeNotice(notice);
				} else {
					// 실패
					result.setErrorCause("알 수 없는 오류: 회원 탈퇴 처리 중");
				}
			}
		} else {
			// 실패: 탈퇴시키려는 회원이 동아리 마스터인 경우
			result.setErrorCause("<br />동아리 마스터는 강제 탈퇴시킬 수 없습니다.");
		}
		
		model.addAttribute("result", result);	// 처리 결과
		return "results/processing_result";
	}
	
	////////////////////////////////////////////////////////////////////////////////
	// 동아리 운영진
	
	/* adminClubCrewMain: 운영진 관리 메인 화면 */
	@RequestMapping(value="/{clubName}/adminClubCrew", method=RequestMethod.GET)
	public String adminClubCrewMain(@PathVariable String clubName, 
			@RequestParam(value="page", required=false, defaultValue="1") int page,
			Model model, HttpSession session) {
		Club club = clubService.getClubInformation(clubName);
		ListHelper<User> clubCrewListHelper = clubService.getClubCrews(page, clubName, 20);
		boolean checkMaster = userService.isThisUserClubMaster(
				SessionUtil.getLoginUserName(session), clubName);
		
		model.addAttribute("club", club);
		model.addAttribute("clubCrewListHelper", clubCrewListHelper);
		model.addAttribute("checkMaster", checkMaster);
		return "club_crew_admin";
	}
	
	/* searchClubMember: 동아리 회원 검색 - 운영진 임명 작업을 위해(Ajax) */
	@RequestMapping(value="/{clubName}/searchClubMember", method=RequestMethod.GET)
	@ResponseBody
	public List<User> searchClubMember(@RequestParam("keyword") String keyword,
			@RequestParam("searchType") int searchType,
			@PathVariable String clubName, HttpSession session) throws UnsupportedEncodingException {
		// 검색 키워드 디코딩(utf-8): 자동으로는 되지 않는다
		keyword = URLDecoder.decode(keyword, "utf-8");
		
		// 사용자가 url을 통해 접근하려 할 때를 대비하여 사용자가 동아리 운영자인지 체크한다
		if(userService.isThisUserClubMaster(SessionUtil.getLoginUserName(session), clubName))
			return clubService.searchClubMember(keyword, clubName, searchType);
		else 
			return Collections.emptyList();	// 부당한 접근일 때는 빈 리스트를 리턴
	}
	
	/* appointClubCrewForm: 동아리 운영진 임명 폼(팝업) */
	@RequestMapping(value="/{clubName}/appointClubCrew/{userName}", method=RequestMethod.GET)
	public String appointClubCrewForm(@PathVariable String clubName, 
			@PathVariable String userName, Model model, HttpSession session) {
		/////////// 팝업 정보
		String title = "동아리 운영진 임명: " + clubName;	// 타이틀
		String comment = userName + "를 동아리 운영진으로 임명하시겠습니까?";	// 코멘트
		String url = "/club/admin/" + clubName + "/appointClubCrew/" + userName; // post action url
		
		// 접근 권한: master만
		boolean access = userService.isThisUserClubMaster(
				SessionUtil.getLoginUserName(session), clubName);
		
		PopupInfo pInfo = new PopupInfo(title, comment, url, access);
		
		// hidden fields 
		Map<String, Object> hiddens = new HashMap<String, Object>();
		hiddens.put("userName", userName);
		hiddens.put("clubName", clubName);		
		pInfo.setHiddens(hiddens);
		
		// 모델 바인딩(팝업 객체)
		model.addAttribute("popupInfo", pInfo);
		
		return "forms/popup/popup_basic";
	}
	
	/* appointClubCrew: 동아리 운영진 임명 처리 */
	@RequestMapping(value="/{clubName}/appointClubCrew/{userName}", method=RequestMethod.POST)
	public String appointClubCrew(@PathVariable String clubName, 
			@PathVariable String userName, Model model, HttpSession session) {
		// 처리 결과 정보 객체
		ProcessingResult result = new ProcessingResult(
				"동아리 운영진 임명", "동아리 운영진 임명", 
				"임명 처리가 완료되었습니다", "임명 처리를 완료하지 못했습니다");
				
		if(!userService.isThisUserClubMaster(userName, clubName) && 
				!userService.isThisUserClubCrew(userName, clubName)) {
			if(clubService.appointClubCrew(clubName, userName)) {
				result.setResult(true);	// 임명 처리 성공
				
				// 신규 운영진에게 알림 전송
				// 알림 내용
				String noticeMsg = new StringBuilder().append(clubName).append(" 동아리의 운영진에 임명 되셨습니다!").toString();
				// 알림 url(동아리 메인)
				String noticeUrl = new StringBuilder().append("club/").append(clubName)
						.append("/clubMain").toString();
				
				// 알림 객체
				Notice notice = new Notice(userName, noticeMsg, noticeUrl);
				
				// 알림 삽입
				noticeService.writeNotice(notice);
			} else {
				// 실패
				result.setErrorCause("알 수 없는 오류");
			}
		} else {
			// 실패
			result.setErrorCause("<br />동아리 마스터이거나 이미 동아리 운영진입니다");
		}
		
		model.addAttribute("result", result);	// 처리 결과
		return "results/processing_result";
	}
	
	/* dismissClubCrewForm: 동아리 운영진 해임(팝업) */
	@RequestMapping(value="/{clubName}/dismissClubCrew/{userName}", method=RequestMethod.GET)
	public String dismissClubCrewForm(@PathVariable String clubName, 
			@PathVariable String userName, HttpSession session, Model model) {
		/////////// 팝업 정보
		String title = "동아리 운영진 해임: " + clubName;	// 타이틀
		String comment = userName + "를 동아리 운영진에서 해임하시겠습니까?";	// 코멘트
		String url = "/club/admin/" + clubName + "/dismissClubCrew/" + userName; // post action url
		
		// 접근 권한: master만
		boolean access = userService.isThisUserClubMaster(
				SessionUtil.getLoginUserName(session), clubName);
		
		PopupInfo pInfo = new PopupInfo(title, comment, url, access);
		
		// hidden fields 
		Map<String, Object> hiddens = new HashMap<String, Object>();
		hiddens.put("userName", userName);
		hiddens.put("clubName", clubName);		
		pInfo.setHiddens(hiddens);
		
		// 모델 바인딩(팝업 객체)
		model.addAttribute("popupInfo", pInfo);
		
		return "forms/popup/popup_basic";
	}
	
	/* dismissClubCrew: 동아리 운영진 해임 처리 */
	@RequestMapping(value="/{clubName}/dismissClubCrew/{userName}", method=RequestMethod.POST)
	public String dismissClubCrew(@PathVariable String clubName, 
			@PathVariable String userName, HttpSession session, Model model) {
		// 처리 결과 정보 객체
		ProcessingResult result = new ProcessingResult(
				"동아리 운영진 해임", "동아리 운영진 해임", 
				"해임 처리가 완료되었습니다", "해임 처리를 완료하지 못했습니다");
				
		if(userService.isThisUserClubCrew(userName, clubName)) {
			if(clubService.dismissClubCrew(clubName, userName)) {
				result.setResult(true);	// 임명 처리 성공
				
				// 해임된 운영진에게 알림 전송
				// 알림 내용
				String noticeMsg = new StringBuilder().append(clubName).append(" 동아리의 운영진에사 해임 되셨습니다!").toString();
				// 알림 url(동아리 메인)
				String noticeUrl = new StringBuilder().append("club/").append(clubName)
						.append("/clubMain").toString();
				
				// 알림 객체
				Notice notice = new Notice(userName, noticeMsg, noticeUrl);
				
				// 알림 삽입
				noticeService.writeNotice(notice);
			} else {
				// 실패
				result.setErrorCause("알 수 없는 오류");
			}
		} else {
			// 실패
			result.setErrorCause("<br />운영진이 아닙니다");
		}
		
		model.addAttribute("result", result);	// 처리 결과
		return "results/processing_result";
	}
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// 동아리 완전 폐쇄(삭제)
	
	/* closeClubForm: 동아리 폐쇄 폼 */
	@RequestMapping(value="/{clubName}/closeClub", method=RequestMethod.GET)
	public String closeClubForm(@PathVariable String clubName, 
			HttpSession session, Model model) {
		boolean checkMaster = userService.isThisUserClubMaster(
				SessionUtil.getLoginUserName(session), clubName);
		Club club = clubService.getClubInformation(clubName);
		
		model.addAttribute("checkMaster", checkMaster);
		model.addAttribute("club", club);
		
		return "forms/close_club_form";
	}
	
	/* closeClub: 동아리 폐쇄 */
	@RequestMapping(value="/{clubName}/closeClub", method=RequestMethod.POST)
	public String closeClub(@PathVariable String clubName, Model model) {
		String errorCode = "";
		
		// 동아리 회원 이름 가져오기(삭제하기 전에)
		List<String> clubMemberNames = clubService.getClubMemberNames(clubName);
					
		if(clubService.deleteClub(clubName)) {
			errorCode = "close success";
			
			//************ 동아리 회원들에게 알림 전송
			// 알림 내용
			String noticeMsg = new StringBuilder().append("동아리 ").append(clubName)
					.append("(이)가 폐쇄 되었습니다.").toString();
			
			for(String memberName : clubMemberNames) {	
				// 알림 객체
				Notice notice = new Notice(memberName, noticeMsg);
				
				// 알림 삽입
				noticeService.writeNotice(notice);
			}
		} else {
			errorCode = "close failure";
		}
		
		model.addAttribute("club", clubService.getClubInformation(clubName));
		model.addAttribute("errorCode", errorCode);
		
		// 결과 통보 및 리다이렉트
		return "results/close_club_result";
	}
}
