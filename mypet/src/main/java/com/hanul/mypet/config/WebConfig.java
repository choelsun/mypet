package com.hanul.mypet.config;

import com.hanul.mypet.interceptor.LoginInterceptor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Log4j2
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("인터셉터 등록 - LoginInterceptor");

        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")  // 모든 경로에 대해 인터셉터 적용
                .excludePathPatterns(  
                    "/member/signin", "/member/signup", "/member/main", "/",
                    "/css/**", "/js/**", "/images/**"  // 정적 리소스 제외
                );
    }
}
