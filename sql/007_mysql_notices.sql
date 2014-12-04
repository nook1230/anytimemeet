/****************************** TODO ******************************
 * 
*******************************************************************/

use anytimemeet;

DROP TABLE IF EXISTS notices;

CREATE TABLE notices (
	notice_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	user_name VARCHAR(20) NOT NULL,
	notice_msg VARCHAR(256) NOT NULL,
	notice_url VARCHAR(256),
	notice_read BOOLEAN NOT NULL DEFAULT 0,
	notice_type TINYINT NOT NULL, 	/* 1: 일반, 2: 동아리 마스터 */
	extra VARCHAR(100),
	notice_date DATETIME NOT NULL,
	
	INDEX ix_notice_user_name (user_name),
	INDEX ix_notice_date (notice_date),
	FOREIGN KEY(user_name) REFERENCES users(user_name)
) engine=innoDB default character set=utf8;
