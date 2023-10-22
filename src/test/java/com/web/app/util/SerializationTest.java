package com.web.app.util;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.app.IntegrationTestSupport;
import com.web.app.domain.board.Board;
import com.web.app.dto.BoardResponseDTO;
import com.web.app.fixture.BoardFixtureFactory;
import com.web.app.repository.BoardRepository;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SerializationTest extends IntegrationTestSupport {
    @Autowired
    BoardRepository boardRepository;

    @Autowired
    RedisTemplate redisTemplate;

    @AfterEach
    void tearDown() {
        boardRepository.deleteAllInBatch();
        Set<String> keys = redisTemplate.keys("*"); // 모든 키 가져오기
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys); // 키 삭제
        }
    }

    @DisplayName("Jackson 객체의 직렬화/역직렬화가 제대로 동작한다.")
    @Test
    void serializationTest() {

        // 직렬화 및 역직렬화를 위한 객체 생성
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        ObjectMapper objectMapper = new ObjectMapper();

        // 테스트 데이터 생성
        Set<String> originalSet = new HashSet<>();
        originalSet.add("tag1");
        originalSet.add("tag2");
        originalSet.add("tag3");

        // 직렬화
        byte[] serializedData = serializer.serialize(originalSet);

        // 역직렬화
        Set<String> deserializedSet = (Set<String>) serializer.deserialize(serializedData);

        assertThat(originalSet).isEqualTo(deserializedSet);
    }

    @DisplayName("Redis 저장소에 직렬화 시 FetchType=LAZY 인 객체로 인해 LazyInitializationException 예외가 발생한다.")
    @Test
    public void LazyInitializationExceptionTest() {
        // given
        Board savedBoard = boardRepository.save(BoardFixtureFactory.create());
        String redisKey = "board:" + savedBoard.getId();

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        ValueOperations<String, byte[]> ops = redisTemplate.opsForValue();

        // when
        Board board = boardRepository.findById(savedBoard.getId()).orElseThrow();
        ops.set(redisKey, serializer.serialize(board));


        // then
        assertThatThrownBy(() -> {
            serializer.deserialize(ops.get(redisKey)); // Board의 tagList 필드가 지연로딩 된다.
        })
                .isInstanceOf(SerializationException.class)
                .hasCauseInstanceOf(JsonMappingException.class)
                .hasRootCauseInstanceOf(LazyInitializationException.class);
    }

    @DisplayName("Redis 캐싱하는 과정에서 정상적으로 직렬화 및 역직렬화를 수행할 수 있다.")
    @Transactional(readOnly = true) // Hibernate 세션이 종료되기 전에 연관관계 필드에 접근할 수 있도록 해준다.
    @Test
    public void RedisSerializationTest() {
        // given
        Board savedBoard = boardRepository.save(BoardFixtureFactory.create());
        String redisKey = "board:" + savedBoard.getId();

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        ValueOperations<String, byte[]> ops = redisTemplate.opsForValue();

        // when
        Board board = boardRepository.findById(savedBoard.getId()).orElseThrow();
        BoardResponseDTO boardDTO = BoardResponseDTO.of(board);


        ops.set(redisKey, serializer.serialize(boardDTO));

        BoardResponseDTO deserialized = (BoardResponseDTO) serializer.deserialize(ops.get(redisKey));

        // then
        assertThat(boardDTO.getId()).isEqualTo(deserialized.getId());
        assertThat(boardDTO.getTagList()).isEqualTo(deserialized.getTagList());
    }
}
