package com.mamascode.dao.mybatis;

/****************************************************
 * [MySQLMybatisClubArticleDao] - ClubArticleDao 인터페이스 구현
 * MyBatis를 이용한 Data Access Object
 * 
 * Repository: MySQL 
 * 주요 처리 테이블: club_articles
 * 트랜잭션 처리: Service 계층 
 * 스프링 컴포넌트(@Repository)
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mamascode.dao.ClubArticleDao;
import com.mamascode.exceptions.UpdateResultCountNotMatchException;
import com.mamascode.model.ClubArticle;

@Repository
public class MySQLMybatisClubArticleDao implements ClubArticleDao {
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// SqlSessionTemplate and data source
	@Autowired private SqlSessionTemplate sqlSessionTemplate;
	@Autowired private DataSource dataSource;
	
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// constant
	private final String NAMESPACE = 
			"com.mamascode.mybatis.mapper.ClubArticleMapper";
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// constructors(default)
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// util
	
	///// getMapperId: 맵퍼의 SQL 아이디와 네임스페이스를 연결해줌 
	private String getMapperId(String mapperId) {
		return String.format("%s.%s", NAMESPACE, mapperId);
	}
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// implemented methods
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// 데이터 변경
	/***** create: 새 글 작성 ******/
	@Override
	public int create(ClubArticle clubArticle) {
		int result = sqlSessionTemplate.insert(getMapperId("insertNewClubArticle"), clubArticle);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException("create result is not 1");
	}
	
	/***** update: 글 수정 ******/
	@Override
	public int update(ClubArticle clubArticle) {
		int result = sqlSessionTemplate.update(getMapperId("updateClubArticle"), clubArticle);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException("update result is not 1");
	}
	
	/***** delete: 글 삭제 ******/
	@Override
	public int delete(int articleId) {
		int result = sqlSessionTemplate.delete(getMapperId("deleteClubArticle"), articleId);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException("delete result is not 1");
	}
	
	/***** delete: 글 삭제(해당 동아리 글 모두) ******/
	@Override
	public int delete(String clubName) {
		int count = getCountForClub(clubName);
		int result = sqlSessionTemplate.delete(
				getMapperId("deleteClubArticleByclubName"), clubName);
		
		if(result == count)
			return result;
		else
			throw new UpdateResultCountNotMatchException("delete result does not match");
	}
	
	/***** blindArticle: 해당 글 블라인드 처리  ******/
	@Override
	public int blindArticle(int articleId) {
		int result = sqlSessionTemplate.update(getMapperId("blindArticle"), articleId);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException("blindArticle result is not 1");
	}
	
	/***** unblindArticle: 해당 글 블라인드 해제 처리  ******/
	@Override
	public int unblindArticle(int articleId) {
		int result = sqlSessionTemplate.update(getMapperId("unblindArticle"), articleId);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException("unblindArticle result is not 1");
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// 데이터 조회
	
	/***** getCount: 전체 레코드 수 조회  ******/
	@Override
	public int getCount() {
		return sqlSessionTemplate.selectOne(getMapperId("selectCount"));
	}
	
	/***** getCountForClub: 해당 동아리의 게시글 수 조회  ******/
	@Override
	public int getCountForClub(String clubName) {
		return sqlSessionTemplate.selectOne(getMapperId("selectCountForClub"), clubName);
	}
	
	/***** getCountForWriter: 해당 사용자(writer)의 게시글 수 조회  ******/
	@Override
	public int getCountForWriter(String writerName) {
		return sqlSessionTemplate.selectOne(getMapperId("selectCountForWriter"), writerName);
	}
	
	/***** get: 글 내용 보기  ******/
	@Override
	public ClubArticle get(int articleId) {
		ClubArticle article = sqlSessionTemplate.selectOne(getMapperId("selectArticle"), articleId);
		
		if(article == null)
			article = new ClubArticle();
		
		return article;
	}
	
	/***** addArticleViewCount: 게시글 조회수 증가  ******/
	@Override
	public int addArticleViewCount(int articleId) {
		ClubArticle article = get(articleId);
		
		if(article != null && article.getArticleId() != 0) {
			// 기존의 조회수 가져오기
			int viewCount = sqlSessionTemplate.selectOne(getMapperId("selectArticleViewCount"), articleId);
			
			// 조회수 1 증가
			viewCount++;
			
			// 파라미터(해시 맵)
			Map<String, Object> hashmap = new HashMap<String, Object>();
			hashmap.put("viewCount", viewCount);
			hashmap.put("articleId", articleId);
			
			// 조회수 증가 쿼리 실행
			return sqlSessionTemplate.update(getMapperId("updateArticleViewCount"), hashmap);
		}
		
		return 0;
	}

	/***** getClubArticles: 해당 동아리의 게시글 목록 조회 ******/
	@Override
	public List<ClubArticle> getClubArticles(String clubName, int offset, int limit) {
		// offset, limit 설정
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		return sqlSessionTemplate.selectList(
				getMapperId("selectArticleListForClub"), clubName, rowBounds);
	}
	
	/***** getMyArticles: 해당 사용자의 게시글 목록 조회 ******/
	@Override
	public List<ClubArticle> getMyArticles(String writerName, int offset, int limit) {
		// offset, limit 설정
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		return sqlSessionTemplate.selectList(
				getMapperId("selectArticleListForWriter"), writerName, rowBounds);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// For Test
	
	// getMaxId: 테이블 내 가장 큰 article_id 가져오기
	@Override
	public int getMaxId() {
		return sqlSessionTemplate.selectOne(getMapperId("selectMaxId"));
	}
	
	// getLastInsertedId: 방금 작성된 게시물의 id 가져오기
	@Override
	public int getLastInsertId() {
		return sqlSessionTemplate.selectOne(getMapperId("selectLastInsertId"));
	}

}
