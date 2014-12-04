package com.mamascode.controller;

/****************************************************
 * HomeController: 메인 페이지 관리
 * 
 * annotation driven handler
 * 관리 url: "/"
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mamascode.service.ClubService;
import com.mamascode.service.NoticeService;
import com.mamascode.service.UserService;
import com.mamascode.utils.ListHelper;
import com.mamascode.utils.SessionUtil;
import com.mamascode.model.Club;
import com.mamascode.model.ClubCategory;
import com.mamascode.model.Notice;
import com.mamascode.model.User;

@Controller
@RequestMapping("/")
public class HomeController {
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// Services
	@Autowired private UserService userService;
	@Autowired private ClubService clubService;
	@Autowired private NoticeService noticeService;
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	public void setNoticeService(NoticeService noticeService) {
		this.noticeService = noticeService;
	}
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// Handling methods
	
	/* showHome: 웹 사이트의 메인 페이지를 보여준다 */
	@RequestMapping({"/", "/home", "/index"})
	public String showHome(HttpSession session, Model model,
			/* parameter: mc_page(my club page) */
			@RequestParam(value="mc_page", required=false, defaultValue="1") int mcPage) {
		// 로그인 상태인지 체크
		if(SessionUtil.isLoginStatus(session)) {
			// 로그인 상태라면 사용자의 동아리 목록을 가져온다
			String userName = SessionUtil.getLoginUserName(session);
			final int clubPerPage = 5;
			User user = userService.getUserAccountByUserName(userName);
			ListHelper<Club> loginUserClubs = clubService.getUserClubList(mcPage, userName, clubPerPage);
			
			// 로그인 상태라면 알림 목록을 가져온다(아직 읽지 않은 알림 7개, 신규)
			ListHelper<Notice> noticeListHelper = noticeService.getNotices(
					userName, 1, 7, NoticeService.NOTICE_READ_IGNORE);
			
			model.addAttribute("user", user);
			model.addAttribute("loginUserClubs", loginUserClubs);
			model.addAttribute("noticeListHelper", noticeListHelper);
		}
		
		// 대분류 카테고리 가져오기
		List<ClubCategory> grandClubCategory = clubService.getClubGrandCategories();
		
		// 신규 동아리나 hot 동아리 등의 목록
		ListHelper<Club> clubListHelperNew = clubService.getClubList(
				ClubService.LIST_RECENTLY, 1, "", 15, ClubService.ORDER_BY_DATE_DESC);
		
		// 모델 바인딩
		model.addAttribute("clubListHelperNew", clubListHelperNew);
		model.addAttribute("grandClubCategory", grandClubCategory);
		
		return "index";
	}
}
