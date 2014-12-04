<%@page import="com.mamascode.model.ClubCategory"%>
<%@page import="com.mamascode.utils.SessionUtil"%>
<%@page import="com.mamascode.model.Club"%>
<%@page import="com.mamascode.utils.ListHelper"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%
	ListHelper<Club> clubListHelperNew = null;
	
	if(request.getAttribute("clubListHelperNew") != null &&
			request.getAttribute("clubListHelperNew") instanceof ListHelper) {
		clubListHelperNew = (ListHelper) request.getAttribute("clubListHelperNew");
	}
	
	List<ClubCategory> grandClubCategory = null;
	
	if(request.getAttribute("grandClubCategory") != null &&
			request.getAttribute("grandClubCategory") instanceof List) {
		grandClubCategory = (List) request.getAttribute("grandClubCategory");
	}
%>
<c:url var="resourceRootUrl" value="/res" />
<c:url var="docRootUrl" value="/" />
<c:url var="clubRootUrl" value="/club" />
<c:url var="userRootUrl" value="/user" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Welcome</title>

	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	<script type="text/javascript" src="${resourceRootUrl}/js/notice.js"></script>
	<style>
		#area1 { height: 200px; margin: 10px; padding: 5px; border: 1px solid gray; clear: both; }
		#area2 { height: 300px; margin: 10px; padding: 5px; border: 1px solid gray; }
		#extra { width: 365px; height: 240px; margin: 10px; margin-right: 0px;padding: 5px; border: 1px solid gray; }
		#notice { width: 475px; height: 240px; margin: 10px; margin-left: 0px; padding: 5px; border: 1px solid gray; }
		#noticeContents { height: 160px; }
		
		#profile_area { width: 350px; }
		#profile_img { width: 150px; padding: 0; }
		#user_information { width: 190px; height: 200px; }
		
		#btn_make_club{ width: 360px; }
		
		#area1 ul li { float: left; width: 8em; }
	</style>
</head>

<body>
	<%@ include file="components/header.jsp" %>
	
	<div id="allcontents">
		<div id="main_contents">
			<c:set var="loginSuccess" value="<%=SessionUtil.loginSuccess%>" />
			
			<div id="extra" class="pull-left">
			
			<c:choose>
			<c:when test="${sessionScope.login != null && sessionScope.login == loginSuccess}">				
				<c:set var="userProfilePicture" value="${user.profilePicture}" />
				<c:if test="${userProfilePicture != null}">
					<c:set var="userProfilePictureSrcPath" value="${resourceRootUrl}/user_files/user_profile_pictures/${userProfilePicture.userName}_${userProfilePicture.fileName}" />
				</c:if>
				<c:if test="${userProfilePicture == null}">
					<c:set var="userProfilePictureSrcPath" value="${resourceRootUrl}/static_img/default_profile.jpg" />
				</c:if>
				
				<div id="profile_area" class="pull-left">
					<!-- 사용자 프로필 부분 -->
					<div id="profile_img" class="pull-left">	
						<img src="${userProfilePictureSrcPath}" alt="user profile image" width="150px" height="200px" />
					</div>
					
					<c:if test="${user.nickname != null &&  user.nickname != ''}">
						<c:set var="profileName" value="${user.nickname}(${user.userName})" />
					</c:if>
					<c:if test="${user.nickname == null || user.nickname == ''}">
						<c:set var="profileName" value="${user.userName}" />
					</c:if>
					
					<div id="user_information" class="pull-right">
						<p>
							<span><img src="${resourceRootUrl}/icon/id-card-icon-16.png" 
								style="vertical-align: bottom;" alt="프로필 정보 보기 버튼" title="프로필 정보 보기" /></span>
							${profileName}님
						</p>
						<hr />
						<p>
							-가입한 동아리
							<%
							ListHelper<Club> userClubListHelper = null;
							if(request.getAttribute("loginUserClubs") != null)
								userClubListHelper = (ListHelper<Club>) request.getAttribute("loginUserClubs");
						
							List<Club> userClubs = null;
							if(userClubListHelper != null)
								userClubs = userClubListHelper.getList();
							%>
							<% if(userClubs != null) { %>
							<table>
								<% for(Club club : userClubs) {
									String clubTitle = (club.getClubTitle().length() <= 20) ?
										club.getClubTitle() : 
										(club.getClubTitle().substring(0, 20) + "...");
								%>
								<c:set var="club" value="<%=club%>" />
								<tr>
									<td class="font-small">
										<a href="${clubRootUrl}/<%=club.getClubName()%>/clubMain"
											title="<%=club.getClubTitle()%>" <c:if test="${club.masterName == sessionScope.loginUserName}">style="color: teal;"</c:if>>
											<%=clubTitle%>
										</a>
										<c:if test="${club.masterName == sessionScope.loginUserName}">
										&nbsp;<span class="font-small font-bold color-teal">M</span>
										</c:if>
									</td>
								</tr>
								<% } %>
							</table>
							
							<span>
							<!-- TODO prev, next 들어갈 자리(Ajax를 이용한 페이지 전환): 차후 구현 -->
							
							</span>
							<% } else { %>
							<table>
								<tr>
									<td>가입한 동아리가 없습니다.</td>
								</tr>
							</table>
							<% } %>
						</p>
					</div>
				</div>
				
				<p class="text-center" style="clear: both; margin: 0; padding: 0; padding-top: 5px; ">
					<button id="btn_make_club" class="font-large" 
						onclick="location.assign('${clubRootUrl}/makeClub');">
						동아리 만들기
					</button>
				</p>
			</c:when>
			
			<c:otherwise>
				<div>
					<img src="${resourceRootUrl}/static_img/welcome.png" width="300px" />
					<p>
						<span class="font-small pull-right">
							<a href="${userRootUrl}/signup">아직 계정이 없으신가요?</a>
						</span>
					</p>
				</div>
			</c:otherwise>
			
			</c:choose>
			</div>	<!-- extra End(temporary) -->
			
			<div id="notice" class="pull-right">
				<h3><img src="${resourceRootUrl}/icon/clipboard-icon-16.png" />&nbsp;알림	</h3>
				<div id="noticeContents">
					<ul>
					<c:forEach var="notice" items="${noticeListHelper.list}">
						<li>
							<c:if test="${notice.noticeUrl != null && notice.noticeUrl != ''}">
							<c:set var="noticeUrl" value="${notice.noticeUrl}" />
							</c:if>
							<c:if test="${notice.noticeUrl == null || (notice.noticeUrl != null && notice.noticeUrl == '')}">
							<c:set var="noticeUrl" value="#" />
							</c:if>
							<span>
								<c:choose>
									<c:when test="${notice.noticeRead == false && notice.noticeType == 2}"><c:set var="color" value="teal" /></c:when>
									<c:when test="${notice.noticeRead == true}"><c:set var="color" value="gray" /></c:when>
									<c:otherwise><c:set var="color" value="black" /></c:otherwise>
								</c:choose>
								<a href="${docRootUrl}${noticeUrl}" style="font-size: 0.8em; color: ${color};"
									onclick="readNotice(${notice.noticeId}, '${sessionScope.loginUserName}',  '${userRootUrl}', '${docRootUrl}');">
								${notice.noticeMsg}
								</a>
								<c:if test="${notice.noticeType == 2}">
								<span style="color: ${color};" class="font-very-small font-bold">&nbsp;M</span>
								</c:if>
							</span>
							<span class="btn" style="margin-left: 15px;" 
								onclick="deleteNotice(${notice.noticeId}, '${sessionScope.loginUserName}', '${userRootUrl}', '${docRootUrl}');">x</span>
						</li>
					</c:forEach>
					</ul>
				</div>	<!-- noticeContents End -->
				
				<div class="text-right font-very-small" style="vertical-align: bottom;" >
					<c:if test="${sessionScope.login != null && sessionScope.login == loginSuccess}">
					<a href="${userRootUrl}/notice/getNoticesAll/${sessionScope.loginUserName}">[더 보기]</a>
					</c:if>
				</div>
			</div>	<!-- notice End -->
				
			<div id="area1">
				<h3><img src="${resourceRootUrl}/icon/clipboard-icon-16.png" />&nbsp;카테고리</h3>
				<div>
				<% 
				if(grandClubCategory != null && grandClubCategory.size() != 0) {
				%>
					<ul>
				<%
					int count = grandClubCategory.size();
					for(int i = 0; i < count; i++) { %>
						<li>
							<a href="${clubRootUrl}/clubListByCat/<%=grandClubCategory.get(i).getCategoryId()%>">
								<%=grandClubCategory.get(i).getCategoryTitle()%>
							</a>
						</li>
					<%
					}
				%>
					</ul>
				<% 
				} %>
				</div>
			</div>
			
			<div id="area2">
				<h3><img src="${resourceRootUrl}/icon/clipboard-icon-16.png" />&nbsp;신규 동아리</h3>
				<div class="">
				<% if(clubListHelperNew != null && clubListHelperNew.getList() != null
						&& clubListHelperNew.getList().size() != 0) { 
					List<Club> clubs = clubListHelperNew.getList();
				%>
					<table class="basic_table lined_table font-small" style="width: 98%; margin: 5px;">
						<tr>
							<th width="15%">#</th>
							<th width="25%">동아리 이름</th>
							<th width="40%">소개</th>
							<th width="20%">회원수(최대인원)</th>
						</tr>
					<% for(Club club : clubs) { %>
						<c:set var="club" value="<%=club%>" />
						<tr>
							<td class="text-center">${club.clubNo}</td>
							<td class="text-center">
								<a href="${clubRootUrl}/${club.clubName}/clubMain">
									${club.clubTitle}
								</a><c:if test="${club.type == 2}">&nbsp;<img src="${resourceRootUrl}/icon/lock-icon-16.png" title="비공개 동아리" alt="비공개 동아리 표시" width="10px" /></c:if>
							</td>
							<%
								String introduction = club.getClubIntroduction();
								if(introduction != null && introduction.length() > 50)
									introduction = (introduction.substring(0, 50) + "...");
							%>
							<c:set var="introduction" value="<%=introduction%>" />
							<td class="text-center" title="${club.clubIntroduction}">
								${introduction}
							</td>
							<td class="text-center">${club.numberOfClubMember}(${club.maxMemberNum})</td>
						</tr>
					<% } %>
					</table>
					
					<p class="font-small text-right">
						<a href="${clubRootUrl}/clubList">[더 보기]</a>
					</p>
				<% } else { %>
				새로운 동아리가 없네요 :(
				<% } %>
				</div>
			</div>
			
		</div>	<!-- main_content End -->
	</div>	<!-- allcontents end -->

	<%@ include file="components/footer.jsp" %>
</body>
</html>