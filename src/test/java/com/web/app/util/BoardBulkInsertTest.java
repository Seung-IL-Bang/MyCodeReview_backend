package com.web.app.util;

import com.web.app.domain.board.Board;
import com.web.app.fixture.BoardFixtureFactory;
import com.web.app.repository.bulk.BoardPostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.stream.LongStream;

@ActiveProfiles("local")
@SpringBootTest
public class BoardBulkInsertTest {

    @Autowired
    private BoardPostRepository boardPostRepository;

    @DisplayName("임의의 게시글 벌크 인서트 쿼리를 요청할 수 있다.")
    @Test
    void bulkInsert() {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<Board> boards = LongStream.range(0, 150000)
                .parallel()
                .mapToObj(BoardFixtureFactory::create)
                .toList();

        stopWatch.stop();
        System.out.println(">>> 임의 객체 생성 시간: " + stopWatch.getTotalTimeSeconds());

        StopWatch queryStopWatch = new StopWatch();
        queryStopWatch.start();

        boardPostRepository.bulkInsert(boards);

        queryStopWatch.stop();
        System.out.println(">>> 벌크 인서트 쿼리 시간: " + queryStopWatch.getTotalTimeSeconds());
    }
}
