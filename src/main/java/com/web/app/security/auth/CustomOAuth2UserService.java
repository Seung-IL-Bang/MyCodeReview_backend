package com.web.app.security.auth;

import com.web.app.domain.member.Member;
import com.web.app.domain.member.MemberRole;
import com.web.app.repository.MemberRepository;
import com.web.app.security.dto.MemberSecurityDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.info("=================================OAuth2 load User=================================");

        ClientRegistration clientRegistration = userRequest.getClientRegistration();

        String clientName = clientRegistration.getClientName();

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = getEmail(clientName, attributes);

        return generateDTO(email, clientName, attributes);
    }

    private OAuth2User generateDTO(String email, String clientName, Map<String, Object> attributes) {

        Optional<Member> result = memberRepository.findById(email);

        if (result.isEmpty()) { // DB 에 해당 이메일을 가진 사용자가 없다면

            String name = null;
            String picture = null;

            switch (clientName) {
                case "kakao":
                    Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
                    name = properties.get("nickname").toString();
                    picture = properties.get("profile_image").toString();
                    break;
                case "google":
                    name = attributes.get("name").toString();
                    picture = attributes.get("picture").toString();
                    break;
            }

            Member member = Member.builder()
                    .email(email)
                    .name(name)
                    .picture(picture)
                    .memberRole(MemberRole.USER)
                    .build();

            memberRepository.save(member);

            MemberSecurityDTO memberSecurityDTO = new MemberSecurityDTO(email, name, MemberRole.USER);
            memberSecurityDTO.setProps(attributes);

            return memberSecurityDTO;
        } else {
            Member member = result.get();

            return new MemberSecurityDTO(member.getEmail(), member.getName(), member.getMemberRole());
        }

    }

    private String getEmail(String clientName, Map<String, Object> attributes) {

        if ("kakao".equals(clientName)) {
            Object kakao_account = attributes.get("kakao_account");

            LinkedHashMap accountMap = (LinkedHashMap) kakao_account;

            String email = (String) accountMap.get("email");

            return email;
        } else {
            String email = attributes.get("email").toString();

            return email;
        }

    }
}
