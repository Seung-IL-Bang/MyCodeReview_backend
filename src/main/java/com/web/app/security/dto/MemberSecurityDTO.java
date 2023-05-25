package com.web.app.security.dto;

import com.web.app.domain.member.MemberRole;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class MemberSecurityDTO implements OAuth2User {

    private String email;

    private String name;

    private MemberRole memberRole;

    private Map<String, Object> props; // 소셜 로그인 정보


    public MemberSecurityDTO(String email, String name, MemberRole memberRole) {

        this.email = email;
        this.name = name;
        this.memberRole = memberRole;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.getProps();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(memberRole.getKey()));
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getRole() {
        return memberRole.getKey();
    }
}
