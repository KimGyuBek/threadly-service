-- ACTIVE 댓글 좋아요
INSERT INTO comment_likes (comment_id, user_id, created_at) VALUES ('cmt7', 'usr2', current_timestamp);
INSERT INTO comment_likes (comment_id, user_id, created_at) VALUES ('cmt3', 'usr1', current_timestamp);
INSERT INTO comment_likes (comment_id, user_id, created_at) VALUES ('cmt5', 'usr4', current_timestamp);
INSERT INTO comment_likes (comment_id, user_id, created_at) VALUES ('cmt6', 'usr2', current_timestamp);
INSERT INTO comment_likes (comment_id, user_id, created_at) VALUES ('cmt10', 'usr5', current_timestamp);

-- BLOCKED / DELETED 댓글 좋아요
INSERT INTO comment_likes (comment_id, user_id, created_at) VALUES ('cmt2', 'usr1', current_timestamp); -- BLOCKED
INSERT INTO comment_likes (comment_id, user_id, created_at) VALUES ('cmt4', 'usr1', current_timestamp); -- BLOCKED
INSERT INTO comment_likes (comment_id, user_id, created_at) VALUES ('cmt8', 'usr2', current_timestamp); -- DELETED
INSERT INTO comment_likes (comment_id, user_id, created_at) VALUES ('cmt14', 'usr5', current_timestamp); -- DELETED
INSERT INTO comment_likes (comment_id, user_id, created_at) VALUES ('cmt16', 'usr1', current_timestamp); -- BLOCKED