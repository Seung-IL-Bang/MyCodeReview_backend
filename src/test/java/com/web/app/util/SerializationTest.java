package com.web.app.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class SerializationTest {
    
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
}
