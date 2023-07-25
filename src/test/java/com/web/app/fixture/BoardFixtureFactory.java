package com.web.app.fixture;

import com.web.app.domain.board.Board;
import com.web.app.dto.BoardRequestDTO;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

public class BoardFixtureFactory {

    public static Board create() {
        EasyRandomParameters param = new EasyRandomParameters();
        param.stringLengthRange(3, 10);
        return new EasyRandom(param).nextObject(Board.class);
    }

    public static Board create(Long seed) {
        EasyRandomParameters param = new EasyRandomParameters().seed(seed);
        param.stringLengthRange(3, 10);
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
}
