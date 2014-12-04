package com.mamascode.dao;

/****************************************************
 * ClubArticleReplyDao: interface
 * Date access object
 * 
 * Model: Reply
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import java.util.List;

import com.mamascode.model.Reply;

public interface ClubArticleReplyDao {
	///////// constants
	
	///////// Create, Update, Delete
	int create(Reply reply);
	int update(Reply reply);
	int delete(int replyId);
	int deleteReplyOfArticle(int articleId);
	int deleteRepliesOfClub(String clubName);
	
	///////// Get
	int getCount();
	Reply get(int replyId);
	
	int getCountReplies(int articleId);
	List<Reply> getReplies(int articleId);
}
