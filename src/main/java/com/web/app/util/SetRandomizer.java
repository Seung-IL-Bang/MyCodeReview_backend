package com.web.app.util;

import org.jeasy.random.api.Randomizer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class SetRandomizer implements Randomizer<Set<String>> {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 8;

    @Override
    public Set<String> getRandomValue() {
        Set<String> resultSet = new HashSet<>();
        int setSize = 1 + ThreadLocalRandom.current().nextInt(5); // 1~5 사이의 랜덤한 값

        for (int i = 0; i < setSize; i++) {
            resultSet.add(getRandomString());
        }
        return resultSet;
    }

    private String getRandomString() {
        int length = MIN_LENGTH + ThreadLocalRandom.current().nextInt(MAX_LENGTH - MIN_LENGTH + 1);
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(ThreadLocalRandom.current().nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
