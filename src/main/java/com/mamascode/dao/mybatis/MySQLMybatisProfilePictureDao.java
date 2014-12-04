package com.mamascode.dao.mybatis;

/****************************************************
 * [MySQLMybatisProfilePictureDao] 
 * 					- ProfilePictureDao 인터페이스 구현
 * MyBatis를 이용한 Data Access Object
 * 
 * Repository: MySQL 
 * 주요 처리 테이블: profile_pictures
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
import java.util.Map;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mamascode.dao.ProfilePictureDao;
import com.mamascode.exceptions.UpdateResultCountNotMatchException;
import com.mamascode.model.ProfilePicture;

@Repository
public class MySQLMybatisProfilePictureDao implements ProfilePictureDao {
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
			"com.mamascode.mybatis.mapper.ProfilePictureMapper";
	
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
	
	/***** register: 새로운 프로필 사진 등록 ******/
	@Override
	public int register(String userName, String fileName) {
		// 파라미터 필터링
		if(userName.equals("") || fileName.equals(""))
			return 0;
		
		// 파라미터: 해시 맵
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("userName", userName);
		hashmap.put("fileName", fileName);
		
		int result = sqlSessionTemplate.insert(
				getMapperId("insertNewProfilePicture"), hashmap);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException(
					"register() result is not 1 in MySQLMybatisProfilePictureDao");
	}
	
	/***** update: 프로필 사진 정보 변경(사진 id 번호) ******/
	@Override
	public int update(int picId, String fileName) {
		// 파라미터 필터링
		if(fileName.equals(""))
			return 0;
		
		// 파라미터: 해시 맵
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("picId", picId);
		hashmap.put("fileName", fileName);
		
		int result = sqlSessionTemplate.update(
				getMapperId("updateProfilePictureByPicId"), hashmap);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException(
					"update() result is not 1 in MySQLMybatisProfilePictureDao");
	}
	
	/***** update: 프로필 사진 정보 변경(사용자 이름) ******/
	@Override
	public int update(String userName, String fileName) {
		// 파라미터 필터링
		if(fileName.equals(""))
			return 0;
		
		// 파라미터: 해시 맵
		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("userName", userName);
		hashmap.put("fileName", fileName);
		
		int result = sqlSessionTemplate.update(
				getMapperId("updateProfilePictureByUserName"), hashmap);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException(
					"update() result is not 1 in MySQLMybatisProfilePictureDao");
	}

	/***** delete: 프로필 사진 정보 삭제(사진 id 번호) ******/
	@Override
	public int delete(int picId) {
		int result = sqlSessionTemplate.delete(
				getMapperId("deleteProfilePictureByPicId"), picId);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException(
					"delete() result is not 1 in MySQLMybatisProfilePictureDao");
	}

	/***** delete: 프로필 사진 정보 삭제(사용자 이름) ******/
	@Override
	public int delete(String userName) {
		int result = sqlSessionTemplate.delete(
				getMapperId("deleteProfilePictureByUserName"), userName);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException(
					"delete() result is not 1 in MySQLMybatisProfilePictureDao");
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// 데이터 조회
	
	/***** getCount: 모든 레코드의 수 조회 ******/
	@Override
	public int getCount() {
		return sqlSessionTemplate.selectOne(getMapperId("selectCount"));
	}

	/***** doesHaveProfilePicture: 해당 사용자의 프로필 사진 정보가 등록되어 있는지 ******/
	@Override
	public boolean doesHaveProfilePicture(String userName) {
		int count = sqlSessionTemplate.selectOne(getMapperId("checkProfilePicture"), userName);
		
		if(count > 0)
			return true;
		
		return false;
	}
	
	/***** get: 프로필 사진 정보 조회(사용자 이름) ******/
	@Override
	public ProfilePicture get(String userName) {
		return sqlSessionTemplate.selectOne(
				getMapperId("selectProfilePictureByUserName"), userName);
	}
	
	/***** get: 프로필 사진 정보 조회(사진 id 번호) ******/
	@Override
	public ProfilePicture get(int picId) {
		return sqlSessionTemplate.selectOne(
				getMapperId("selectProfilePictureByPicId"), picId);
	}

}
