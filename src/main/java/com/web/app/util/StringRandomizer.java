package com.web.app.util;

import org.jeasy.random.api.Randomizer;

import java.util.concurrent.ThreadLocalRandom;

public class StringRandomizer implements Randomizer<String> {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 8;

    @Override
    public String getRandomValue() {

        int length = MIN_LENGTH + ThreadLocalRandom.current().nextInt(MAX_LENGTH - MIN_LENGTH + 1); // 3~8 글자 사이의 랜덤한 길이
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(ThreadLocalRandom.current().nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
