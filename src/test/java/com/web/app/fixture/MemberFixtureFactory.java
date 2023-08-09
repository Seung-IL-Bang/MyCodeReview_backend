package com.web.app.fixture;

import com.web.app.domain.member.Member;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;

public class MemberFixtureFactory {

    public static Member create() {
        EasyRandomParameters param = new EasyRandomParameters();
        param.stringLengthRange(3, 10);
        return new EasyRandom(param).nextObject(Member.class);
    }

    public static Member create(Long seed) {
        EasyRandomParameters param = new EasyRandomParameters().seed(seed);
        param.stringLengthRange(3, 10);
        return new EasyRandom(param).nextObject(Member.class);
    }
}
