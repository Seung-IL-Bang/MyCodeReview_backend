package com.web.app.service;


import com.web.app.domain.member.Member;
import com.web.app.repository.MemberRepository;
import com.web.app.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final JWTUtil jwtUtil;

    private final MemberRepository memberRepository;

    @Override
    public Member read(HttpServletRequest request) {

        String authorization = request.getHeader("Authorization");

        String accessToken = authorization.substring(7); // slicing: accessToken

        Map<String, Object> claims = jwtUtil.validateToken(accessToken);

        String email = claims.get("email").toString();

        Optional<Member> result = memberRepository.findById(email);

        return result.orElseThrow();
    }
}
