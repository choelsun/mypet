package com.hanul.mypet.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.hanul.mypet.security.dto.MemberAuthDTO;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("loggedInUser")
    public MemberAuthDTO addLoggedInUser(@AuthenticationPrincipal MemberAuthDTO authDTO) {
        return authDTO; // 로그인한 사용자의 정보를 모델에 추가
    }
}
