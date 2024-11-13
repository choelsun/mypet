package com.hanul.mypet.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "shelter")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShelterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 자동 생성되는 고유 식별자

    @Column(unique = true, nullable = true)
    private String careRegNo;    // 보호소 고유 번호 (API에서 받아오는 값, 없을 수도 있음)

    private String careNm;       // 보호소 이름

    private String careAddr;     // 보호소 주소
    @Column(nullable = true)
    private String careTel;      // 보호소 전화

    @OneToMany(mappedBy = "shelter")  // AnimalEntity에 있는 shelter 필드와 매핑
    private List<AnimalEntity> animals;  // 보호소에 있는 동물들 목록
}
