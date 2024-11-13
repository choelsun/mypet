package com.hanul.mypet.service;

import com.hanul.mypet.entity.MemberEntity;
import com.hanul.mypet.helper.constants.SocialLoginType;
import com.hanul.mypet.service.Impl.GoogleOauth;
import com.hanul.mypet.service.Impl.NaverOauth;

public interface SocialOauth {

	String getOauthRedirectURL();
	String requestAccessToken(String code);
	MemberEntity getUserInfo(String accessToken);
	
	default SocialLoginType type() {
		if (this instanceof GoogleOauth) {
			return SocialLoginType.GOOGLE;
		} else if (this instanceof NaverOauth) {
			return SocialLoginType.NAVER;
		} else {
			return null;
		}
	}
}
