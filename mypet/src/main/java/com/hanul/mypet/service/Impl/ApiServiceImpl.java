package com.hanul.mypet.service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanul.mypet.dto.AnimalInfoDTO;
import com.hanul.mypet.service.ApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiServiceImpl implements ApiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${api.url}")
    private String apiUrl;

    @Value("${api.key}")
    private String serviceKey;

    @Override
    public List<AnimalInfoDTO> getAnimalList(int pageNo, int pageSize) {
        String requestUrl = String.format("%s/animalInfo?serviceKey=%s&pageNo=%d&numOfRows=%d&_type=json",
                                          apiUrl, serviceKey, pageNo, pageSize);
        log.info("유기동물 목록 요청 URL: {}", requestUrl);

        List<AnimalInfoDTO> animalList = new ArrayList<>();

        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(requestUrl, String.class);
            int statusCodeValue = responseEntity.getStatusCode().value();

            log.info("API 응답 상태 코드: {}", statusCodeValue);

            if (statusCodeValue == 200) {
                String response = responseEntity.getBody();
                log.info("API로부터 받은 응답: {}", response);

                if (response == null || response.isEmpty()) {
                    log.warn("API로부터 받은 응답이 비어 있습니다.");
                    return animalList;
                }

                JsonNode root = objectMapper.readTree(response);
                JsonNode items = root.path("response").path("body").path("items").path("item");
                if (items.isArray()) {
                    for (JsonNode item : items) {
                        AnimalInfoDTO animalInfo = objectMapper.treeToValue(item, AnimalInfoDTO.class);
                        animalList.add(animalInfo);
                    }
                }
                log.info("가져온 유기동물 수: {}", animalList.size());
            } else {
                log.warn("API 호출 실패 - 상태 코드: {}", statusCodeValue);
            }
        } catch (Exception e) {
            log.error("API 호출 중 오류 발생: {}", e.getMessage(), e);
        }

        return animalList;
    }
}
