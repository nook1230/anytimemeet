package com.mamascode.dao.jdbc;

/****************************************************
 * @Deprecated
 * MySqlJdbcClubArticleDao: implements ClubArticleDao(I)
 *
 * uses the Spring JDBC Template. 
 * handling: club_articles
 * 트랜잭션 처리: Service tire
 * 
 * by Hwang Inho
 ****************************************************/

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.mamascode.dao.ClubArticleDao;
import com.mamascode.exceptions.UpdateResultCountNotMatchException;
import com.mamascode.model.ClubArticle;

@Deprecated
public class MySqlJdbcClubArticleDao implements ClubArticleDao {
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// JdbcTemplate and data source
	@Autowired private JdbcTemplate jdbcTemplate;
	@Autowired private DataSource dataSource;
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// constructors(default)

	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// implemented methods
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// 데이터 변경
	
	/***** create: 새 글 작성  ******/
	@Override
	public int create(ClubArticle clubArticle) {
		StringBuilder builder = new StringBuilder();
		builder.append("INSERT INTO club_articles ")
		.append("(club_name, writer_name, title, content, write_date) ")
		.append("VALUES (?, ?, ?, ?, NOW())");
		String sql = builder.toString();
		
		int result = jdbcTemplate.update(sql, clubArticle.getClubName(), 
				clubArticle.getWriterName(), clubArticle.getTitle(), 
				clubArticle.getContent());
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException("create result is not 1");
	}
	
	/***** update: 글 제목, 내용 변경  ******/
	@Override
	public int update(ClubArticle clubArticle) {
		StringBuilder builder = new StringBuilder();
		builder.append("UPDATE club_articles SET ")
		.append("title = ?, content = ? WHERE article_id = ?");
		String sql = builder.toString();
		
		int result = jdbcTemplate.update(sql, clubArticle.getTitle(), 
				clubArticle.getContent(), clubArticle.getArticleId());
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException("update result is not 1");
	}
	
	/***** delete: 글 삭제  ******/
	@Override
	public int delete(int articleId) {
		int result = jdbcTemplate.update(
				"DELETE FROM club_articles WHERE article_id = ?", articleId);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException("delete result is not 1");
	}
	
	@Override
	public int delete(String clubName) {
		return 0;
	}

	/***** blindArticle: 해당 글 블라인드 처리  ******/
	@Override
	public int blindArticle(int articleId) {
		int result = jdbcTemplate.update(
				"UPDATE club_articles SET blind = 1 WHERE article_id = ?", 
				articleId);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException("blindArticle result is not 1");
	}
	
	/***** unblindArticle: 해당 글 블라인드 해제 처리  ******/
	@Override
	public int unblindArticle(int articleId) {
		int result = jdbcTemplate.update(
				"UPDATE club_articles SET blind = 0 WHERE article_id = ?", 
				articleId);
		
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
		return jdbcTemplate.queryForInt("SELECT COUNT(*) FROM club_articles");
	}
	
	/***** getCountForClub: 해당 동아리의 게시글 수 조회  ******/
	@Override
	public int getCountForClub(String clubName) {
		return jdbcTemplate.queryForInt(
				"SELECT COUNT(*) FROM club_articles WHERE club_name = ?", clubName);
	}
	
	/***** getCountForWriter: 해당 사용자(writer)의 게시글 수 조회  ******/
	@Override
	public int getCountForWriter(String writerName) {
		return jdbcTemplate.queryForInt(
				"SELECT COUNT(*) FROM club_articles WHERE writer_name = ?", writerName);
	}
	
	/***** get: 글 내용 보기  ******/
	@Override
	public ClubArticle get(int articleId) {
		return jdbcTemplate.query(
				"SELECT * FROM club_articles WHERE article_id = ?", 
				new Object[] {articleId}, articleResultSetExtractor);
	}
	
	/***** getClubArticles: 해당 동아리의 게시글 목록 조회 ******/
	@Override
	public List<ClubArticle> getClubArticles(String clubName, int offset, int limit) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT * FROM club_articles WHERE club_name = ? ")
		.append("ORDER BY write_date DESC LIMIT ? OFFSET ?");
		String sql = builder.toString();
		
		return jdbcTemplate.query(sql, 
				new Object[] {clubName, limit, offset}, articleRowMapper);
	}
	
	/***** getMyArticles: 해당 사용자의 게시글 목록 조회 ******/
	@Override
	public List<ClubArticle> getMyArticles(String writerName, int offset, int limit) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT * FROM club_articles WHERE writer_name = ? ")
		.append("ORDER BY write_date DESC LIMIT ? OFFSET ?");
		String sql = builder.toString();
		
		return jdbcTemplate.query(sql, 
				new Object[] {writerName, limit, offset}, articleRowMapper);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// utils
	
	// articleResultSetExtractor
	private ResultSetExtractor<ClubArticle> articleResultSetExtractor = 
			new ResultSetExtractor<ClubArticle>() {
		@Override
		public ClubArticle extractData(ResultSet rs) throws SQLException,
				DataAccessException {
			ClubArticle article = null;
			
			if(rs.next()) {
				article = makeClubArticleFromResultSet(rs);
			} else {
				article = new ClubArticle();
			}
			
			return article;
		}
	};
	
	private RowMapper<ClubArticle> articleRowMapper = new RowMapper<ClubArticle>() {
		@Override
		public ClubArticle mapRow(ResultSet rs, int rowNum) throws SQLException {
			return makeClubArticleFromResultSet(rs);
		}
	};
	
	// makeClubArticleFromResultSet: 결과 집합(ResultSet)으로부터 게시글 객체 가져오기 
	private ClubArticle makeClubArticleFromResultSet(ResultSet rs) throws SQLException {
		ClubArticle article = new ClubArticle();
		article.setArticleId(rs.getInt("article_id"));
		article.setClubName(rs.getString("club_name"));
		article.setWriterName(rs.getString("writer_name"));
		article.setTitle(rs.getString("title"));
		article.setContent(rs.getString("content"));
		article.setWriteDate(rs.getTimestamp("write_date"));
		
		boolean blind = (rs.getInt("blind") == 0) ? false : true;
		article.setBlind(blind);
		
		return article;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// for test
	
	// getMaxId: 테이블 내 가장 큰 article_id 가져오기
	@Override
	public int getMaxId() {
		return jdbcTemplate.queryForInt("SELECT MAX(article_id) FROM club_articles"); 
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// utils	
	
	// getLastInsertedId: 방금 작성된 게시물의 id 가져오기
	@Override
	public int getLastInsertId() {
		return jdbcTemplate.queryForInt(
				"SELECT LAST_INSERT_ID()");
	}

	/***** deleteAll: delete all clubs   ******/
	/*************************************************
	 * DO NOT USE this method except for TEST!
	 * 
	 * If you reference this class 
	 * by type (I)ClubArticleDao (not MySqlJdbcClubArticleDao),
	 * this method is invisible to you 
	 * and your Database may be safe 
	 * from unintended deleting data :D
	 **************************************************/
	public int deleteAll() {
		return jdbcTemplate.update("DELETE FROM club_articles");
	}

	@Override
	public int addArticleViewCount(int articleId) {
		return 0;
	}
}
