package com.hanul.mypet.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.servlet.HandlerInterceptor;
import java.security.Principal;

@Log4j2
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Principal principal = request.getUserPrincipal();
        // 로그인된 사용자 정보 가져오기
        String requestURI = request.getRequestURI(); 
        // 요청된 경로 확인

        log.info("요청 경로: {}", requestURI);

        // 로그인하지 않은 사용자가 게시글 작성 페이지에 접근할 때 처리
        if (principal == null && requestURI.startsWith("/post/create")) {
            log.warn("비로그인 사용자가 게시글 작성 페이지에 접근 시도");
            response.sendRedirect("/member/signin"); 
            return false; 
        }

        return true; 
    }
}
