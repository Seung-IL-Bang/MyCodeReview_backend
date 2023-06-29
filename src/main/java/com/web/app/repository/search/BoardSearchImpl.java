package com.web.app.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.web.app.domain.board.Board;
import com.web.app.domain.board.QBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch {

    public BoardSearchImpl() {
        super(Board.class);
    }


    @Override
    public Page<Board> searchAll(String[] types, String email, String keyword, String[] difficulties, String tag, Pageable pageable) {
        QBoard board = QBoard.board;

        JPQLQuery<Board> query = from(board);


        if ((types != null && types.length > 0)) { // 검색 조건과 키워드가 있다면

            BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

            for (String type : types) {
                switch (type) {
                    case "k" :
                        booleanBuilder.or(board.title.contains(keyword)); // title like ...
                        break;
                    case "d" :
                        booleanBuilder.or(board.difficulty.in(difficulties));
                        break;
                    case "t":
                        booleanBuilder.or(board.tagList.contains(tag));
                }
            } // end for

            query.where(booleanBuilder); // )
        } // end if

        // bno > 0
        query.where(board.id.gt(0L)); // and id > 0

        query.where(board.email.eq(email)); // and email = :email

        // paging
        this.getQuerydsl().applyPagination(pageable, query);

        List<Board> list = query.fetch();

        long count = query.fetchCount();

        return new PageImpl<>(list, pageable, count);
    }
}
