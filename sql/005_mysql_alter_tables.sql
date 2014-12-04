/****************************** TODO ******************************
 * 
*******************************************************************/

use anytimemeet;

ALTER TABLE clubs ADD club_introduction TEXT AFTER recruit; /* 2014. 10. 31 동아리 소개 추가 */
ALTER TABLE users ADD user_introduction TEXT AFTER certified; /* 2014. 10. 31 사용자 자기 소개 추가 */
ALTER TABLE club_articles ADD view_count INT NOT NULL DEFAULT 0 AFTER blind; /* 2014. 11. 3 게시글 조회수 카운트 추가 */