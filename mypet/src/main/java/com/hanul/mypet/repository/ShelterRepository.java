package com.hanul.mypet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hanul.mypet.entity.ShelterEntity;

@Repository
public interface ShelterRepository extends JpaRepository<ShelterEntity, Long> {
	boolean existsByCareNmAndCareAddr(String careNm, String careAddr);
	
	  Optional<ShelterEntity> findByCareNmAndCareAddr(String careNm, String careAddr);
}
