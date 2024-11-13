package com.hanul.mypet.helper.constants;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
public class CustomRestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 연결 타임아웃 5초
        factory.setReadTimeout(5000);    // 읽기 타임아웃 5초
        return new RestTemplate(factory);
    }

	public String callApi(String url, RestTemplate restTemplate) {
	    HttpHeaders headers = new HttpHeaders();
	    headers.set("User-Agent", "Mozilla/5.0");
	    headers.set("Accept", "application/xml"); // Accept 헤더 설정

	    log.info("요청 URL: {}", url);
	    log.info("요청 헤더: {}", headers);

	    HttpEntity<String> entity = new HttpEntity<>(headers);

	    try {
	        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

	        log.info("API 응답 상태 코드: {}", response.getStatusCode());
	        log.info("API 응답 바디: {}", response.getBody());

	        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
	            XmlMapper xmlMapper = new XmlMapper();
	            // XML 데이터를 Java 객체로 매핑하거나 필요한 데이터를 추출
	            JsonNode jsonNode = xmlMapper.readTree(response.getBody());
	            log.info("파싱된 응답: {}", jsonNode.toPrettyString());
	        }

	        return response.getBody();
	    } catch (Exception e) {
	        log.error("API 호출 중 오류 발생: {}", e.getMessage(), e);
	        return null;
	    }
	}


}
