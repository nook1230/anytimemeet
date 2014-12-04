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
	<title>회원 탈퇴</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	<style>
		#body-content { width: 700px; min-height: 500px; border: 1px solid gray; }
		table { margin-top: 5px; margin-bottom: 25px; text-align: center; }
	</style>
	
	<script>
		$(document).ready(function() {
			$('input[type="submit"]').click(function(event) {
				event.preventDefault();
				event.stopPropagation();
				
				var passwd = $('#passwd').attr('value');
				
				passwd = $.trim(passwd);
				
				if(passwd.length < 1) {
					alert('비밀번호를 입력해주세요');
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
			<%@ include file="../components/left_nav.jsp" %>
			
			<div id="body_content">
			<c:choose>
				<c:when test="${checkValidUser == true}">
				
				<sf:form method="post">
				<table class="basic_table lined_table w-66">
					<tr>
						<th>
							회원 탈퇴
						</th>
					</tr>
					
					<tr>
						<td>
							탈퇴를 하면 계정과 관련된 모든 정보가 삭제됩니다.<br />
							정말 탈퇴하시겠습니까? 
						</td>
					</tr>
					
					<tr>
						<td>
							다시 한 번 비밀번호를 입력해주세요<br />
							<input type="password" name="passwd" id="passwd" />
						</td>
					</tr>
									
					<tr>
						<td colspan="2" class="text-right">
							<input type="submit" value="네" />
							<input type="button" value="아니요" onclick="location.assign('${DocRootUrl}');"/>
						</td>
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