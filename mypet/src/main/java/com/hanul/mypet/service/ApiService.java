package com.hanul.mypet.service;

import com.hanul.mypet.dto.AnimalInfoDTO;

import java.util.List;

public interface ApiService {

    // 외부 API로부터 유기동물 목록을 가져오는 메소드
    List<AnimalInfoDTO> getAnimalList(int pageNo, int pageSize);
}
