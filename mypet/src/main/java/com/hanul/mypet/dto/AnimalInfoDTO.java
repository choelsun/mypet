package com.hanul.mypet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnimalInfoDTO {
    private String animalSeq;       // 유기동물 고유 번호
    private String classification;  // 동물 분류 (예: 개, 고양이)
    private String species;         // 품종
    private String gender;          // 성별
    private String age;             // 나이
    private String weight;          // 몸무게
    private String foundPlace;      // 발견 장소
    private String rescueDate;      // 구조일자
    private String fileNm;          // 이미지 파일 이름
    private String filePath;        // 이미지 파일 경로
    private String memo;            // 특이사항 메모
    private String hitCnt;          // 조회수
}
