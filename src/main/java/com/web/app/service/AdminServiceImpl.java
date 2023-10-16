package com.web.app.service;


import com.web.app.domain.board.Board;
import com.web.app.domain.comment.Comment;
import com.web.app.domain.likes.Likes;
import com.web.app.domain.member.Member;
import com.web.app.domain.member.MemberRole;
import com.web.app.domain.reply.Reply;
import com.web.app.domain.review.Review;
import com.web.app.dto.ApiResponse;
import com.web.app.repository.bulk.DummyDataRepository;
import com.web.app.util.EmailRandomizer;
import com.web.app.util.SetRandomizer;
import com.web.app.util.StringRandomizer;
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

import java.time.LocalDateTime;
import java.util.*;
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
                                                   Long numberOfMembers,
                                                   Long numberOfBoards,
                                                   Long numberOfReviews,
                                                   Long numberOfComments,
                                                   Long numberOfReplies,
                                                   Long numberOfLikes) {
        if (request.getAttribute("role") == null) {
            return ApiResponse.of(HttpStatus.UNAUTHORIZED, null);
        } else if ((Objects.equals(request.getAttribute("role").toString(), "ROLE_USER"))) {
            return ApiResponse.of(HttpStatus.FORBIDDEN, null);
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<Member> memberList = LongStream.range(0, numberOfMembers)
                .parallel()
                .mapToObj(this::createMemberFixture)
                .toList();

        List<Board> boardList = LongStream.range(0, numberOfBoards)
                .parallel()
                .mapToObj(this::createBoardFixture)
                .toList();

        List<Review> reviewList = LongStream.range(0, numberOfReviews)
                .parallel()
                .mapToObj(this::createReviewFixture)
                .toList();

        List<Comment> commentList = LongStream.range(0, numberOfComments)
                .parallel()
                .mapToObj(this::createCommentFixture)
                .toList();

        List<Reply> replyList = LongStream.range(0, numberOfReplies)
                .parallel()
                .mapToObj(this::createReplyFixture)
                .toList();

        List<Likes> likeList = LongStream.range(0, numberOfLikes)
                .parallel()
                .mapToObj(this::createLikesFixture)
                .toList();

        stopWatch.stop();
        double dummyCreateTime = stopWatch.getTotalTimeSeconds();
        log.info(String.format("=================================더미 데이터 생성 시간: %f=================================", dummyCreateTime));

        StopWatch queryStopWatch = new StopWatch();
        queryStopWatch.start();

        // INSERT 순서 중요!
        dummyDataRepository.bulkInsertForMember(memberList);
        dummyDataRepository.bulkInsertForBoard(boardList, memberList);
        dummyDataRepository.bulkInsertForTagList(boardList);
        dummyDataRepository.bulkInsertForReview(boardList, reviewList);
        dummyDataRepository.bulkInsertForComment(boardList, memberList, commentList);
        dummyDataRepository.bulkInsertForReply(boardList, memberList, commentList, replyList);
        dummyDataRepository.bulkInsertForLike(boardList, memberList, likeList);

        queryStopWatch.stop();
        double bulkInsertTime = queryStopWatch.getTotalTimeSeconds();
        log.info(String.format("=================================벌크 인서트 쿼리 시간: %f=================================", bulkInsertTime));

        return ApiResponse.of(HttpStatus.OK, null,
                Map.of("message", "Dummy Data Bulk Insert Complete!!!",
                        "DummyCreateTime", String.format("더미 데이터 생성 시간: %f", dummyCreateTime),
                        "BulkInsertTime", String.format("벌크 인서트 쿼리 시간: %f", bulkInsertTime)));
    }

    private Member createMemberFixture(Long seed) {
        EasyRandomParameters param = new EasyRandomParameters().seed(seed);
        param.randomize(FieldPredicates.named("email"), new EmailRandomizer());
        param.randomize(FieldPredicates.named("name"), new StringRandomizer());
        param.randomize(FieldPredicates.named("picture"), new StringRandomizer());
        param.randomize(FieldPredicates.named("createdAt"), () -> LocalDateTime.now());
        param.randomize(FieldPredicates.named("modifiedAt"), () -> LocalDateTime.now());
        param.randomize(MemberRole.class, () -> MemberRole.USER);
        return new EasyRandom(param).nextObject(Member.class);
    }

    private Board createBoardFixture(Long seed) {
        EasyRandomParameters param = new EasyRandomParameters().seed(seed);
        param.collectionSizeRange(1, 5);
        param.randomize(FieldPredicates.named("title"), new StringRandomizer());
        param.randomize(FieldPredicates.named("content"), new StringRandomizer());
        param.randomize(FieldPredicates.named("writer"), new StringRandomizer());
        param.randomize(FieldPredicates.named("difficulty"), new StringRandomizer());
        param.randomize(FieldPredicates.named("email"), new EmailRandomizer());
        param.randomize(FieldPredicates.named("tagList"), new SetRandomizer());
        param.randomize(FieldPredicates.named("link"), new StringRandomizer());
        param.randomize(FieldPredicates.named("createdAt"), () -> LocalDateTime.now());
        param.randomize(FieldPredicates.named("modifiedAt"), () -> LocalDateTime.now());
        param.excludeField(FieldPredicates.named("id"));
        param.excludeField(FieldPredicates.named("likeCount"));
        param.excludeField(FieldPredicates.named("version"));
        return new EasyRandom(param).nextObject(Board.class);
    }

    private Review createReviewFixture(Long seed) {
        EasyRandomParameters param = new EasyRandomParameters().seed(seed);
        param.collectionSizeRange(1, 5);
        param.randomize(FieldPredicates.named("subTitle"), new StringRandomizer());
        param.randomize(FieldPredicates.named("content"), new StringRandomizer());
        param.randomize(FieldPredicates.named("createdAt"), () -> LocalDateTime.now());
        param.randomize(FieldPredicates.named("modifiedAt"), () -> LocalDateTime.now());
        param.excludeField(FieldPredicates.named("id"));
        param.excludeField(FieldPredicates.named("board"));
        return new EasyRandom(param).nextObject(Review.class);
    }

    private Comment createCommentFixture(Long seed) {
        EasyRandomParameters param = new EasyRandomParameters().seed(seed);
        param.randomize(FieldPredicates.named("content"), new StringRandomizer());
        param.excludeField(FieldPredicates.named("id"));
        param.excludeField(FieldPredicates.named("board"));
        param.excludeField(FieldPredicates.named("member"));
        param.randomize(FieldPredicates.named("createdAt"), () -> LocalDateTime.now());
        param.randomize(FieldPredicates.named("modifiedAt"), () -> LocalDateTime.now());
        return new EasyRandom(param).nextObject(Comment.class);
    }

    private Reply createReplyFixture(Long seed) {
        EasyRandomParameters param = new EasyRandomParameters().seed(seed);
        param.randomize(FieldPredicates.named("content"), new StringRandomizer());
        param.excludeField(FieldPredicates.named("id"));
        param.excludeField(FieldPredicates.named("comment"));
        param.excludeField(FieldPredicates.named("member"));
        param.randomize(FieldPredicates.named("createdAt"), () -> LocalDateTime.now());
        param.randomize(FieldPredicates.named("modifiedAt"), () -> LocalDateTime.now());
        return new EasyRandom(param).nextObject(Reply.class);
    }

    private Likes createLikesFixture(Long seed) {
        EasyRandomParameters param = new EasyRandomParameters().seed(seed);
        param.excludeField(FieldPredicates.named("id"));
        param.excludeField(FieldPredicates.named("board"));
        param.excludeField(FieldPredicates.named("member"));
        param.randomize(FieldPredicates.named("createdAt"), () -> LocalDateTime.now());
        param.randomize(FieldPredicates.named("modifiedAt"), () -> LocalDateTime.now());
        return new EasyRandom(param).nextObject(Likes.class);
    }
}
