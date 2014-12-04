<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
			<div id="left-nav" class="pull-left">
				<ul>
					<li><a href="${userRootUrl}/mypage/${sessionScope.loginUserName}">회원정보</a></li>
					<li><a href="${userRootUrl}/update_myinfo/${sessionScope.loginUserName}">회원정보 수정</a></li>
					<li><a href="${userRootUrl}/change_pass/${sessionScope.loginUserName}">비밀번호 수정</a></li>
					<li><a href="${userRootUrl}/notice/getNoticesAll/${sessionScope.loginUserName}">알림 목록</a></li>
					<li><a href="${userRootUrl}/deleteAccount/${sessionScope.loginUserName}">회원 탈퇴</a></li>
				</ul>
			</div>	<!-- left-nav end -->
			