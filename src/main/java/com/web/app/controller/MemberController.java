package com.web.app.controller;

import com.web.app.domain.member.Member;
import com.web.app.security.dto.MemberDTO;
import com.web.app.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final ModelMapper modelMapper;

    @GetMapping("/auth/userinfo")
    public ResponseEntity getUserInfo(HttpServletRequest request) {
        Member member = memberService.read(request);

        MemberDTO memberDTO = modelMapper.map(member, MemberDTO.class);

        return new ResponseEntity<>(memberDTO, HttpStatus.OK);
    }
}
