package com.web.app.repository;

import com.web.app.domain.member.Member;
import com.web.app.domain.member.MemberRole;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("Member insert")
    public void testInsert() {

        Member member = Member.builder()
                .email("test@gmail.com")
                .name("username")
                .picture("picture.jpg")
                .memberRole(MemberRole.USER)
                .build();

        Member save = memberRepository.save(member);

    }
}
