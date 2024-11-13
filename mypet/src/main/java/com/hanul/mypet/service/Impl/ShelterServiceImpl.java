package com.hanul.mypet.service.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.hanul.mypet.dto.ShelterInfoDTO;
import com.hanul.mypet.entity.ShelterEntity;
import com.hanul.mypet.repository.ShelterRepository;
import com.hanul.mypet.response.OpenAPIServiceResponse;
import com.hanul.mypet.service.ShelterService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShelterServiceImpl implements ShelterService {

    private final RestTemplate restTemplate;
    private final ShelterRepository shelterRepository;

    @Value("${api.url}")
    private String apiUrl;

    @Value("${api.key}")
    private String serviceKey; // 인코딩된 키 사용

    @Override
    @Transactional
    public List<ShelterInfoDTO> getShelterList(int pageNo, int numOfRows) {
        log.info("사용 중인 API 키: {}", serviceKey);
        List<ShelterInfoDTO> totalShelters = new ArrayList<>();

        try {
            String requestUrl = String.format("%s/shelterInfo?serviceKey=%s&pageNo=%d&numOfRows=%d&_type=xml", apiUrl,
                    serviceKey, pageNo, numOfRows);

            log.info("API 요청 URL (페이지 {}): {}", pageNo, requestUrl);

            // API 호출
            String responseBody = callApi(requestUrl);
            if (responseBody == null || responseBody.isEmpty()) {
                log.warn("API로부터 받은 응답이 비어 있습니다 (페이지 {}). 종료합니다.", pageNo);
                return totalShelters; // API 호출 실패 시 빈 리스트 반환
            }

            // 응답 파싱
            List<ShelterInfoDTO> shelterList = parseApiResponse(responseBody);
            if (shelterList.isEmpty()) {
                log.info("더 이상 가져올 보호센터 정보가 없습니다 (페이지 {}).", pageNo);
                return totalShelters; // 더 이상 데이터가 없으면 빈 리스트 반환
            }

            // 보호소 목록 추가
            totalShelters.addAll(shelterList);
            log.info("현재 페이지에서 수집된 보호센터 수: {}", shelterList.size());

            // 데이터베이스에 보호센터 정보 저장
            saveSheltersToDatabase(shelterList);

        } catch (Exception e) {
            log.error("보호소 목록을 가져오는 중 오류 발생: ", e);
            throw new RuntimeException("보호소 목록을 가져오는 도중 오류가 발생했습니다.", e);
        }

        return totalShelters;
    }

    @Override
    public String callApi(String requestUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0");
        headers.set("Accept", "application/xml");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.GET, entity,
                    String.class);
            int statusCodeValue = responseEntity.getStatusCode().value();
            log.info("API 응답 상태 코드: {}", statusCodeValue);

            if (statusCodeValue == 200) {
                String responseBody = responseEntity.getBody();
                // 전체 API 응답 내용을 로그로 기록
                log.info("API 전체 응답: {}", responseBody);
                return responseBody;
            } else {
                log.warn("API 호출 실패 - 상태 코드: {}", statusCodeValue);
            }
        } catch (Exception e) {
            log.error("API 호출 중 오류 발생: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public ShelterInfoDTO getShelterByNameAndAddress(String careNm, String careAddr) {
        Optional<ShelterEntity> shelterEntityOptional = shelterRepository.findByCareNmAndCareAddr(careNm, careAddr);
        if (shelterEntityOptional.isEmpty()) {
            log.warn("DB에서 해당 이름과 주소의 보호센터를 찾을 수 없습니다: 이름 = {}, 주소 = {}", careNm, careAddr);
            return null;
        }
        ShelterEntity shelterEntity = shelterEntityOptional.get();
        log.info("DB에서 찾은 보호센터: {}", shelterEntity);

        return ShelterInfoDTO.builder()
                .careRegNo(shelterEntity.getCareRegNo())
                .careNm(shelterEntity.getCareNm())
                .careAddr(shelterEntity.getCareAddr())
                .careTel(shelterEntity.getCareTel())
                .build();
    }

    @Override
    public int getTotalShelters() {
        String requestUrl = String.format("%s/shelterInfo?serviceKey=%s&pageNo=%d&numOfRows=%d&_type=xml", apiUrl, serviceKey, 1, 1);

        try {
            String responseXml = callApi(requestUrl);
            if (responseXml != null && !responseXml.isEmpty()) {
                // XML 응답에서 totalCount 값을 추출
                XmlMapper xmlMapper = new XmlMapper();
                OpenAPIServiceResponse apiResponse = xmlMapper.readValue(responseXml, OpenAPIServiceResponse.class);
                if (apiResponse.getBody() != null) {
                    int totalCount = apiResponse.getBody().getTotalCount();
                    log.info("API로부터 가져온 총 보호센터 수: {}", totalCount);
                    return totalCount;
                } else {
                    log.warn("API 응답에서 body가 존재하지 않습니다. 응답 내용: {}", responseXml);
                }
            } else {
                log.warn("API로부터 받은 응답이 비어 있습니다.");
            }
        } catch (Exception e) {
            log.error("총 보호센터 수를 가져오는 중 오류 발생: ", e);
        }

        // 오류 발생 시 기본값 반환
        return 0;
    }

    @Override
    public List<ShelterInfoDTO> parseApiResponse(String responseBody) {
        try {
            if (responseBody != null && !responseBody.isEmpty()) {
                XmlMapper xmlMapper = new XmlMapper();
                OpenAPIServiceResponse apiResponse = xmlMapper.readValue(responseBody, OpenAPIServiceResponse.class);
                if (apiResponse.getBody() != null && apiResponse.getBody().getItems() != null) {
                    return apiResponse.getBody().getItems().getItem();
                } else {
                    log.warn("API 응답에서 body 또는 items가 존재하지 않습니다.");
                }
            } else {
                log.warn("API로부터 받은 응답이 비어 있습니다.");
            }
        } catch (Exception e) {
            log.error("XML 응답 파싱 중 오류 발생: {}", e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional
    public void saveSheltersToDatabase(List<ShelterInfoDTO> shelterList) {
        for (ShelterInfoDTO shelterDTO : shelterList) {
            if (shelterDTO.getCareNm() == null || shelterDTO.getCareNm().isEmpty() || shelterDTO.getCareAddr() == null
                    || shelterDTO.getCareAddr().isEmpty()) {
                log.warn("API 응답에서 보호소 이름(careNm) 또는 주소(careAddr)가 비어 있습니다: {}", shelterDTO);
                continue; // 식별자가 없으면 저장 불가하므로 넘어감
            }

            try {
                // 보호소 이름과 주소를 기반으로 중복 여부 확인
                Optional<ShelterEntity> existingShelterOpt = shelterRepository.findByCareNmAndCareAddr(shelterDTO.getCareNm(), shelterDTO.getCareAddr());
                if (existingShelterOpt.isPresent()) {
                    log.info("보호소 정보가 이미 DB에 존재합니다: {}", existingShelterOpt.get());
                    continue; // 중복된 경우 저장하지 않고 넘어감
                }

                ShelterEntity shelterEntity = ShelterEntity.builder()
                        .careNm(shelterDTO.getCareNm())
                        .careAddr(shelterDTO.getCareAddr())
                        .careTel(shelterDTO.getCareTel())
                        .build();
                shelterRepository.save(shelterEntity);
                log.info("DB에 저장된 보호센터: {}", shelterEntity);
            } catch (Exception e) {
                log.error("보호소 정보 저장 중 오류 발생: {}, 보호소 정보: {}", e.getMessage(), shelterDTO);
            }
        }
    }
}
