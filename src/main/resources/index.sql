/*
 * My Review 조회 인덱스: GET /auth/board/v2/list
 * explain select b from Board b where b.email = :email
 */
CREATE INDEX email_idx ON Board (email);


/*
 * public search 조회 인덱스: GET /board/list?types=k&keyword=???
 * explain
    select board
    from Board board
    where board.title like ?1 escape '!' and board.id > ?2
    order by board.createdAt desc
 */
CREATE INDEX title_idx ON Board (title);
