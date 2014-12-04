package com.mamascode.dao.mybatis;

/****************************************************
 * [MySQLMybatisMeetingReplyDao] 
 * 					- MeetingReplyDao 인터페이스 구현
 * MyBatis를 이용한 Data Access Object
 * 
 * Repository: MySQL 
 * 주요 처리 테이블: meeting_replies
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

import java.util.List;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mamascode.dao.MeetingReplyDao;
import com.mamascode.exceptions.UpdateResultCountNotMatchException;
import com.mamascode.model.Reply;

@Repository
public class MySQLMybatisMeetingReplyDao implements MeetingReplyDao {
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
			"com.mamascode.mybatis.mapper.MeetingReplyMapper";
	
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
	/***** create: 새 댓글 작성 ******/
	@Override
	public int create(Reply reply) {
		int result = sqlSessionTemplate.insert(getMapperId("insertNewReply"), reply);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException(
					"MySQLMybatisMeetingReplyDao.create() result is not 1");
	}
	
	/***** update: 댓글 수성 ******/
	@Override
	public int update(Reply reply) {
		int result = sqlSessionTemplate.update(getMapperId("updateReply"), reply);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException(
					"MySQLMybatisMeetingReplyDao.update() result is not 1");
	}
	
	/***** delete: 댓글 삭제 ******/
	@Override
	public int delete(int replyId) {
		int result = sqlSessionTemplate.delete(getMapperId("deleteReply"), replyId);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException(
					"MySQLMybatisMeetingReplyDao.delete() result is not 1");
	}

	/***** deleteOfArticle: 게시물에 달린 댓글 모두 삭제 ******/
	@Override
	public int deleteReplyOfMeeting(int meetingId) {
		return sqlSessionTemplate.delete(getMapperId("deleteRepliesOfArticle"), meetingId);
	}
	
	/***** deleteOfArticle: 해당 동아리의 모임에 달린 모든 댓글 삭제 ******/
	@Override
	public int deleteRepliesOfClub(String clubName) {
		return sqlSessionTemplate.delete(getMapperId("deleteRepliesOfClub"), clubName);
	}

	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// 데이터 조회

	/***** getCount: 전체 레코드 수 조회 ******/
	@Override
	public int getCount() {
		return sqlSessionTemplate.selectOne(getMapperId("selectCount"));
	}
	
	/***** get: 댓글 보기 ******/
	@Override
	public Reply get(int replyId) {
		
		Reply reply = sqlSessionTemplate.selectOne(getMapperId("selectReply"), replyId);
		
		if(reply == null)
			reply = new Reply();
		
		return reply;
	}
	
	/***** getCountReplies: 게시물에 달린 댓글 수 ******/
	@Override
	public int getCountReplies(int meetingId) {
		return sqlSessionTemplate.selectOne(getMapperId("selectCountReplies"), meetingId);
	}
	
	/***** getReplies: 게시물에 달린 댓글 모두 가져오기 ******/
	@Override
	public List<Reply> getReplies(int meetingId) {
		return sqlSessionTemplate.selectList(getMapperId("selectReplies"), meetingId);
	}

}