package com.mamascode.controller;

/****************************************************
 * UserController
 * 
 * annotation driven handler
 * handling url: /user
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.multipart.MultipartFile;

import com.mamascode.exceptions.FilePictureUploadFailedException;
import com.mamascode.model.Notice;
import com.mamascode.model.ProfilePicture;
import com.mamascode.model.User;
import com.mamascode.model.utils.ProcessingResult;
import com.mamascode.service.ClubService;
import com.mamascode.service.NoticeService;
import com.mamascode.service.UserService;
import com.mamascode.utils.ListHelper;
import com.mamascode.utils.SecurityUtil;
import com.mamascode.utils.SessionUtil;
import com.mamascode.model.utils.PopupInfo;

@Controller
@SessionAttributes("user")
@RequestMapping("/user")
public class UserController {
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// 상수
	
	// 프로필 사진 최대 크기(500KB)
	private final static int PROFILE_PICTURE_MAX_SIZE = 500 * 1024;
	
	// 프로필 사진 파일 업로드 경로
	@Value("${application.profilePictureUploadPath}") private String PROFILE_PICTURE_UPLOAD_PATH;
	
	// 프로필 사진 파일 허용 포맷(이미지 파일만 허용함)
	private String[] allowedProfilePictureFormat = {"image/jpeg", "image/jpg", "image/png"};
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// 서비스 인터페이스
	@Autowired private UserService userService;
	@Autowired private ClubService clubService;
	@Autowired private NoticeService noticeService;
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	public void setClubService(ClubService clubService) {
		this.clubService = clubService;
	}
	
	public void setNoticeService(NoticeService noticeService) {
		this.noticeService = noticeService;
	}
	
	// controller methods //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// 사용자 정보
	
	/* mypage: 사용자 정보 조회 */
	@RequestMapping(value="/mypage/{userName}", method=RequestMethod.GET)
	public String mypage(@PathVariable String userName, Model model, HttpSession session) {
		User user = userService.getUserAccountByUserName(userName);	// 사용자 정보
		
		// 유효한 사용자인지
		boolean checkValidUser = false;
		if(SessionUtil.isValidUser(session, userName))
			checkValidUser = true;
		
		// 모델 바인딩
		model.addAttribute("user", user);
		model.addAttribute("checkValidUser", checkValidUser);
		return "mypage";
	}
	
	/*************** 사용자 정보 변경 ***************/
	/* updateUserInfoForm: 정보 변경 폼(GET) */
	@RequestMapping(value="/update_myinfo/{userName}", method=RequestMethod.GET)
	public String updateUserInfoForm(@PathVariable String userName, 
			Model model, HttpSession session) {
		User user = userService.getUserAccountByUserName(userName);
		
		// 유효한 사용자인지
		boolean checkValidUser = false;
		if(SessionUtil.isValidUser(session, userName))
			checkValidUser = true;
		
		model.addAttribute("user", user);
		model.addAttribute("checkValidUser", checkValidUser);
		return "forms/update_myinfo_form";
	}
	
	/* updateUserInfo: 사용자 정보 변경 처리 */
	@RequestMapping(value="/update_myinfo/{userName}", method=RequestMethod.POST)
	public String updateUserInfo(@PathVariable String userName, @ModelAttribute @Valid User user, 
			@RequestParam("profile_pic") MultipartFile profilePictureFile,
			BindingResult bindingResult, SessionStatus sessionStatus, Model model, HttpSession session) {		
		// 파라미터 필터링
		String nickname = SecurityUtil.replaceScriptTag(user.getNickname(), false, null);
		String userRealName = SecurityUtil.replaceScriptTag(user.getUserRealName(), false, null);
		String userIntroduction = SecurityUtil.replaceScriptTag(user.getUserIntroduction(), false, null);
		
		if(!nickname.equals("")) user.setNickname(nickname);
		if(!userRealName.equals("")) user.setUserRealName(userRealName);
		if(!userIntroduction.equals("")) user.setUserIntroduction(userIntroduction);
		
		// 파일 업로드 관련 체크 변수들
		boolean fileUploadResult = true;
		boolean formatCheck = false;
		boolean sizeCheck = profilePictureFile.getSize() <= PROFILE_PICTURE_MAX_SIZE;
		ProfilePicture oldProfilePicture = user.getProfilePicture();	// 기존 사진 정보
		
		// 업로드 파일이 허용된 포맷인지 체크
		for(String allowedFormat : allowedProfilePictureFormat) {
			if(profilePictureFile.getContentType().equals(allowedFormat)) {
				formatCheck = true;
				break;
			}
		}
		
		// 프로필 사진 업로드
		if(!profilePictureFile.isEmpty() && profilePictureFile.getSize() != 0) {
			// 업로드 파일이 존재하는 경우
			
			// 새 프로필 사진 정보 설정
			ProfilePicture profilePicture = new ProfilePicture();
			profilePicture.setUserName(user.getUserName());
			profilePicture.setFileName(profilePictureFile.getOriginalFilename());
			user.setProfilePicture(profilePicture);
			
			user.getProfilePicture().setFileExist(true);	// 파일 정보에 업로드 사진이 있음을 표시 
			
			// 크기와 포맷이 적절한 경우에는 파일을 업로드한다
			if(sizeCheck && formatCheck) {
				// 타겟(임시 파일을 복사할 곳) 경로 지정
				String targetFilePath = PROFILE_PICTURE_UPLOAD_PATH + profilePicture.getUserProfilePictureName();
				
				// 임시 파일 복사(이동) 함수에 사용할 파일 객체 파라미터
				File targetFile = new File(targetFilePath);
				
				try {
					// 파일 복사(임시 파일을 타겟에 지정된 경로에 저장함)
					profilePictureFile.transferTo(targetFile);
				} catch (IllegalStateException e) {
					throw new FilePictureUploadFailedException("File upload failed: IllegalStateException", e);
				} catch (IOException e) {
					throw new FilePictureUploadFailedException("File upload failed: IOException", e);
				}
				
				fileUploadResult = targetFile.exists() && targetFile.isFile()
						&& (targetFile.length() == profilePictureFile.getSize());
				
				if(fileUploadResult) {
					// 파일 업로드 성공 시 기존 파일 삭제
					if(oldProfilePicture != null) {
						String oldFilePath = PROFILE_PICTURE_UPLOAD_PATH + oldProfilePicture.getUserProfilePictureName(); 
						File oldFile = new File(oldFilePath);
						oldFile.delete();
					}
				}
			} // 파일이 존재하지만 규정에 어긋난 경우
			
		} else {
			// 업로드 파일이 존재하지 않는 경우
			// 업로드 파일이 존재하지 않는다는 것을 User 객체(필드 중 ProfilePicture)에 표시
			// 서비스 계층에서 이를 보고 DB 갱신 여부를 판단한다
			if(user.getProfilePicture() != null)
				user.getProfilePicture().setFileExist(false); // 파일 정보에 업로드 사진이 없음을 표시
			else {
				// DB 검색 시 사용자의 프로필 사진 정보가 없다면 profilePicture 필드는 null이 된다
				// 이 경우 더미 객체를 생성해서 업로드 파일 존재 여부를 표시해줌
				ProfilePicture dummyPicture = new ProfilePicture();
				dummyPicture.setFileExist(false); // 파일 정보에 업로드 사진이 없음을 표시
				user.setProfilePicture(dummyPicture);
			}
		}
		
		// 에러 체크 후에 모든 것이 정상이라면 서비스 계층의 갱신 메소드를 호출
		if(bindingResult.hasErrors() || 
				(user.getProfilePicture().isFileExist() && !sizeCheck) ||
				(user.getProfilePicture().isFileExist() && !formatCheck) ||
				(user.getProfilePicture().isFileExist() && !fileUploadResult) || 
				!userService.setUserAccount(user)) {
			// 유효한 사용자인지
			boolean checkValidUser = false;
			if(SessionUtil.isValidUser(session, userName))
				checkValidUser = true;
			
			// 오류가 있는 경우 파일 정보를 기존 파일 정보로 재설정해줌
			user.setProfilePicture(oldProfilePicture);
			
			// 오류 정보 출력에 사용할 모델 객체들 추가
			model.addAttribute("user", user);
			model.addAttribute("checkValidUser", checkValidUser);
			model.addAttribute("sizeCheck", sizeCheck);
			model.addAttribute("formatCheck", formatCheck);
			model.addAttribute("fileUploadResult", fileUploadResult);
			return "forms/update_myinfo_form"; // 오류 존재시 폼 재출력
		}
		sessionStatus.setComplete();	// 세션 정리
		return "redirect:/user/mypage/" + user.getUserName();
	}
	
	/*************** 비밀번호 변경 ***************/
	/* changePassForm: 변경 폼 */
	@RequestMapping(value="/change_pass/{userName}", method=RequestMethod.GET)
	public String changePassForm(@PathVariable String userName, Model model, HttpSession session) {
		// 유효한 사용자인지
		boolean checkValidUser = false;
		if(SessionUtil.isValidUser(session, userName))
			checkValidUser = true;
				
		model.addAttribute("userName", userName);
		model.addAttribute("checkValidUser", checkValidUser);
		return "/forms/change_passwd_form";
	}
	
	/* changePass: 변경 처리 */
	@RequestMapping(value="/change_pass/{userName}", method=RequestMethod.POST)
	public String changePass(@PathVariable String userName,
			@RequestParam("oldPasswd") String oldPasswd, 
			@RequestParam("newPasswd1") String newPasswd1, 
			@RequestParam("newPasswd2") String newPasswd2) {
		
		// 비밀번호 체크
		if(!userService.login(userName, oldPasswd))
			return "/forms/change_passwd_form";
		
		// parameter filtering
		// TODO 자바 정규 표현식 문제: 왜 특수기호(&^* 등)를 넣으면 정상적으로 작동하지 않는지
		// 모두 false를 내보낸다(혹은 컴파일 오류)
		boolean match = Pattern.matches("^[a-zA-Z0-9]+$", newPasswd1);
		boolean checkParamters = true;
		if(newPasswd1.length() > 20 || newPasswd1.length() < 4 || 
				newPasswd2.length() > 20 || newPasswd2.length() < 4 ||
				!newPasswd1.equals(newPasswd2) || !match) {
			checkParamters = false;
		}
		
		if(!checkParamters || !userService.changePassword(userName, newPasswd1)) {
			return "/forms/change_passwd_form";
		}
			
		return "redirect:/user/mypage/" + userName;
	}
	
	// login & logout ///////////////////////////////////////////////
	/* login: 로그인 처리 */
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String login(@RequestParam("userName") String userName, @RequestParam("passwd") String passwd,  
			@RequestParam("redirect") String redirect, HttpSession session, Model model) {
		String viewName = "redirect:" + redirect;
		
		if(!userName.equals("") && !passwd.equals("") && 
				userService.login(userName, passwd)) {
			// 사용자 정보 세션에 저장
			User user = userService.getUserAccountByUserName(userName);
			session.setAttribute(SessionUtil.loginStatusAttr, SessionUtil.loginSuccess);
			session.setAttribute(SessionUtil.loginUserNameAttr, user.getUserName());
			session.setAttribute(SessionUtil.loginUserNoAttr, user.getUserNo());
						
			// 조회수 조작을 위한 세션 객체
			Map<Integer, Timestamp> articleAccessTimeTable = new HashMap<Integer, Timestamp>();
			session.setAttribute(SessionUtil.articleAccessTimeTableMapAttr, articleAccessTimeTable);
		} else {
			model.addAttribute("errorCode", "login failure");
			viewName = "errors/login_error";
		}
		
		return viewName;
	}
	
	/* logout: 로그아웃 처리 */
	@RequestMapping(value="/logout", method=RequestMethod.GET)
	public String logout(@RequestParam("redirect") String redirect, HttpSession session, Model model) {
		String viewName = "redirect:" + redirect;
		
		if(!logout(session)) {
			// logout failure
			model.addAttribute("errorCode", "logout failure");
			viewName = "errors/login_error";
		}
		
		return viewName;
	}
	
	/* logout: 내부 메소드 */
	private boolean logout(HttpSession session) {
		if(session.getAttribute(SessionUtil.loginStatusAttr) != null && 
			session.getAttribute(SessionUtil.loginStatusAttr).equals(SessionUtil.loginSuccess)) {
			
			// clear session attributes
			session.setAttribute(SessionUtil.loginStatusAttr, SessionUtil.logoutStatus);
			session.removeAttribute(SessionUtil.loginUserNameAttr);
			session.removeAttribute(SessionUtil.loginUserNoAttr);
			
			session.removeAttribute(SessionUtil.articleAccessTimeTableMapAttr);
			
			return true;
		} else {
			return false;
		}
	}
	
	// 회원 가입 ///////////////////////////////////////////////
	/* signupForm: 회원 가입 폼 */
	@RequestMapping(value="/signup", method=RequestMethod.GET)
	public String signupForm(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		return "forms/signup_form";
	}
	
	/* signup: 회원 가입 처리 
	 * JSR-303 검증 */
	@RequestMapping(value="/signup", method=RequestMethod.POST)
	public String signup(@ModelAttribute @Valid User user, 
			BindingResult bindingResult, SessionStatus sessionStatus) {
		if(bindingResult.hasErrors() || !user.getPasswd().equals(user.getPasswd2()) ||
				!userService.createNewUserAccount(user)) {
			return "forms/signup_form";	// 오류 존재 시 폼 다시 출력
		}
		
		// 회원가입 알림 넣기
		Notice notice = new Notice(user.getUserName(), "회원 가입을 축하드립니다!");
		noticeService.writeNotice(notice);
		
		sessionStatus.setComplete();	// 성공시 세션 정리
		return "redirect:/";
	}
	
	/* checkUserName: 사용자 계정 이름 중복 체크 - 중복이면 true를 반환(ajax) */
	@RequestMapping(value="/checkUserName", method=RequestMethod.GET)
	@ResponseBody
	public boolean checkUserName(@RequestParam("userName") String userName) {
		return userService.checkUserName(userName);
	}
	
	/* checkEmail: 이메일 계정 주소 중복 체크 - 중복이면 true를 반환(ajax) */
	@RequestMapping(value="/checkEmail", method=RequestMethod.GET)
	@ResponseBody
	public boolean checkEmail(@RequestParam("email") String email) {
		return userService.checkEmail(email);
	}
	
	// 동아리 가입 신청 ///////////////////////////////////////////////
	/* cancelApplForm: 가입 신청 취소 폼(팝업) */
	@RequestMapping(value="/cancelAppl", method=RequestMethod.GET)
	public String cancelApplForm(@RequestParam("clubName") String clubName, 
			@RequestParam("userName") String userName, Model model, HttpSession session) {
		//// 팝업 정보
		String title = "동아리 가입 신청";
		String comment = "가입 신청을 취소하시겠습니까?";
		String url = "/user/cancelAppl";
		boolean access = SessionUtil.isValidUser(session, userName);
		
		PopupInfo pInfo = new PopupInfo(title, comment, url, access);
		
		// hidden fields 
		Map<String, Object> hiddens = new HashMap<String, Object>();
		hiddens.put("userName", userName);
		hiddens.put("clubName", clubName);		
		pInfo.setHiddens(hiddens);
		
		model.addAttribute("popupInfo", pInfo);
		
		return "forms/popup/popup_basic";
	}
	
	/* cancelApplication: 가입 신청 취소 처리 */
	@RequestMapping(value="/cancelAppl", method=RequestMethod.POST)
	public String cancelApplication(@RequestParam("clubName") String clubName, 
			@RequestParam("userName") String userName, Model model) {
		ProcessingResult result = new ProcessingResult(
				"동아리 가입 신청 취소", "동아리 가입 신청 취소", 
				"가입 신청이 취소되었습니다", "취소 중 오류 발생");
		
		if(clubService.cancelApplication(clubName, userName)) {
			result.setResult(true);	// 성공
		} else {
			// 실패: 오류 원인은 알 수 없다(서비스 내부적인 원인은 알려주지 않음)
			result.setErrorCause("알 수 없는 오류");
		}
		
		model.addAttribute("result", result);	// 처리 결과
		return "results/processing_result";
	}
	
	////////////////////////////////////////////////////////////////////////////////
	
	/* approveClubInvitation: 가입 초대 확인 폼(팝업 용) */
	@RequestMapping(value="/{userName}/confirmInvitation/{clubName}", method=RequestMethod.GET)
	public String approveClubInvitation(@PathVariable String userName, 
			@PathVariable String clubName, HttpSession session, Model model) {
		/////////// 팝업 정보
		String title = "동아리 회원 가입 초대 확인: " + clubName;	// 타이틀
		String comment = clubName + "로 가입 하시겠습니까?";	// 코멘트
		String url = "user/" + userName + "/confirmInvitation/" + clubName;	// post action url
		
		// 접근 권한: master만
		boolean access = SessionUtil.isValidUser(session, userName);
		
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
	
	/* joinClub: 가입 초대 승낙 처리 */	
	@RequestMapping(value="/{userName}/confirmInvitation/{clubName}", method=RequestMethod.POST)
	public String joinClub(@PathVariable String clubName, 
			@RequestParam("userName") String userName, Model model) {		
		// 처리 결과 정보 객체
		ProcessingResult result = new ProcessingResult(
				"동아리 회원 초대 승낙", "동아리 회원 초대 승낙", 
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
	
	////////////////////////////////////////////////////////////////////////////////
	
	/* viewUserProfile: 사용자 프로필 정보 */
	@RequestMapping(value="/profile/{userName}", method=RequestMethod.GET)
	public String viewUserProfile(@PathVariable String userName, 
			Model model, HttpSession session) {
		// 사용자 정보
		User user = userService.getUserAccountByUserName(userName);
		
		// 접근 권한 체크(로그인 사용자)
		boolean loginCheck = SessionUtil.isLoginStatus(session);
		
		// 모델 바인딩
		model.addAttribute("user", user);
		model.addAttribute("loginCheck", loginCheck);
		
		return "view_user_profile";
	}
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// 알림 관리
	
	/* readNotice: 알림 읽음 표시(ajax) */
	@RequestMapping(value="/notice/readNotice", method=RequestMethod.POST)
	@ResponseBody
	public int readNotice(@RequestParam("noticeId") int noticeId) {
		return noticeService.readNotice(noticeId);
	}
	
	/* deleteNotice: 알림 삭제 처리(ajax) */
	@RequestMapping(value="/notice/deleteNotice", method=RequestMethod.POST)
	@ResponseBody
	public int deleteNotice(@RequestParam("noticeId") int noticeId) {
		return noticeService.deleteNotice(noticeId);
	}
	
	/* getNotices: 알림 목록 최신 7개(ajax) */
	@RequestMapping(value="/notice/getNotices/{userName}", method=RequestMethod.GET)
	@ResponseBody
	public List<Notice> getNotices(@PathVariable String userName) {
		
		ListHelper<Notice> listHelper = noticeService.getNotices(
				userName, 1, 7, NoticeService.NOTICE_READ_IGNORE);
		
		if(listHelper != null) {
			return listHelper.getList();
		} else {
			return Collections.emptyList();
		}
	}
	
	/* getNoticesAll: 알림 목록(전체) */
	@RequestMapping(value="/notice/getNoticesAll/{userName}", method=RequestMethod.GET)
	public String getNoticesAll(@PathVariable String userName,
			@RequestParam(value="page", required=false, defaultValue="1") int page,
			Model model, HttpSession session) {
		// 접근 권한
		boolean checkValidUser = SessionUtil.isValidUser(session, userName);
		String loginUserName = SessionUtil.getLoginUserName(session);
		
		// 알림 목록
		ListHelper<Notice> listHelper = 
				noticeService.getNotices(userName, page, 20, NoticeService.NOTICE_READ_IGNORE);
		
		// 모델 바인딩
		model.addAttribute("checkValidUser", checkValidUser);
		model.addAttribute("noticeListHelper", listHelper);
		model.addAttribute("loginUserName", loginUserName);
		
		return "view_my_alarms";
	}
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// 계정 인증하기
	// 원래는 이메일에 인증 링크를 첨부해서 인증 절차를 진행하는 것을 고려했으나
	// 메일 서버 등의 문제가 있어서 생략. 원래는 이메일 계정 1개에 사이트 계정 1개를 제공하는 것을 고려
	// 무한 가입을 방지하기 위한 방편으로 고려된 기능임
	// 인증 절차는 마이페이지에서 링크를 클릭함으로써 이루어진다.
	// 이메일을 이용한다면, 메일 본문에 링크를 첨부해서 절차가 진행되도록 유도한다
	
	/* certifyAccountForm: 인증 페이지 */
	@RequestMapping(value="/certify", method=RequestMethod.GET)
	public String certifyAccountForm(@RequestParam("userName") String userName, 
			@RequestParam("certiKey") String certificationKey, Model model) {
		// 사용자 정보: 인증키 확인을 위해
		User user = userService.getUserAccountByUserName(userName);
		
		// 인증키가 맞는지 확인
		boolean certificationSuccess =
				!user.isCertified() && (user.getCertificationKey().equals(certificationKey));
		
		// 모델 바인딩
		model.addAttribute("user", user);
		model.addAttribute("certificationSuccess", certificationSuccess);
		
		// 실제 인증 처리를 할 페이지로 이동(ajax 이용)
		return "forms/account_certification";
	}
	
	/* certifyAccount: 인증 처리(ajax) */
	@RequestMapping(value="/certify", method=RequestMethod.POST)
	@ResponseBody
	public boolean certifyAccount(@RequestParam("userName") String userName, 
			@RequestParam("certiKey") String certificationKey) {
		return userService.certifyUserAccount(userName, certificationKey);
	}
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// 회원 탈퇴
	
	/* deleteAccountForm: 회원 탈퇴 폼 */
	@RequestMapping(value="/deleteAccount/{userName}", method=RequestMethod.GET)
	public String deleteAccountForm(@PathVariable String userName, 
			Model model, HttpSession session) {
		boolean checkValidUser = SessionUtil.isValidUser(session, userName);
		model.addAttribute("checkValidUser", checkValidUser);
		
		return "forms/delete_account_form";
	}
	
	/* deleteAccount: 회원 탈퇴 처리 */
	@RequestMapping(value="/deleteAccount/{userName}", method=RequestMethod.POST)
	public String deleteAccount(@PathVariable String userName, Model model,
			@RequestParam("passwd") String passwd, HttpSession session) {
		int result = UserService.DEL_ERR_FAILURE;
		
		if(SessionUtil.isLoginStatus(session)) {
			// 로그인 상태라면 로그아웃 시켜준다
			logout(session);
		}
		
		// 비밀번호 확인 후 삭제 처리
		if(userService.login(userName, passwd))
			result = userService.deleteUserAccount(userName, PROFILE_PICTURE_UPLOAD_PATH);
		else
			result = UserService.DEL_ERR_FAILURE_PASSWORD;
		
		model.addAttribute("delete_result", result);
		
		return "errors/delete_account_result";
	}
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// 알림 관리
	
	/* readNotice: 알림 읽음 표시(ajax) */
	@RequestMapping(value="/search/getUserName", method=RequestMethod.GET)
	@ResponseBody
	public String getUserName(@RequestParam("keyword") String keyword) {
		String userName = userService.searchUserName(keyword);
		
		if(userName == null)
			return "";
		
		return userName;
	}
}
