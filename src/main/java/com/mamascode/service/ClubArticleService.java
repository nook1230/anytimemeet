package com.mamascode.service;

/****************************************************
 * ClubArticleService: interface
 * 선언적 트랜잭션 적용
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mamascode.model.ClubArticle;
import com.mamascode.model.Reply;
import com.mamascode.utils.ListHelper;

@Transactional(propagation=Propagation.SUPPORTS, readOnly=true) // 기본 전파 속성:  Supports, 읽기 전용
public interface ClubArticleService {
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// constants
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// interface methods
	
	/////// Write, Update, Delete
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	int writeNewWrticle(ClubArticle article);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean deleteArticle(int articleId);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean updateArticle(ClubArticle article);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean setArticleBlindStatus(int articleId, boolean blindOn);
	
	/////// Read
	ClubArticle readArticle(int articleId, boolean addViewCount);
	int getLastInsertId();
	ListHelper<ClubArticle> getArticlesForWriter(
			String writerName, int page, int perPage);
	ListHelper<ClubArticle> getArticlesForClub(
			String clubName, int page, int perPage);
	
	/////// Reply
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean writeNewArticleReply(Reply reply);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean deleteArticleReply(int replyId);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	boolean updateArticleReply(Reply reply);
	
	List<Reply> readArticleReplies(int articleId);
	Reply readArticleReply(int replyId);
}
