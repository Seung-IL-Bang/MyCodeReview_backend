package com.web.app.fixture;

import com.web.app.domain.board.Board;
import com.web.app.domain.member.Member;
import com.web.app.dto.BoardRequestDTO;
import com.web.app.dto.BoardResponseDTO;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;

public class BoardFixtureFactory {

    public static Board create() {
        EasyRandomParameters param = new EasyRandomParameters();
        param.stringLengthRange(3, 10);
        param.excludeField(FieldPredicates.named("id"));
        param.excludeField(FieldPredicates.named("likeCount"));
        return new EasyRandom(param).nextObject(Board.class);
    }

    public static Board create(Long seed) {
        EasyRandomParameters param = new EasyRandomParameters().seed(seed);
        param.stringLengthRange(3, 10);
        param.excludeField(FieldPredicates.named("id"));
        param.excludeField(FieldPredicates.named("likeCount"));
        return new EasyRandom(param).nextObject(Board.class);
    }

    public static BoardRequestDTO createRequestDTO() {
        EasyRandomParameters param = new EasyRandomParameters();
        param.stringLengthRange(3, 10);
        return new EasyRandom(param).nextObject(BoardRequestDTO.class);
    }

    public static BoardRequestDTO createRequestDTO(Long seed) {
        EasyRandomParameters param = new EasyRandomParameters().seed(seed);
        param.stringLengthRange(3, 10);
        return new EasyRandom(param).nextObject(BoardRequestDTO.class);
    }

    public static BoardResponseDTO createResponseDTO() {
        EasyRandomParameters param = new EasyRandomParameters();
        param.stringLengthRange(3, 10);
        return new EasyRandom(param).nextObject(BoardResponseDTO.class);
    }

    public static BoardResponseDTO createResponseDTO(Long seed) {
        EasyRandomParameters param = new EasyRandomParameters().seed(seed);
        param.stringLengthRange(3, 10);
        return new EasyRandom(param).nextObject(BoardResponseDTO.class);
    }

    public static Board createById(Long id) {
        Board board = create();
        return Board.builder()
                .id(id)
                .title(board.getTitle())
                .content(board.getContent())
                .email(board.getEmail())
                .difficulty(board.getDifficulty())
                .link(board.getLink())
                .tagList(board.getTagList())
                .writer(board.getWriter())
                .likeCount(0)
                .version(0)
                .build();
    }

    public static Board of(Member member) {
        Board board = create();
        return Board.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .email(member.getEmail())
                .difficulty(board.getDifficulty())
                .link(board.getLink())
                .tagList(board.getTagList())
                .writer(member.getName())
                .likeCount(0)
                .version(0)
                .build();
    }
}
