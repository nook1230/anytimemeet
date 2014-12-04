package com.mamascode.controller;

/****************************************************
 * ClubArticleController
 *
 * annotation 기반 핸들러 
 * 관리 url: /club/article
 * 동아리 관리 화면 담당
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
import java.sql.Timestamp;
import java.util.ArrayList;
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

import com.mamascode.model.ClubArticle;
import com.mamascode.model.Notice;
import com.mamascode.model.Reply;
import com.mamascode.model.User;
import com.mamascode.service.ClubService;
import com.mamascode.service.NoticeService;
import com.mamascode.service.UserService;
import com.mamascode.utils.ListHelper;
import com.mamascode.utils.SecurityUtil;
import com.mamascode.utils.SessionUtil;
import com.mamascode.model.Club;
import com.mamascode.model.utils.PopupInfo;
import com.mamascode.model.utils.ProcessingResult;
import com.mamascode.service.ClubArticleService;

@Controller
@SessionAttributes("clubArticle")
@RequestMapping("/club/article")
public class ClubArticleController {
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// 서비스 인터페이스
	@Autowired ClubArticleService articleService;
	@Autowired ClubService clubService;
	@Autowired UserService userService;
	@Autowired private NoticeService noticeService;
	
	public void setArticleService(ClubArticleService articleService) {
		this.articleService = articleService;
	}
	
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
	// 게시물 작성, 수정, 삭제
	/* writeNewArticleForm: 게시물 등록 폼 */
	@RequestMapping(value="/{clubName}/writeNewArticle", method=RequestMethod.GET)
	public String writeNewArticleForm(@PathVariable String clubName, 
			Model model, HttpSession session) {
		boolean checkLogin = SessionUtil.isLoginStatus(session);
		boolean checkMember = clubService.isThisUserInThisClub(
				clubName, SessionUtil.getLoginUserName(session));
		String writerName = SessionUtil.getLoginUserName(session);
		
		ClubArticle clubArticle = new ClubArticle();
		clubArticle.setClubName(clubName);
		clubArticle.setWriterName(writerName);
		
		model.addAttribute("checkLogin", checkLogin);
		model.addAttribute("clubArticle", clubArticle);
		model.addAttribute("checkMember", checkMember);
		
		return "forms/write_new_club_article_form";
	}
	
	/* writeNewArticle: 게시물 등록 - JSR-303 검증 */
	@RequestMapping(value="/{clubName}/writeNewArticle", method=RequestMethod.POST)
	public String writeNewArticle(@PathVariable String clubName,
			@ModelAttribute @Valid ClubArticle clubArticle,
			BindingResult bindingResult, SessionStatus sessionStatus,
			HttpSession session, Model model) {
		// 성공시 게시물 id
		int articleId = 0;
		
		// 파라미터 검정 및 필터링(제목과 내용)
		String title = SecurityUtil.replaceScriptTag(clubArticle.getTitle(), false, null);
		if(!title.equals("")) clubArticle.setTitle(title);
		
		// 본문은 html 태그 사용을 위해 자바스크립트 태그만 제거한다
		String content = SecurityUtil.replaceJavaScriptTag(clubArticle.getContent());
		if(!content.equals("")) clubArticle.setContent(content);
		
		// 오류가 있으면 폼을 재출력
		if(bindingResult.hasErrors() || 
				(articleId = articleService.writeNewWrticle(clubArticle)) == 0) {
			boolean checkLogin = SessionUtil.isLoginStatus(session);
			boolean checkMember = clubService.isThisUserInThisClub(
					clubName, SessionUtil.getLoginUserName(session));
			
			model.addAttribute("checkLogin", checkLogin);
			model.addAttribute("checkMember", checkMember);
			
			return "forms/write_new_club_article_form";
		}
				
		sessionStatus.setComplete();	// 성공시 세션 정리
		return "redirect:/club/article/" + clubName + 
				"/readArticle/" + articleId;	// 리다이렉트: 게시물 읽기
	}
	
	/* deleteArticleForm: 게시글 삭제 폼(팝업) */
	@RequestMapping(value="/{clubName}/deleteArticle/{articleId}", method=RequestMethod.GET)
	public String deleteArticleForm(@PathVariable String clubName, 
			@PathVariable int articleId, 
			@RequestParam(value="redirect_page", required=false, defaultValue="1") int redirectPage,
			Model model, HttpSession session) {
		
		ClubArticle article = articleService.readArticle(articleId, false);
		
		/////////// 팝업 정보
		String title = "동아리 게시물 삭제: #" + article.getTitle();	// 타이틀
		
		// 코멘트
		String comment = "이 게시물을 삭제하겠습니까?: " + article.getTitle();
		
		String url = "club/article/" + clubName + "/deleteArticle/" + articleId;	// post action url
		
		// 접근 권한 체크: 로그인 멤버 & 동아리 회원 & 게시글 작성자 or 동아리 마스터
		String loginUserName = (String) session.getAttribute(SessionUtil.loginUserNameAttr);
		boolean access = (clubService.isThisUserInThisClub(
				clubName, SessionUtil.getLoginUserName(session))) // 동아리 회원인지 확인
				&& (article.getWriterName() != null // 게시글 작성자 확인
					&& article.getWriterName().equals(loginUserName)) || 
					// 동아리 마스터인지 확인
					(userService.isThisUserClubMaster(
							SessionUtil.getLoginUserName(session), clubName));
		
		PopupInfo pInfo = new PopupInfo(title, comment, url, access);
		
		// hidden fields 
		Map<String, Object> hiddens = new HashMap<String, Object>();
		hiddens.put("articleId", articleId);
		hiddens.put("redirect_page", redirectPage);
		pInfo.setHiddens(hiddens);
		
		// 모델 바인딩(팝업 객체)
		model.addAttribute("popupInfo", pInfo);
		
		return "forms/popup/popup_basic";	
	}
	
	/* deleteArticle: 게시글 삭제 처리(POST) */
	@RequestMapping(value="/{clubName}/deleteArticle/{articleId}", method=RequestMethod.POST)
	public String deleteArticle(@PathVariable String clubName, 
			@PathVariable int articleId, Model model, HttpSession session,
			@RequestParam(value="redirect_page", required=false, defaultValue="1") int redirectPage) {
		// 처리 결과 정보 객체
		ProcessingResult result = new ProcessingResult(
				"게시글 삭제", "게시글 삭제", 
				"게시글이 삭제 되었습니다", "처리 중 오류 발생");
		String successUrl = "club/article/" + clubName + "/articleList?page=" + redirectPage;
		result.setSuccessUrl(successUrl);
		
		if(articleService.deleteArticle(articleId)) {
			result.setResult(true);
		} else {
			result.setErrorCause("내부 오류: 알 수 없는 오류");
		}
				
		model.addAttribute("result", result);
		
		// 성공하면 해당 정보가 삭제되므로, 게시글 목록으로 리다이렉트
		return "results/processing_result_location_assign";
	}
	
	/* modifyArticleForm: 게시물 수정 폼 */
	@RequestMapping(value="/{clubName}/modifyArticle/{articleId}", method=RequestMethod.GET)
	public String modifyArticleForm(@PathVariable String clubName, 
			@PathVariable int articleId, Model model, HttpSession session) {
		// 수정할 게시물 객체
		ClubArticle clubArticle = articleService.readArticle(articleId, false);
		
		// 접근 권한 체크: 로그인 멤버 & 동아리 회원 & 게시글 작성자
		String loginUserName = (String) session.getAttribute(SessionUtil.loginUserNameAttr);
		boolean checkValidUser = (clubService.isThisUserInThisClub(
					clubName, SessionUtil.getLoginUserName(session)) // 동아리 회원인지 확인
				&& (clubArticle.getWriterName() != null // 게시글 작성자 확인
				&& clubArticle.getWriterName().equals(loginUserName)));
		
		// 모델 바인딩
		model.addAttribute("clubArticle", clubArticle);
		model.addAttribute("checkValidUser", checkValidUser);
		
		return "forms/modify_club_article_form";
	}
	
	/* modifyArticle: 게시물 수정 - JSR-303 검증 */
	@RequestMapping(value="/{clubName}/modifyArticle/{articleId}", method=RequestMethod.POST)
	public String modifyArticle(@PathVariable String clubName,
			@PathVariable int articleId, @ModelAttribute @Valid ClubArticle clubArticle,
			BindingResult bindingResult, SessionStatus sessionStatus, Model model, HttpSession session,
			@RequestParam(value="redirect_page", required=false, defaultValue="1") int redirectPage) {
		// 파라미터 검정 및 필터링(제목과 내용)
		String title = SecurityUtil.replaceScriptTag(clubArticle.getTitle(), false, null);
		if(!title.equals("")) clubArticle.setTitle(title);
		
		// 본문은 html 태그 사용을 위해 자바스크립트 태그만 제거한다
		String content = SecurityUtil.replaceJavaScriptTag(clubArticle.getContent());
		if(!content.equals("")) clubArticle.setContent(content);
		
		// 오류가 있으면 폼을 재출력(폼에 출력할 모델 재설정)
		if(bindingResult.hasErrors() || !articleService.updateArticle(clubArticle)) {
			// 접근 권한 체크: 로그인 멤버 & 동아리 회원 & 게시글 작성자
			String loginUserName = (String) session.getAttribute(SessionUtil.loginUserNameAttr);
			boolean checkValidUser = (clubService.isThisUserInThisClub(
						clubName, SessionUtil.getLoginUserName(session)) // 동아리 회원인지 확인
					&& (clubArticle.getWriterName() != null // 게시글 작성자 확인
					&& clubArticle.getWriterName().equals(loginUserName)));
			
			// 모델 바인딩
			model.addAttribute("clubArticle", clubArticle);
			model.addAttribute("checkValidUser", checkValidUser);
			
			return "forms/modify_club_article_form";
		}
		
		return "redirect:/club/article/" + clubName + 
				"/readArticle/" + articleId + "?redirect_page=" + redirectPage;	// 리다이렉트: 게시물 읽기
	}
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// 게시글 읽기
	
	/* readArticle: 게시글 읽기 */
	@RequestMapping(value="/{clubName}/readArticle/{articleId}", method=RequestMethod.GET)
	public String readArticle(@PathVariable String clubName, 
			@PathVariable int articleId, Model model, HttpSession session,
			@RequestParam(value="redirect_page", required=false, defaultValue="1") int redirectPage) {
		// 보안 정보 - 접근 권한
		boolean checkLogin = SessionUtil.isLoginStatus(session);
		boolean checkMember = clubService.isThisUserInThisClub(clubName, 
				SessionUtil.getLoginUserName(session));
		boolean isWriter = false;
		
		
		
		// 게시물을 읽어온다(작성자 정보를 위해) - 조회수는 증가시키지 않음
		ClubArticle article = articleService.readArticle(articleId, false);
		
		// 게시글을 읽는 이가 작성자 자신인지 체크
		String writerName = "";
		if(article != null && article.getWriterName() != null)
			writerName = article.getWriterName();
		
		// 작성자 정보
		User writer = userService.getUserAccountByUserName(writerName);
		
		if(writerName.equals(SessionUtil.getLoginUserName(session)))
			isWriter = true;
		
		///// 게시물 가져오기
		// [조회수 증가 여부 결정]
		// 작성자 이외의 동아리 회원이 조회하는 경우에 조회수 증가
		// 그러나 10분 내 중복 조회일 경우는 증가하지 않음
		// 사용자가 로그아웃 하고 다시 접속한 경우는 카운트 포함
		boolean addViewCount = false;
		Map<Integer, Timestamp> articleAccessTimeTable = SessionUtil.getArticleAccessTimeTable(session);
		Timestamp lastAccessTime = articleAccessTimeTable.get(articleId);
		Timestamp now = new Timestamp(System.currentTimeMillis());
		
		if(checkLogin && !isWriter) {
			// 조회하는 이가 로그인한 사용자이면서, 글쓴이 자신이 아니라면,
			if(lastAccessTime != null) {
				if(now.getTime() - lastAccessTime.getTime() > 600000) {
					// 마지막 조회 이후 10분이 경과한 경우라면 조회수를 카운트한다 
					addViewCount = true;
					
					// 게시물 조회 시간 갱신
					articleAccessTimeTable.remove(articleId);
					articleAccessTimeTable.put(articleId, now);
				}
			} else {
				// 최근 세션에서 조회한 적이 없으므로 조회수 카운트, 게시물 조회 시간 갱신
				articleAccessTimeTable.put(articleId, now);
				addViewCount = true;
			}
		}
		
		// 게시물 가져오기(사용자에게 보여줄 정보)
		article = articleService.readArticle(articleId, addViewCount);
		// 게시물에 대한 댓글
		List<Reply> articleReplyList = 
				articleService.readArticleReplies(articleId);
		
		// 동아리 정보
		Club club = clubService.getClubInformation(clubName);
		
		// 모델 바인딩
		model.addAttribute("article", article);
		model.addAttribute("club", club);
		model.addAttribute("checkLogin", checkLogin);
		model.addAttribute("checkMember", checkMember);
		model.addAttribute("articleReplyList", articleReplyList);
		model.addAttribute("redirectPage", redirectPage);
		model.addAttribute("writer", writer);
		
		// 뷰 이름 리턴
		return "read_club_article";
	}
	
	/* articleList: 게시글 목록(자세한 목록) */
	@RequestMapping(value="/{clubName}/articleList", method=RequestMethod.GET)
	public String articleList(@PathVariable String clubName,
			@RequestParam(value="page", required=false, defaultValue="1") int page,
			Model model, HttpSession session) {
		// 게시물 리스트 가져오기(ListHelper 사용, 페이지 당 게시물 수 = 10)
		ListHelper<ClubArticle> listHelper = 
				articleService.getArticlesForClub(clubName, page, 10);
		
		// 보안정보 - 접근권한(로그인 여부, 멤버 여부)
		boolean checkLogin = SessionUtil.isLoginStatus(session);
		boolean checkMember = clubService.isThisUserInThisClub(
				clubName, SessionUtil.getLoginUserName(session));
		
		// 동아리 정보
		Club club = clubService.getClubInformation(clubName);
		
		// 모델 바인딩
		model.addAttribute("articleListHelper", listHelper);	// 목록
		model.addAttribute("checkLogin", checkLogin);			// 로그인 여부
		model.addAttribute("checkMember", checkMember);			// 멤버 여부
		model.addAttribute("club", club);						// 동아리 정보
		
		return "club_article_list_detail";
	}
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// 댓글 추가, 조회, 수정, 삭제
	
	/* writeReply: 댓글 쓰기(Ajax) */
	@RequestMapping(value="/{clubName}/writeReply/{articleId}", method=RequestMethod.POST)
	@ResponseBody
	public boolean writeReply(@PathVariable String clubName, @PathVariable int articleId,
			@RequestParam("writerName") String writerName, @RequestParam("content") String content) 
					throws UnsupportedEncodingException {	
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
		clubReply.setTargetId(articleId);
		clubReply.setWriterName(writerName);
		clubReply.setContent(content);
		
		boolean result = articleService.writeNewArticleReply(clubReply);
		ClubArticle article = articleService.readArticle(articleId, false); // 게시글 정보
		if(result && !writerName.equals(article.getWriterName())) {
			////////// 알림 전송
			// 알림 내용
			String noticeMsg = new StringBuilder().append("게시글에 댓글이 달렸습니다").toString();
			// 알림 url(게시글 보기)
			String noticeUrl = new StringBuilder().append("club/article/").append(clubName)
					.append("/readArticle/").append(articleId).toString();
			
			// 알림 객체
			Notice notice = new Notice(article.getWriterName(), noticeMsg, noticeUrl);
			
			// 알림 삽입
			noticeService.writeNotice(notice);
		}
		
		return result;
	}
	
	/* readReplies: 댓글 가져오기(Ajax) */
	@RequestMapping(value="/{clubName}/readReplies/{articleId}", method=RequestMethod.GET)
	@ResponseBody
	public List<Reply> readReplies(@PathVariable String clubName, @PathVariable int articleId) {
		List<Reply> repliesList = articleService.readArticleReplies(articleId);
		
		if(repliesList == null) {
			repliesList = new ArrayList<Reply>();
		}
		
		return repliesList;
	}
	
	/* deleteReply: 댓글 삭제 폼(Ajax) */
	@RequestMapping(value="/{clubName}/deleteReply/{replyId}", method=RequestMethod.POST)
	@ResponseBody
	public boolean deleteReply(@PathVariable String clubName, @PathVariable int replyId,
			HttpSession session, Model model) {
		Reply articleReply = articleService.readArticleReply(replyId);
		
		if(articleReply.getWriterName() != null && 
				(userService.isThisUserClubMaster(SessionUtil.getLoginUserName(session), clubName) || 
				articleReply.getWriterName().equals(SessionUtil.getLoginUserName(session))))
			return articleService.deleteArticleReply(replyId);
		
		return false;			
	}
}
