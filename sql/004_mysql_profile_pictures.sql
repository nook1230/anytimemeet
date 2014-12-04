/****************************** TODO ******************************
 * 
*******************************************************************/

use anytimemeet;

DROP TABLE IF EXISTS profile_pictures;

CREATE TABLE profile_pictures (
	pic_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	user_name VARCHAR(20) UNIQUE NOT NULL,
	file_name VARCHAR(256) NOT NULL,
	
	INDEX ix_username (user_name),
	FOREIGN KEY(user_name) REFERENCES users(user_name)
) engine=innoDB default character set=utf8;

