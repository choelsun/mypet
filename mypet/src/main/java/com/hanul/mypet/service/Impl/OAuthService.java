package com.hanul.mypet.service.Impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.hanul.mypet.entity.MemberEntity;
import com.hanul.mypet.entity.MemberRole;
import com.hanul.mypet.helper.constants.SocialLoginType;
import com.hanul.mypet.repository.MemberRepository;
import com.hanul.mypet.service.MemberService;
import com.hanul.mypet.service.SocialOauth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class OAuthService {
    private final List<SocialOauth> socialOauthList;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    public void request(SocialLoginType socialLoginType, HttpServletResponse response) {
        SocialOauth socialOauth = findSocialOauthByType(socialLoginType);
        String redirectURL = socialOauth.getOauthRedirectURL();
        try {
            response.sendRedirect(redirectURL);
        } catch (IOException e) {
            log.error("리다이렉트 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    public boolean requestAccessToken(SocialLoginType socialLoginType, String code) {
        SocialOauth socialOauth = findSocialOauthByType(socialLoginType);
        
        try {
            String accessToken = socialOauth.requestAccessToken(code);
            log.info("액세스 토큰: {}", accessToken);
            return processUserInfo(socialOauth, accessToken);
        } catch (Exception e) {
            log.error("소셜 로그인 처리 중 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean processUserInfo(SocialOauth socialOauth, String accessToken) {
        MemberEntity memberEntity = socialOauth.getUserInfo(accessToken);

        if (memberEntity == null) {
            log.warn("회원 정보를 가져오는 데 실패했습니다: memberEntity가 null입니다.");
            return false;
        }

        log.info("회원 정보: {}", memberEntity);
        Optional<MemberEntity> optionalExistingMember = memberRepository.findByEmail(memberEntity.getEmail());

        if (optionalExistingMember.isEmpty()) {
            log.info("신규 회원 등록: {}", memberEntity.getEmail());
            memberEntity.setFromSocial(true);
            memberEntity.setPassword(null);
            memberRepository.save(memberEntity);
        } else {
            MemberEntity existingMember = optionalExistingMember.get();
            log.info("기존 회원 업데이트: {}", existingMember.getEmail());
            existingMember.setFromSocial(true);
            memberRepository.save(existingMember);
        }

        return true;
    }


    public SocialOauth findSocialOauthByType(SocialLoginType socialLoginType) {
        return socialOauthList.stream()
                .filter(x -> x.type() == socialLoginType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 SocialLoginType 입니다."));
    }
    
    public MemberEntity getMemberInfo(SocialLoginType socialLoginType, String email) {
    	
    	Optional<MemberEntity> optionalMemberEntity = memberRepository.findByEmail(email);
    	
        if (optionalMemberEntity.isPresent()) {
            return optionalMemberEntity.get();
        } else {
            // 신규 사용자일 경우 회원 등록 로직
            MemberEntity memberEntity = new MemberEntity();
            memberEntity.setEmail(email);
            memberEntity.setFromSocial(true);

            // 기본 권한
            Set<MemberRole> roles = Set.of(MemberRole.MEMBER);
            memberEntity.setRoleSet(roles);

            memberRepository.save(memberEntity);
            return memberEntity;
        }
    }
}
