/****************************** TODO ******************************
 * 1. meeting 관련 테이블들은 사용량 증가에 따라 그 크기가 몹시 커질 우려가 있다. 파티션 고려
 *  
*******************************************************************/

use anytimemeet;

CREATE TABLE meetings (
	meeting_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	club_name VARCHAR(50) NOT NULL,
	title VARCHAR(100) NOT NULL,
	administrator_name VARCHAR(20) NOT NULL,
	introduction TEXT NOT NULL,
	location VARCHAR(200),
	meeting_status TINYINT NOT NULL DEFAULT 0,	/* 0: 진행 중, 1: 취소, 2: 확정 */
	reg_date DATETIME NOT NULL,
	
	INDEX ix_meeting_club_name (club_name),
	INDEX ix_meeting_reg_date (reg_date),
	FOREIGN KEY(club_name) REFERENCES clubs(club_name)
) engine=innoDB default character set=utf8;


CREATE TABLE meeting_dates (
 	date_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	meeting_id INT UNSIGNED NOT NULL,
	recommended_date DATE NOT NULL,
	recommended_time VARCHAR(10) NOT NULL,
	date_status TINYINT NOT NULL DEFAULT 0,	/* 0: 미확정, 1: 확정(1개의 날짜만 확정 가능), 2: 비확정(참가 불가) */
	
	INDEX ix_meeting_id (meeting_id),
	FOREIGN KEY(meeting_id) REFERENCES meetings(meeting_id)
) engine=innoDB default character set=utf8;


CREATE TABLE meeting_members (
 	date_id INT UNSIGNED NOT NULL,
	user_name VARCHAR(20) NOT NULL,
	reg_date DATETIME NOT NULL,
	
	INDEX(date_id), INDEX(user_name),
	FOREIGN KEY(date_id) REFERENCES meeting_dates(date_id)
) engine=innoDB default character set=utf8;
