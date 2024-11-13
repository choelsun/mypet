package com.hanul.mypet.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "animal")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalEntity {

    @Id
    private String animalSeq;  // 동물 고유 번호

    private String adoptionStatusCd;  // 입양 상태 코드

    private String classification;    // 유기동물 구분 (개, 고양이 등)

    private String species;           // 품종

    private String gender;            // 성별

    private String age;               // 나이

    private String fileNm;            // 사진 파일명

    private String filePath;          // 사진 경로 (이미지 URL)

    private String foundPlace;        // 발견 장소

    private String rescueDate;        // 구조일

    @ManyToOne
    @JoinColumn(name = "shelter_id")
    private ShelterEntity shelter;

}
