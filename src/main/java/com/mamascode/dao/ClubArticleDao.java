package com.mamascode.dao;

/****************************************************
 * ClubArticleDao: interface
 * Date access object
 * 
 * Model: ClubArticle
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import java.util.List;

import com.mamascode.model.ClubArticle;

public interface ClubArticleDao {
	///////// Create, Update, Delete
	int create(ClubArticle clubArticle);
	int update(ClubArticle clubArticle);
	int delete(int articleId);
	int delete(String clubName);
	
	int blindArticle(int articleId);
	int unblindArticle(int articleId);
	
	///////// get a size of rows of the club_articles table
	int getCount();
	int getCountForClub(String clubName);
	int getCountForWriter(String writerName);
	
	///////// get a club article
	ClubArticle get(int articleId);
	int addArticleViewCount(int articleId);
	
	///////// get a list of club articles
	List<ClubArticle> getClubArticles(String clubName, int offset, int limit);
	List<ClubArticle> getMyArticles(String writerName, int offset, int limit);
	
	///////// for test
	int getMaxId();
	
	///////// utils
	int getLastInsertId();
}
