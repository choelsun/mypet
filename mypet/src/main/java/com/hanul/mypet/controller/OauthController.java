package com.hanul.mypet.controller;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hanul.mypet.helper.constants.SocialLoginType;
import com.hanul.mypet.service.SocialOauth;
import com.hanul.mypet.service.Impl.OAuthService;

import jakarta.security.auth.message.callback.PrivateKeyCallback.Request;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(value = "/signin")
@Slf4j
public class OauthController {

	
	@Autowired
	private OAuthService oauthService;
	
	private final SocialOauth socialOauth;
	private final List<SocialOauth> socialOauthList;
	

	private ResponseEntity<Void> redirectToOAuth(SocialLoginType type) {
		log.info("{} 로그인 요청", type);
		
		SocialOauth socialOauth = oauthService.findSocialOauthByType(type);
		String redirectURL = socialOauth.getOauthRedirectURL();
		
		log.info("{} 리다이렉트URL : {}", type, redirectURL);
		return ResponseEntity.status(HttpStatus.FOUND)
							 .location(URI.create(redirectURL))
							 .build();
	}
	
	@GetMapping(value = "/google")
	public ResponseEntity<Void> googleLogin() {
		return redirectToOAuth(SocialLoginType.GOOGLE);
	}
	@GetMapping(value = "/naver")
	public ResponseEntity<Void> naverLogin() {
		return redirectToOAuth(SocialLoginType.NAVER);
	}
	
	@GetMapping(value = "/{socialLoginType}")
	public ResponseEntity<String> socialLoginType(@PathVariable(name = "socialLoginType") String socialLoginType,
												  HttpServletResponse response) {
		log.info("사용자로부터 SNS 로그인 요청:: {} Social Login", socialLoginType);

		try {
			SocialLoginType loginType = SocialLoginType.valueOf(socialLoginType.toUpperCase());
			log.info("지원 가능한 로그인 타입: {}", loginType);
			oauthService.request(loginType, response);
			
			return ResponseEntity.ok("로그인 요청 성공");
		} catch (IllegalArgumentException e) {
			log.info("지원하지 않는 로그인 타입: {}", socialLoginType);
			return ResponseEntity.badRequest().body("지원하지 않는 로그인 타입입니다");
		}
	}

	@GetMapping(value = "/{socialLoginType}/callback")
	public ResponseEntity<Void> callback(@PathVariable(name = "socialLoginType")
										 SocialLoginType socialLoginType,
										 @RequestParam(name = "code") String code,
										 HttpServletRequest request) {
		log.info("SNS 로그인 콜백 요청: {} | 받은 코드: {}", socialLoginType, code);
		
		boolean success = oauthService.requestAccessToken(socialLoginType, code);
		
		if (success) {
			log.info("엑세스 토큰 요청 성공?: {}", socialLoginType);
			log.info("세션 ID: {}", request.getSession().getId());
			
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			log.info("현재 로그인 한 사용자: {}", authentication.getName());
			
			return ResponseEntity.status(HttpStatus.FOUND)
								 .location(URI.create("/member/main"))
								 .build();
		} else {
			log.info("엑세스 토큰 요청 실패ㅠㅠ: {}", socialLoginType);
			return ResponseEntity.status(HttpStatus.FOUND)
								 .location(URI.create("/member/signin"))
								 .build();
		}

	}

}
