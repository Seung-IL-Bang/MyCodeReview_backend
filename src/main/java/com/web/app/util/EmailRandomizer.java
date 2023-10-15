package com.web.app.util;

import org.jeasy.random.api.Randomizer;

import java.util.UUID;

public class EmailRandomizer implements Randomizer<String> {

    @Override
    public String getRandomValue() {
        String localPart = UUID.randomUUID().toString().substring(0, 8); // 첫 8글자만 사용
        String domain = "example.com"; // 예제 도메인
        return localPart + "@" + domain;
    }
}
