package com.hanul.mypet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hanul.mypet.entity.AnimalEntity;

@Repository
public interface AnimalRepository extends JpaRepository<AnimalEntity, String> {

	List<AnimalEntity> findByAdoptionStatusCd(String adoptionStatusCd);
	
	List<AnimalEntity> findByShelter_CareRegNo(String careRegNo);
	
}
