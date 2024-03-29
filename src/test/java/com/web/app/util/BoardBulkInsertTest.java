package com.web.app.util;

import com.web.app.domain.board.Board;
import com.web.app.domain.member.Member;
import com.web.app.fixture.BoardFixtureFactory;
import com.web.app.fixture.MemberFixtureFactory;
import com.web.app.repository.bulk.DummyDataRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.stream.LongStream;

@Disabled
@ActiveProfiles("local")
@SpringBootTest
public class BoardBulkInsertTest {

    @Autowired
    private DummyDataRepository dummyDataRepository;

    @DisplayName("임의의 게시글 벌크 인서트 쿼리를 요청할 수 있다.")
    @Test
    void bulkInsert() {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<Board> boards = LongStream.range(0, 50000)
                .parallel()
                .mapToObj(BoardFixtureFactory::create)
                .toList();

        List<Member> members = LongStream.range(0, 500)
                .parallel()
                .mapToObj(MemberFixtureFactory::create)
                .toList();

        stopWatch.stop();
        System.out.println(">>> 임의 객체 생성 시간: " + stopWatch.getTotalTimeSeconds());

        StopWatch queryStopWatch = new StopWatch();
        queryStopWatch.start();

        dummyDataRepository.bulkInsertForBoard(boards, members);

        queryStopWatch.stop();
        System.out.println(">>> 벌크 인서트 쿼리 시간: " + queryStopWatch.getTotalTimeSeconds());
    }
}
