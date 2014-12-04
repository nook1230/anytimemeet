<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
	<c:url var="docRootUrl" value="/" />
	<c:url var="userRootUrl" value="/user" />
	<c:url var="resourcRootUrl" value="/res" />
	<div id="header_wrap">
		<div id="header">
			<div id="toolbar" class="pull-right">
				<script> 
					function logout() { location.assign('${userRootUrl}/logout?redirect=' + location.href); }
				</script>
				<ul>
				<c:choose>
					<c:when test="${sessionScope.login != null && sessionScope.login == 'loginSuccess'}">
					<li>
						<span><img src="${resourcRootUrl}/icon/user-icon-16.png" /></span>
						<span style="vertical-align: top;">${sessionScope.loginUserName}님</span>
					</li> 
					<li><a onclick="logout();" class="btn">로그아웃</a></li>
					<li><a href="${userRootUrl}/mypage/${sessionScope.loginUserName}">마이페이지</a></li>
					</c:when>
					
					<c:otherwise>
					<li>
						<form action="${userRootUrl}/login" method="post">
							<input type="hidden" name="redirect" id="redirect" />
							<script>
								function setRedirect() { document.getElementById("redirect").setAttribute("value", location.href); return true; }
							</script>
							<input type="text" name="userName" placeholder="User Name" style="ime-mode: disabled;"/>
							<input type="password" name="passwd" placeholder="Password" style="ime-mode: disabled;"/>
							<input type="submit" value="login" onclick="return setRedirect();" />
							<a href="${userRootUrl}/signup">회원가입</a>
						</form>
					</li>
					</c:otherwise>
				</c:choose>
				</ul>
			</div>
			<h3><a href="${docRootUrl}">Anytime Meet</a></h3>
		</div>	<!-- header End -->
	</div>
		