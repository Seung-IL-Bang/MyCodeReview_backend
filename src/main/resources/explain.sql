/* 메인 페이지 게시글 전체 조회 */
explain     select
                b1_0.id,
                b1_0.content,
                b1_0.created_at,
                b1_0.difficulty,
                b1_0.email,
                b1_0.like_count,
                b1_0.link,
                b1_0.modified_at,
                b1_0.title,
                b1_0.version,
                b1_0.writer
            from
                board b1_0
            where
                    b1_0.id > 0
            order by
                b1_0.created_at desc limit 1, 8;