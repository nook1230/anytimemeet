<%@ page import="com.mamascode.model.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<%@ page trimDirectiveWhitespaces="true" %>

<c:url var="resourceRootUrl" value="/res" />
<c:url var="DocRootUrl" value="/" />
<c:url var="clubRootUrl" value="/club" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>동아리 폐쇄</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	<style>
		#body_content { margin-top: 150px; width: 700px; height: 250px; border: 1px solid gray; }
	</style>
	
	<script>
		
	</script>
</head>
<body>
	<%@ include file="../components/header.jsp" %>
	
	<div id="allcontents">
		<div id="main_contents">
			<div id="body_content">
			<c:choose>
				<c:when test="${checkMaster == true}">
				
				<form method="post">
				<table class="basic_table w-66">
					<tr class="text-center">
						<th>동아리 폐쇄</th>
					</tr>
					
					<tr>
						<td>
							정말 이 동아리를 폐쇄하시겠습니까?<br /> 동아리를 폐쇄하면 관련된 정보가 모두 삭제됩니다.<br />
							그래도 삭제하시겠습니까?
						</td>
					</tr>
					
					<tr class="text-center">
						<td>
							<input type="submit" value="네" />
							<input type="button" value="아니요" onclick="location.assign('${DocRootUrl}');" />
						</td>
					</tr>
				</table>
				</form>
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