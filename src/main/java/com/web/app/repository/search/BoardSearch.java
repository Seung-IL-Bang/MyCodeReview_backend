package com.web.app.repository.search;

import com.web.app.domain.board.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardSearch {

    Page<Board> searchAll(String[] types, String email, String keyword, String[] difficulties, String tag, Pageable pageable);

    long filteredAll(String[] types, String email, String keyword, String[] difficulties, String tag);
}
