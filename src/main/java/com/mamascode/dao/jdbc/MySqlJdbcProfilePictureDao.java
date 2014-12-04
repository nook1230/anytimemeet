package com.mamascode.dao.jdbc;

/****************************************************
 * @Deprecated
 * MySqlJdbcProfilePictureDao: implements ProfilePictureDao(I)
 *
 * uses the Spring JDBC Template. 
 * handling: profile_pictures
 * 트랜잭션 처리: Service tire
 * 
 * by Hwang Inho
 ****************************************************/

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import com.mamascode.dao.ProfilePictureDao;
import com.mamascode.exceptions.UpdateResultCountNotMatchException;
import com.mamascode.model.ProfilePicture;

@Deprecated
public class MySqlJdbcProfilePictureDao implements ProfilePictureDao {
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
	
	/***** register: 새로운 프로필 사진 등록 ******/
	@Override
	public int register(String userName, String fileName) {
		int result = jdbcTemplate.update(
				"INSERT INTO profile_pictures (user_name, file_name) VALUES (?, ?)", 
				userName, fileName);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException(
					"register result is not 1 in MySQLJdbcProfilePictureDao");
	}
	
	/***** update: 프로필 사진 정보 변경(사진 id 번호) ******/
	@Override
	public int update(int picId, String fileName) {
		int result = jdbcTemplate.update(
				"UPDATE profile_pictures SET file_name = ? WHERE pic_id = ?", 
				fileName, picId);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException(
					"update result is not 1 in MySQLJdbcProfilePictureDao");
	}
	
	/***** update: 프로필 사진 정보 변경(사용자 이름) ******/
	@Override
	public int update(String userName, String fileName) {
		int result = jdbcTemplate.update(
				"UPDATE profile_pictures SET file_name = ? WHERE user_name = ?", 
				fileName, userName);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException(
					"update result is not 1 in MySQLJdbcProfilePictureDao");
	}
	
	/***** delete: 프로필 사진 정보 삭제(사진 id 번호) ******/
	@Override
	public int delete(int picId) {
		int result = jdbcTemplate.update(
				"DELETE FROM profile_pictures WHERE pic_id = ?", picId);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException(
					"delete result is not 1 in MySQLJdbcProfilePictureDao");
	}
	
	/***** delete: 프로필 사진 정보 삭제(사용자 이름) ******/
	@Override
	public int delete(String userName) {
		int result = jdbcTemplate.update(
				"DELETE FROM profile_pictures WHERE user_name = ?", userName);
		
		if(result == 1)
			return result;
		else
			throw new UpdateResultCountNotMatchException(
					"delete result is not 1 in MySQLJdbcProfilePictureDao");
	}

	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	// 데이터 조회
	
	/***** getCount: 모든 레코드의 수 조회 ******/
	@Override
	public int getCount() {
		return jdbcTemplate.queryForInt("SELECT COUNT(*) FROM profile_pictures"); 
	}
	
	/***** doesHaveProfilePicture: 해당 사용자의 프로필 사진 정보가 등록되어 있는지 ******/
	@Override
	public boolean doesHaveProfilePicture(String userName) {
		String sql = "SELECT COUNT(pic_id) FROM profile_pictures WHERE user_name = ?";
		return (jdbcTemplate.queryForInt(sql, userName) == 1);
	}
	
	/***** get: 프로필 사진 정보 조회(사용자 이름) ******/
	@Override
	public ProfilePicture get(String userName) {
		String sql = "SELECT * FROM profile_pictures WHERE user_name = ?";
		ProfilePicture profilePicture = jdbcTemplate.query(
				sql, new Object[] {userName}, profilePictureRSE);
		
		return profilePicture;
	}
	
	/***** get: 프로필 사진 정보 조회(사진 id 번호) ******/
	@Override
	public ProfilePicture get(int picId) {
		String sql = "SELECT * FROM profile_pictures WHERE pic_id = ?";
		ProfilePicture profilePicture = jdbcTemplate.query(
				sql, new Object[] {picId}, profilePictureRSE);
		
		return profilePicture;
	}
	
	// profilePictureRSE
	private ResultSetExtractor<ProfilePicture> profilePictureRSE = 
			new ResultSetExtractor<ProfilePicture>() {
		
		@Override
		public ProfilePicture extractData(ResultSet rs) throws SQLException,
				DataAccessException {
			ProfilePicture profilePicture = null;
			
			if(rs.next()) {
				profilePicture = new ProfilePicture();
				profilePicture.setPicId(rs.getInt("pic_id"));
				profilePicture.setUserName(rs.getString("user_name"));
				profilePicture.setFileName(rs.getString("file_name"));
			}
			
			return profilePicture;
		}
	};
	
	/***** deleteAll: delete all clubs   ******/
	/*************************************************
	 * DO NOT USE this method except for TEST!
	 * 
	 * If you reference this class 
	 * by type (I)ProfilePictureDao (not MySqlJdbcProfilePictureDao),
	 * this method is invisible to you 
	 * and your Database may be safe 
	 * from unintended deleting data :D
	 **************************************************/
	public int deleteAll() {
		return jdbcTemplate.update("DELETE FROM profile_pictures");
	}
}
