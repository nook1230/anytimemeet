/****************************** TODO ******************************
 * 1. 이하 테이블들은 파티션이 필요할 수도 있다
*******************************************************************/

use anytimemeet;

DROP TABLE IF EXISTS club_articles;

CREATE TABLE club_articles (
	article_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	club_name VARCHAR(50) NOT NULL,
	writer_name VARCHAR(20) NOT NULL,
	title VARCHAR(200) NOT NULL,
	content TEXT NOT NULL,
	write_date DATETIME NOT NULL,
	blind TINYINT NOT NULL DEFAULT 0,
	
	INDEX(club_name), INDEX(writer_name),
	INDEX ix_ca_write_date (write_date),
	FOREIGN KEY(club_name) REFERENCES clubs(club_name)
) engine=innoDB default character set=utf8;