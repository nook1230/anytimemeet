package com.mamascode.dao.mybatis;

/****************************************************
 * [MySQLMybatisNoticeDao] - NoticeDao 인터페이스 구현
 * MyBatis를 이용한 Data Access Object
 * 
 * Repository: MySQL 
 * 주요 처리 테이블: notices
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

import com.mamascode.dao.NoticeDao;
import com.mamascode.model.Notice;

@Repository
public class MySQLMybatisNoticeDao implements NoticeDao {
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
			"com.mamascode.mybatis.mapper.NoticeMapper";
	
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
	
	/***** writeNotice: 새 알림 ******/
	@Override
	public int writeNotice(Notice notice) {
		return sqlSessionTemplate.insert(getMapperId("insertNotice"), notice);
	}
	
	/***** deleteNotice: 알림 삭제 ******/
	@Override
	public int deleteNotice(int noticeId) {
		return sqlSessionTemplate.delete(getMapperId("deleteNotice"), noticeId);
	}
	
	/***** deleteNotice: 사용자의 모든 알림 삭제 ******/
	@Override
	public int deleteNotice(String userName) {
		return sqlSessionTemplate.delete(getMapperId("deleteNoticeByUserName"), userName);
	}

	/***** readNotice: 알림 읽음으로 표시 ******/
	@Override
	public int readNotice(int noticeId) {
		return sqlSessionTemplate.update(getMapperId("readNotice"), noticeId);
	}

	/***** readNoticesOfUser: 사용자의 모든 알림 읽음으로 표시(아직 읽지 않음으로 표시된 것들만) ******/
	@Override
	public int readNoticesOfUser(String userName) {
		return sqlSessionTemplate.update(getMapperId("readNoticesOfUser"), userName);
	}

	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// 데이터 조회
	
	/***** getCount ******/
	@Override
	public int getCount() {
		return sqlSessionTemplate.selectOne(getMapperId("selectCount"));
	}
	
	/***** getCount ******/
	@Override
	public int getCount(String userName) {
		return sqlSessionTemplate.selectOne(getMapperId("selectCountByUserName"), userName);
	}

	/***** getCount: 사용자의 알림 개수 조회 ******/
	@Override
	public int getCount(String userName, int read) {
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("userName", userName);
		hashmap.put("read", read);
		
		return sqlSessionTemplate.selectOne(getMapperId("selectCountByUserNameFiltered"), hashmap);
	}

	/***** getNotices: 사용자의 알림 가져오기 ******/
	@Override
	public List<Notice> getNotices(String userName, int offset, int limit, int read) {
		RowBounds rowBounds = new RowBounds(offset, limit);
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("userName", userName);
		hashmap.put("read", read);
		
		return sqlSessionTemplate.selectList(
				getMapperId("selectNoticeByUserName"), hashmap, rowBounds);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// for test
	
	@Override
	public int deleteNoticeAll() {
		return sqlSessionTemplate.delete(getMapperId("deleteAll"));
	}
}
