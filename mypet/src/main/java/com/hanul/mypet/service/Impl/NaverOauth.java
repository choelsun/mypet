package com.hanul.mypet.service.Impl;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.hanul.mypet.entity.MemberEntity;
import com.hanul.mypet.service.SocialOauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class NaverOauth implements SocialOauth {
	
	private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String NAVER_SNS_CLIENT_ID;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String NAVER_SNS_CLIENT_SECRET;
    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String NAVER_SNS_CALLBACK_URL;
    @Value("${spring.security.oauth2.client.provider.naver.authorization-uri}")
    private String NAVER_SNS_BASE_URL;
    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    private String NAVER_SNS_TOKEN_BASE_URL;
	@Override
	public String getOauthRedirectURL() {
		Map<String, Object> params = new HashMap<>();
		params.put("response_type", "code");
		params.put("client_id", NAVER_SNS_CLIENT_ID);
		params.put("redirect_uri", NAVER_SNS_CALLBACK_URL);
		params.put("state", "random_state_value"); // CSRF 방지를 위한 state 파라미터 추가

		String parameterString = params.entrySet().stream().map(x -> x.getKey() + "=" + x.getValue())
				.collect(Collectors.joining("&"));

		String redirectURL = NAVER_SNS_BASE_URL + "?" + parameterString;
		log.info("네이버 로그인 URL: {}", redirectURL);
		return redirectURL;
	}

	@Override
	public String requestAccessToken(String code) {
		RestTemplate restTemplate = new RestTemplate();

		Map<String, Object> params = new HashMap<>();
		params.put("code", code);
		params.put("client_id", NAVER_SNS_CLIENT_ID);
		params.put("client_secret", NAVER_SNS_CLIENT_SECRET);
		params.put("redirect_uri", NAVER_SNS_CALLBACK_URL);
		params.put("grant_type", "authorization_code");
		params.put("state", "random_state_value");

		ResponseEntity<String> responseEntity = restTemplate.postForEntity(NAVER_SNS_TOKEN_BASE_URL, params,
				String.class);
		
		log.info("네이버 응답 상태코드: {}", responseEntity.getStatusCode());
		log.info("네이버 응답 본문: {}", responseEntity.getBody());

		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			return responseEntity.getBody();
		}
		return "네이버 로그인 요청 처리 실패";
	}

	@Override
	public MemberEntity getUserInfo(String accessToken) {
		// TODO Auto-generated method stub
		return null;
	}

}
