package com.hanul.mypet.service;

import java.util.List;
import com.hanul.mypet.dto.AnimalInfoDTO;

public interface AnimalService {
    // 특정 보호소의 동물 목록을 가져오는 메소드 (DB에서)
    List<AnimalInfoDTO> getAnimalsByShelterIdFromDB(String shelterId);

    // 특정 동물의 세부 정보를 가져오는 메소드 (DB에서)
    AnimalInfoDTO getAnimalDetailFromDB(String animalSeq);

    // 동물을 입양하는 메소드
    boolean adoptAnimal(String animalSeq, String userEmail);

    // 외부 API를 통해 동물 데이터를 동기화하는 메소드
    void syncAnimalData();

    // 페이지네이션 처리된 동물 목록 가져오기 (DB에서)
    List<AnimalInfoDTO> getAnimalListFromDB(int pageNo, int pageSize);
}
