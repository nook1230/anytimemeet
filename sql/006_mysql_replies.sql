/****************************** TODO ******************************
 * 
*******************************************************************/

use anytimemeet;

DROP TABLE IF EXISTS meeting_replies;
DROP TABLE IF EXISTS article_replies;

CREATE TABLE meeting_replies (
	meeting_reply_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	meeting_id INT UNSIGNED NOT NULL,
	writer_name VARCHAR(20) NOT NULL,
	content TEXT NOT NULL,
	write_date DATETIME NOT NULL,
	blind BOOLEAN NOT NULL DEFAULT 0,
	
	INDEX(meeting_id), INDEX(writer_name),
	INDEX ix_mr_write_date (write_date),
	FOREIGN KEY(meeting_id) REFERENCES meetings(meeting_id),
	FOREIGN KEY(writer_name) REFERENCES users(user_name)
) engine=innoDB default character set=utf8;


CREATE TABLE article_replies (
	article_reply_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	article_id INT UNSIGNED NOT NULL,
	writer_name VARCHAR(20) NOT NULL,
	content TEXT NOT NULL,
	write_date DATETIME NOT NULL,
	blind BOOLEAN NOT NULL DEFAULT 0,
	
	INDEX(article_id), INDEX(writer_name),
	INDEX ix_ar_write_date (write_date),
	FOREIGN KEY(article_id) REFERENCES club_articles(article_id),
	FOREIGN KEY(writer_name) REFERENCES users(user_name)
) engine=innoDB default character set=utf8;