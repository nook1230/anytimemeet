package com.mamascode.service;

/****************************************************
 * ClubArticleServiceImpl: ClubArticleService 구현
 * 
 * Spring component(@Service)
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mamascode.model.ClubArticle;
import com.mamascode.model.Reply;
import com.mamascode.dao.ClubArticleDao;
import com.mamascode.dao.ClubArticleReplyDao;
import com.mamascode.utils.ListHelper;

@Service
public class ClubArticleServiceImpl implements ClubArticleService {
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// ClubArticleDao
	@Autowired ClubArticleDao articleDao;
	@Autowired ClubArticleReplyDao articleReplyDao;
	
	public void setArticleDao(ClubArticleDao articleDao) {
		this.articleDao = articleDao;
	}
	
	public void setArticleReplyDao(ClubArticleReplyDao articleReplyDao) {
		this.articleReplyDao = articleReplyDao;
	}
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// constructor(default)
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// implemented methods
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/***** writeNewWrticle: 새로운 동아리 게시글 쓰기 ******/
	@Override
	public int writeNewWrticle(ClubArticle article) {
		int result = articleDao.create(article);
		
		if(result == 1) {
			return articleDao.getLastInsertId();
		}
		
		return 0;
	}
	
	/***** deleteArticle: 동아리 게시글 삭제 ******/
	@Override
	public boolean deleteArticle(int articleId) {
		// 게시물에 달린 댓글 모두 삭제
		articleReplyDao.deleteReplyOfArticle(articleId);
		
		// 게시물 삭제
		int result = articleDao.delete(articleId);
		
		if(result == 1)
			return true;
		
		return false;
	}
	
	/***** updateArticle: 동아리 게시글 수정 ******/
	@Override
	public boolean updateArticle(ClubArticle article) {
		int result = articleDao.update(article);
		
		if(result == 1)
			return true;
		
		return false;
	}
	
	/***** setArticleBlindStatus: 동아리 게시글 블라인드 처리 ******/
	@Override
	public boolean setArticleBlindStatus(int articleId, boolean blindOn) {
		int result = 0;
		
		if(blindOn) {
			result = articleDao.blindArticle(articleId);
		} else {
			result = articleDao.unblindArticle(articleId);
		}
		
		if(result == 1)
			return true;
			
		return false;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// Read
	/***** readArticle: 동아리 게시물 읽기 ******/
	@Override
	public ClubArticle readArticle(int articleId, boolean addViewCount) {
		// addViewCount = true이면 조회수 증가
		if(addViewCount) {
			articleDao.addArticleViewCount(articleId);
		}

	    ClubArticle article = articleDao.get(articleId);
	    
	    if(article == null) {
	    	article = new ClubArticle();
	    }
	    
	    return article;
	}
	
	/***** getLastInsertId: 가장 최근에 작성된 게시물의 id 가져오기 ******/
	@Override
	public int getLastInsertId() {
		return articleDao.getLastInsertId();
	}

	/***** getArticlesForWriter: 동아리 게시물 리스트(작성자 이름 기준) ******/
	@Override
	public ListHelper<ClubArticle> getArticlesForWriter(
			String writerName, int page, int perPage) {
		// 전체 레코드 수 조회
		int totalCount = articleDao.getCountForWriter(writerName);
		
		// 게시물 리스트 헬퍼 생성
		ListHelper<ClubArticle> articleHelper = new ListHelper<ClubArticle>(
				totalCount, page, perPage);
		
		// 게시물 리스트 조회
		List<ClubArticle> articleList = articleDao.getMyArticles(writerName, 
				articleHelper.getOffset(),articleHelper.getObjectPerPage());
		
		if(articleList != null) {
			for(ClubArticle article: articleList) {
				article.setRepliesCount(articleReplyDao.getCountReplies(article.getArticleId()));
			}
		}
		
		// 리스트 설정
		articleHelper.setList(articleList);
		
		return articleHelper;
	}

	/***** getArticlesForClub: 동아리 게시물 리스트(동아리 이름 기준) ******/
	@Override
	public ListHelper<ClubArticle> getArticlesForClub(
			String clubName, int page, int perPage) {
		int totalCount = articleDao.getCountForClub(clubName);
		
		// 게시물 리스트 헬퍼 생성 - 1페이지 당 20개 게시물
		ListHelper<ClubArticle> articleHelper = new ListHelper<ClubArticle>(
				totalCount, page, perPage);
		
		// 게시물 리스트 조회
		List<ClubArticle> articleList = articleDao.getClubArticles(clubName, 
				articleHelper.getOffset(),articleHelper.getObjectPerPage());
		
		if(articleList != null) {
			for(ClubArticle article: articleList) {
				article.setRepliesCount(articleReplyDao.getCountReplies(article.getArticleId()));
			}
		}
		
		// 리스트 설정
		articleHelper.setList(articleList);
		
		return articleHelper;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// 게시물 댓글 관리
	
	/***** writeNewArticleReply: 새 댓글 쓰기 ******/
	@Override
	public boolean writeNewArticleReply(Reply reply) {
		int result = articleReplyDao.create(reply);
		
		if(result == 1)
			return true;
		
		return false;
	}
	
	/***** deleteArticleReply: 댓글 삭제 ******/
	@Override
	public boolean deleteArticleReply(int replyId) {
		int result = articleReplyDao.delete(replyId);
		
		if(result == 1)
			return true;
		
		return false;
	}
	
	/***** updateArticleReply: 댓글 수정 ******/
	@Override
	public boolean updateArticleReply(Reply reply) {
		int result = articleReplyDao.update(reply);
		
		if(result == 1)
			return true;
		
		return false;
	}
	
	/***** readArticleReplies: 게시물에 달린 댓글 가져오기 ******/
	@Override
	public List<Reply> readArticleReplies(int articleId) {
		List<Reply> replyList = articleReplyDao.getReplies(articleId);
		return replyList;
	}
	
	/***** readArticleReply: 댓글 보기 ******/
	@Override
	public Reply readArticleReply(int replyId) {
		return articleReplyDao.get(replyId);
	}
}
