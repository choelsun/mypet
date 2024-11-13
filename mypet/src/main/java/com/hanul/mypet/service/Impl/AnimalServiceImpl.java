package com.hanul.mypet.service.Impl;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanul.mypet.dto.AnimalInfoDTO;
import com.hanul.mypet.entity.AnimalEntity;
import com.hanul.mypet.repository.AnimalRepository;
import com.hanul.mypet.service.AnimalService;
import com.hanul.mypet.service.ApiService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnimalServiceImpl implements AnimalService {

    private final AnimalRepository animalRepository;
    private final ApiService apiService;

    @Transactional
    @Override
    public boolean adoptAnimal(String animalSeq, String userEmail) {
        AnimalEntity animal = animalRepository.findById(animalSeq)
                .orElseThrow(() -> new EntityNotFoundException("해당 동물을 찾을 수 없습니다."));

        if (!"입양대기".equals(animal.getAdoptionStatusCd())) {
            throw new IllegalStateException("이 동물은 입양 가능한 상태가 아닙니다.");
        }

        animal.setAdoptionStatusCd("입양됨");
        animalRepository.save(animal);
        log.info("동물 {}가 사용자 {}에게 입양되었습니다.", animalSeq, userEmail);
        return true;
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    @Override
    public void syncAnimalData() {
        List<AnimalInfoDTO> animals = apiService.getAnimalList(1, 100); // API 호출
        for (AnimalInfoDTO dto : animals) {
            AnimalEntity entity = convertToEntity(dto);
            animalRepository.save(entity); // 데이터베이스에 저장
        }
        log.info("유기동물 데이터가 성공적으로 동기화되었습니다.");
    }

    @Override
    public List<AnimalInfoDTO> getAnimalsByShelterIdFromDB(String shelterId) {
        // 특정 보호소 ID에 해당하는 동물 목록을 DB에서 조회
        List<AnimalEntity> animalEntities = animalRepository.findByShelter_CareRegNo(shelterId);
        return animalEntities.stream()
                             .map(this::convertToDTO)
                             .toList();
    }

    @Override
    public List<AnimalInfoDTO> getAnimalListFromDB(int pageNo, int pageSize) {
        // 페이지네이션 처리를 통해 동물 목록을 DB에서 조회
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<AnimalEntity> animalPage = animalRepository.findAll(pageable);
        return animalPage.getContent().stream()
                         .map(this::convertToDTO)
                         .toList();
    }

    @Override
    public AnimalInfoDTO getAnimalDetailFromDB(String animalSeq) {
        // 특정 동물의 세부 정보를 DB에서 조회
        AnimalEntity animalEntity = animalRepository.findById(animalSeq)
                .orElseThrow(() -> new EntityNotFoundException("해당 동물을 찾을 수 없습니다."));
        return convertToDTO(animalEntity);
    }

    private AnimalEntity convertToEntity(AnimalInfoDTO dto) {
        // AnimalInfoDTO를 AnimalEntity로 변환하는 로직
        return AnimalEntity.builder()
                .animalSeq(dto.getAnimalSeq())
                .classification(dto.getClassification())
                .species(dto.getSpecies())
                .gender(dto.getGender())
                .age(dto.getAge())
                .fileNm(dto.getFileNm())
                .filePath(dto.getFilePath())
                .foundPlace(dto.getFoundPlace())
                .rescueDate(dto.getRescueDate())
                .adoptionStatusCd("입양대기") // 기본 입양 상태 코드 설정
                .build();
    }

    private AnimalInfoDTO convertToDTO(AnimalEntity entity) {
        // AnimalEntity를 AnimalInfoDTO로 변환하는 로직
        return AnimalInfoDTO.builder()
                .animalSeq(entity.getAnimalSeq())
                .classification(entity.getClassification())
                .species(entity.getSpecies())
                .gender(entity.getGender())
                .age(entity.getAge())
                .fileNm(entity.getFileNm())
                .filePath(entity.getFilePath())
                .foundPlace(entity.getFoundPlace())
                .rescueDate(entity.getRescueDate())
                .memo("해당 없음") // Entity에는 없지만 DTO에 있는 필드를 기본값으로 설정
                .hitCnt("0") // 조회수를 기본값으로 설정
                .build();
    }
}
