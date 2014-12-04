<%@ page import="com.mamascode.model.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%	
	String userName = "";
	if(request.getAttribute("userName") != null && request.getAttribute("userName") instanceof String)
		userName = (String) request.getAttribute("userName");
%>
<c:url var="resourceRootUrl" value="/res" />
<c:url var="DocRootUrl" value="/" />
<c:url var="userRootUrl" value="/user" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>비밀번호 변경</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	<style>
		#body-content { width: 700px; min-height: 500px; border: 1px solid gray; }
		h2 { margin-top: 5px; margin-bottom: 25px; text-align: center; clear: both; }
	</style>
	
	<script>
		$(document).ready(function() {
			$('input[type="submit"]').click(function(event) {
				event.preventDefault();
				event.stopPropagation();
				
				var oldPasswd = $('#oldPasswd').attr('value');
				var newPasswd1 = $('#newPasswd1').attr('value');
				var newPasswd2 = $('#newPasswd2').attr('value');
				
				oldPasswd = $.trim(oldPasswd);
				newPasswd1 = $.trim(newPasswd1);
				newPasswd2 = $.trim(newPasswd2);
				
				if(oldPasswd.length < 1) {
					alert('기존의 비밀번호를 입력해주세요');
					return false;
				}
				
				if(newPasswd1 != newPasswd2) {
					alert('새로 입력된 비밀번호들이 서로 다릅니다');
					return false;
				}
				
				if(newPasswd1 < 4 || newPasswd1 < 20) {
					alert('비밀번호는 4글자 이상 20글자 이하여야 합니다');
					return false;
				}
				
				$('form').get(0).submit();
			});
		});
	</script>
</head>
<body>
	<c:set var="userName" value="<%=userName%>" />
	<%@ include file="../components/header.jsp" %>
	
	<div id="allcontents">
		<div id="main_contents">
			<h2>비밀번호 변경</h2>
			
			<%@ include file="../components/left_nav.jsp" %>
			
			<div id="body_content">
			<c:choose>
				<c:when test="${checkValidUser == true}">
				
				<sf:form method="post" action="${userRootUrl}/change_pass/${userName}">
				<table class="basic_table lined_table w-66">
					<tr>
						<td width="40%">기존 비밀번호</td>
						<td width="60%"><input type="password" name="oldPasswd" id="oldPasswd" /></td>
					</tr>
					
					<tr>
						<td>새 비밀번호</td>
						<td><input type="password" name="newPasswd1" id="newPasswd1" /></td>
					</tr>
					
					<tr>
						<td>새 비밀번호(다시)</td>
						<td><input type="password" name="newPasswd2" id="newPasswd2" /></td>
					</tr>
										
					<tr>
						<td colspan="2" class="text-right"><input type="submit" value="변경" /></td>
					</tr>
				</table>
				</sf:form>
				</c:when>
				
				<c:otherwise>
				<script>alert('잘못된 접근입니다.'); location.assign('${DocRootUrl}');</script>
				</c:otherwise>	
			</c:choose>
			</div>	<!-- body_content end -->
		</div>	<!-- main_contents end -->
	</div>	<!-- allcontents end -->
	
	<div>
		<%@ include file="../components/footer.jsp" %>
	</div>
</body>
</html>