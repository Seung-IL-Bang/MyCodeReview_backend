package com.web.app.service;

import com.web.app.domain.member.Member;
import jakarta.servlet.http.HttpServletRequest;

public interface MemberService {

    public Member read(HttpServletRequest request);
}
