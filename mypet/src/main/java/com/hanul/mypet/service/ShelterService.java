package com.hanul.mypet.service;

import java.util.List;

import com.hanul.mypet.dto.ShelterInfoDTO;

public interface ShelterService {
    List<ShelterInfoDTO> getShelterList(int pageNo, int numOfRows);
    // 보호소 목록 가져오기
    
    ShelterInfoDTO getShelterByNameAndAddress(String careNm, String careAddr);
    // 보호소 이름과 주소로 상세 정보 가져오기
    
    int getTotalShelters();
    // 총 보호소 수 가져오기 (페이징 계산에 필요)
    
    String callApi(String requestUrl);
    // Api 호출하는 역할
    
    List<ShelterInfoDTO> parseApiResponse(String responseBody);
    // Api 응답을 파싱하여 실제 사용 할 데이터로 변환하는 역할
    
    void saveSheltersToDatabase(List<ShelterInfoDTO> shelterList);
    // 보호소 목록을 DB에 저장하는 역할
    
}
