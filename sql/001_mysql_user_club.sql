/****************************** TODO ******************************
 * 1. club_crew, club_members 파티션 여부
 * 2. club_join_applications, club_invitations, 
 *    두 테이블은 쓰기와 지우기가 빈번히 발생할 것으로 예상
 * 
*******************************************************************/

use anytimemeet;

CREATE TABLE users (
	user_no INT UNSIGNED UNIQUE AUTO_INCREMENT,
	user_name VARCHAR(20) NOT NULL,
	passwd VARCHAR(50) NOT NULL,
	email VARCHAR(100) NOT NULL,
	date_of_join DATETIME NOT NULL,
	active BOOLEAN NOT NULL DEFAULT 0,
	nickname VARCHAR(20),
	user_real_name VARCHAR(20),	
	date_of_birth DATETIME,
	certification_key VARCHAR(30) NOT NULL,
	certified TINYINT NOT NULL DEFAULT 0,
	
	PRIMARY KEY (user_name),
	INDEX ix_user_no (user_no)
) engine=innoDB default character set=utf8;


CREATE TABLE club_categories (
	category_id INT UNSIGNED PRIMARY KEY,
	parent_cat_id INT UNSIGNED DEFAULT 0,
	category_title VARCHAR(50),
	INDEX(category_title)
) engine=innoDB default character set=utf8;


CREATE TABLE clubs (
	club_no INT UNSIGNED UNIQUE AUTO_INCREMENT,
	club_name VARCHAR(50) PRIMARY KEY NOT NULL,
	club_title VARCHAR(256) NOT NULL,
	grand_category_id INT UNSIGNED NOT NULL,
	category_id INT UNSIGNED,
	master_name VARCHAR(20) NOT NULL,
	type TINYINT NOT NULL DEFAULT 1,		/* 1: approval type, 2: closed(invitation) type */
	max_member_num INT UNSIGNED NOT NULL DEFAULT 100,
	active TINYINT NOT NULL DEFAULT 1,	/* whether the club is active now - 1: true , 0: false */
	recruit TINYINT NOT NULL DEFAULT 1, /* whether the club is recruiting now - 1: true , 0: false */
	date_of_created DATETIME NOT NULL,
	
	INDEX ix_club_no (club_no),
	INDEX ix_club_date_of_created (date_of_created),
	
	FOREIGN KEY(grand_category_id) REFERENCES club_categories(category_id),
	FOREIGN KEY(category_id) REFERENCES club_categories(category_id),
	FOREIGN KEY(master_name) REFERENCES users(user_name)
) engine=innoDB default character set=utf8;


CREATE TABLE club_members (
	join_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	club_name VARCHAR(50) NOT NULL,
	member_name VARCHAR(20) NOT NULL,
	join_date DATETIME NOT NULL,
	active TINYINT NOT NULL DEFAULT 1, /* 1: true , 0: false */
	
	INDEX(club_name), INDEX(member_name),
	INDEX ix_club_join_date (join_date),
	FOREIGN KEY(club_name) REFERENCES clubs(club_name),
	FOREIGN KEY(member_name) REFERENCES users(user_name)
) engine=innoDB default character set=utf8;



CREATE TABLE club_crew (
	crew_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	club_name VARCHAR(50) NOT NULL,
	crew_name VARCHAR(20) NOT NULL,
	appointed_date DATETIME NOT NULL,
	
	INDEX(club_name, crew_name),
	FOREIGN KEY(club_name) REFERENCES clubs(club_name),
	FOREIGN KEY(crew_name) REFERENCES users(user_name)
) engine=innoDB default character set=utf8;


CREATE TABLE club_join_applications (
	appl_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	club_name VARCHAR(50) NOT NULL,
	user_name VARCHAR(20) NOT NULL,
	comment TEXT NOT NULL,
	appl_date DATETIME NOT NULL,
	
	INDEX(club_name), INDEX(user_name),
	FOREIGN KEY(club_name) REFERENCES clubs(club_name),
	FOREIGN KEY(user_name) REFERENCES users(user_name)
) engine=innoDB default character set=utf8;


CREATE TABLE club_invitations (
	inv_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	club_name VARCHAR(50) NOT NULL,
	user_name VARCHAR(20) NOT NULL,
	comment TEXT NOT NULL,
	inv_date DATETIME NOT NULL,
	
	INDEX(club_name), INDEX(user_name),
	FOREIGN KEY(club_name) REFERENCES clubs(club_name),
	FOREIGN KEY(user_name) REFERENCES users(user_name)
) engine=innoDB default character set=utf8;