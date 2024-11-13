package com.hanul.mypet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.mail.Session;
import lombok.extern.log4j.Log4j2;

// 환경 설정을 담당하는 어노테이션
@Configuration
// 어플 내에서 모든 것들이 세큐리티의 통제를 받음
@EnableWebSecurity
@Log4j2
public class SecurityConfig {
	
	// 스프링이 관리 한다는 걸 지정하는 어노테이션
//	@Bean
//	SecurityFilterChain filter(HttpSecurity http) throws Exception {
//		// 모든 설정에 세큐리티를 해제
//		http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
//																.requestMatchers(new AntPathRequestMatcher("/**"))
//																.permitAll());
//		
//		return http.build();
//	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
//	@Bean
//	UserDetailsService userDetailsService() {
//		UserDetails user = User.builder()
//							.username("user1")
//							.password(passwordEncoder().encode("1111"))
//							.roles("USER")
//							.build();
//		
//		log.info("user ==> {}", user);
//		return new InMemoryUserDetailsManager(user);
//	}
	
	@Bean
	SecurityFilterChain filter(HttpSecurity http) throws Exception {
		
		http.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
		// 모든 설정에 세큐리티를 해제
		http.authorizeHttpRequests((requests)
				-> requests.requestMatchers("/member/**", "/api/mail/**", "/auth/**", "/main", "/post/**", "/animals/**", "/shelter/**").permitAll()
							.requestMatchers("/css/**", "/jquery/**", "/js/**", "/upload/**", "/display/**").permitAll()
							// 실제 로그인을 하기 위해 나타내주는 post방식의 로그인 페이지
							.requestMatchers("/member/signin", "/member/signup", "/member/main", "member/agree", "/terms").permitAll()
							// 위에 지정 된 경로 제외한 모든 경로에 인증이 필요하다는 걸 명시
//							.requestMatchers("/signin/google", "/signin/naver").permitAll()
//							.requestMatchers("/member/**").hasRole("MEMBER")
//							.requestMatchers("/movie/read").hasRole("ADMIN")
							// 위 페이지에 접근할 권한을 지정
							.anyRequest().authenticated())
		
						.formLogin((form) -> form
									// get방식의 로그인 페이지
									.loginPage("/member/signin")
									.defaultSuccessUrl("/member/main", true)
									.failureUrl("/member/signin?error")
									.permitAll())
						
							.csrf(csrf -> csrf.disable())
							// csrf를 쓸 때 허용하면 post방식, 비활성화 시켰으면 get방식도 가능하다
							.logout(logout -> logout
									.logoutUrl("/logout")
									.logoutSuccessUrl("/member/main")
									.invalidateHttpSession(true)
									.deleteCookies("JSESSIONID"));
							
		http.oauth2Login((auth) -> auth
										.defaultSuccessUrl("/member/main", true)
										.failureUrl("/member/signin?error")
										.permitAll());
		return http.build();
	}
}
