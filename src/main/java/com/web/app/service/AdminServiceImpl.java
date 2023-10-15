package com.web.app.service;


import com.web.app.domain.board.Board;
import com.web.app.domain.comment.Comment;
import com.web.app.domain.member.Member;
import com.web.app.domain.member.MemberRole;
import com.web.app.dto.ApiResponse;
import com.web.app.repository.bulk.DummyDataRepository;
import com.web.app.util.EmailRandomizer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.LongStream;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{

    private final DummyDataRepository dummyDataRepository;


    @Override
    public ApiResponse<Object> testAdminAuthority(HttpServletRequest request) {
        if (request.getAttribute("role") == null) {
            return ApiResponse.of(HttpStatus.UNAUTHORIZED, null);
        } else if ((Objects.equals(request.getAttribute("role").toString(), "ROLE_USER"))) {
            return ApiResponse.of(HttpStatus.FORBIDDEN, null);
        }
        return ApiResponse.of(HttpStatus.OK, null);
    }

    @Override
    public ApiResponse<Object> bulkInsertDummyData(HttpServletRequest request,
                                                   Long numberOfBoards,
                                                   Long numberOfMembers,
                                                   Long numberOfComments) {
        if (request.getAttribute("role") == null) {
            return ApiResponse.of(HttpStatus.UNAUTHORIZED, null);
        } else if ((Objects.equals(request.getAttribute("role").toString(), "ROLE_USER"))) {
            return ApiResponse.of(HttpStatus.FORBIDDEN, null);
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<Board> boardList = LongStream.range(0, numberOfBoards)
                .parallel()
                .mapToObj(this::createBoardFixture)
                .toList();

        List<Comment> commentList = LongStream.range(0, numberOfComments)
                .parallel()
                .mapToObj(this::createCommentFixture)
                .toList();

        List<Member> memberList = LongStream.range(0, numberOfMembers)
                .parallel()
                .mapToObj(this::createMemberFixture)
                .toList();

        stopWatch.stop();
        double dummyCreateTime = stopWatch.getTotalTimeSeconds();
        log.info(String.format("=================================더미 데이터 생성 시간: %f=================================", dummyCreateTime));

        StopWatch queryStopWatch = new StopWatch();
        queryStopWatch.start();

        dummyDataRepository.bulkInsert(boardList);
        dummyDataRepository.bulkInsertForMember(memberList);
        dummyDataRepository.bulkInsertForTagList(boardList);
        dummyDataRepository.bulkInsertForComment(boardList, commentList, memberList);

        queryStopWatch.stop();
        double bulkInsertTime = queryStopWatch.getTotalTimeSeconds();
        log.info(String.format("=================================벌크 인서트 쿼리 시간: %f=================================", bulkInsertTime));

        return ApiResponse.of(HttpStatus.OK, null,
                Map.of("message", "Dummy Data Bulk Insert Complete!!!",
                        "DummyCreateTime", String.format("더미 데이터 생성 시간: %f", dummyCreateTime),
                        "BulkInsertTime", String.format("벌크 인서트 쿼리 시간: %f", bulkInsertTime)));
    }

    private Board createBoardFixture(Long seed) {
        EasyRandomParameters param = new EasyRandomParameters().seed(seed);
        param.stringLengthRange(3, 10);
        param.excludeField(FieldPredicates.named("id"));
        param.excludeField(FieldPredicates.named("likeCount"));
        param.randomize(FieldPredicates.named("email"), new EmailRandomizer());
        param.collectionSizeRange(1, 5);
        return new EasyRandom(param).nextObject(Board.class);
    }

    private Member createMemberFixture(Long seed) {
        EasyRandomParameters param = new EasyRandomParameters().seed(seed);
        param.stringLengthRange(3, 10);
        param.randomize(FieldPredicates.named("email"), new EmailRandomizer());
        param.randomize(MemberRole.class, () -> MemberRole.USER);
        return new EasyRandom(param).nextObject(Member.class);
    }

    private Comment createCommentFixture(Long seed) {
        EasyRandomParameters param = new EasyRandomParameters().seed(seed);
        param.stringLengthRange(3, 10);
        param.excludeField(FieldPredicates.named("board"));
        param.excludeField(FieldPredicates.named("id"));
        param.excludeField(FieldPredicates.named("member"));
        return new EasyRandom(param).nextObject(Comment.class);
    }


}
