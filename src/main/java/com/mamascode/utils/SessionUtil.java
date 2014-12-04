package com.mamascode.utils;

/**************************************
 * SessionUtil
 * 
 * 세션 관리를 위한 편의 기능 제공
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 *   
 * 최종 업데이트: 2014. 11. 17
***************************************/

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

public class SessionUtil {
	//////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////
	// constants
	
	//// 로그인 상태 속성
	public final static String loginStatusAttr = "login";
	public final static String loginUserNameAttr = "loginUserName";
	public final static String loginUserNoAttr = "loginUserNo";
	
	//// 로그인 상태 값
	public final static String loginSuccess = "loginSuccess";
	public final static String loginFailed = "loginFailed";
	public final static String logoutStatus = "logoutStatus";
	
	//// 게시물 조회수 관련 속성
	public final static String articleAccessTimeTableMapAttr = "articleAccessTimeTableMap";
	
	//////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////
	// static methods
	
	// isLoginStatus: 로그인 상태인지 체크
	public static boolean isLoginStatus(HttpSession session) {
		if(session != null && session.getAttribute(loginStatusAttr) != null && 
				session.getAttribute(loginStatusAttr).equals(loginSuccess))
			return true;
		
		return false;
	}
	
	// isLoginUser: 로그인한 사용자인지(Depreciated)
	public static boolean isLoginUser(HttpSession session, String userName) {
		if(isLoginStatus(session) && 
				session.getAttribute(loginUserNameAttr) != null &&
				session.getAttribute(loginUserNameAttr).equals(userName))
			return true;
		
		return false;
	}
	
	// getLoginUserName: 로그인 사용자 아이디(user name) 가져오기
	public static String getLoginUserName(HttpSession session) {
		if(isLoginStatus(session))
			return (String) session.getAttribute(loginUserNameAttr);
		
		// 로그인 상태가 아니라면 빈 문자열을 반환한다
		return "";
	}
	
	// isValidUser: userName이 로그인 사용자 이름과 같은지 체크
	public static boolean isValidUser(HttpSession session, String userName) {
		boolean checkValidUser = false;
		
		if(SessionUtil.isLoginStatus(session)) {
			String loginUserName = getLoginUserName(session);
			
			if(userName != null && userName.equals(loginUserName)) {
				checkValidUser = true;
			}	
		}
		
		return checkValidUser;
	}
	
	// getArticleAccessTimeTable: 게시물 조회수 카운팅과 관련해서 조회 시간 체크
	@SuppressWarnings("unchecked")
	public static Map<Integer, Timestamp> getArticleAccessTimeTable(HttpSession session) {
		if(SessionUtil.isLoginStatus(session)) {
			return (Map<Integer, Timestamp>) session.getAttribute(articleAccessTimeTableMapAttr);
		} else {
			return new HashMap<Integer, Timestamp>();
		}
	}
}
