package com.hanul.mypet.helper.constants;

import org.springframework.core.convert.converter.Converter;

public class SocialLoginTypeConverter implements Converter<String, SocialLoginType>{

	@Override
	public SocialLoginType convert(String s) {
		return SocialLoginType.valueOf(s.toUpperCase());
	}
}
