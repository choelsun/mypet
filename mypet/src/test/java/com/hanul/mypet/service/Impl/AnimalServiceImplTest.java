package com.hanul.mypet.service.Impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.hanul.mypet.entity.AnimalEntity;
import com.hanul.mypet.repository.AnimalRepository;

@SpringBootTest
public class AnimalServiceImplTest {

    @MockBean
    private AnimalRepository animalRepository;

    @Autowired
    private AnimalServiceImpl animalService;

//    @Test
//    void testAdoptAnimal_Success() {
//        // given
//        String animalSeq = "123";
//        String userEmail = "test@example.com";
//        AnimalEntity mockAnimal = new AnimalEntity();
//        mockAnimal.setAnimalSeq(animalSeq);
//        mockAnimal.setAdoptionStatusCd("입양대기");
//
//        when(animalRepository.findById(animalSeq)).thenReturn(java.util.Optional.of(mockAnimal));
//
//        // when
//        boolean result = animalService.adoptAnimal(animalSeq, userEmail);
//
//        // then
//        assertTrue(result);
//        assertEquals("입양됨", mockAnimal.getAdoptionStatusCd());
//        verify(animalRepository, times(1)).save(mockAnimal);
//
//        System.out.println("테스트 성공: 동물이 성공적으로 입양되었습니다.");
//    }


//    @Test
//    void testAdoptAnimal_AnimalNotFound() {
//        // given
//        String animalSeq = "999";
//        String userEmail = "test@example.com";
//
//        when(animalRepository.findById(animalSeq)).thenReturn(java.util.Optional.empty());
//
//        // when / then
//        assertThrows(EntityNotFoundException.class, () -> animalService.adoptAnimal(animalSeq, userEmail));
//    }

    @Test
    void testAdoptAnimal_AlreadyAdopted() {
        // given
        String animalSeq = "123";
        String userEmail = "test@example.com";
        AnimalEntity mockAnimal = new AnimalEntity();
        mockAnimal.setAnimalSeq(animalSeq);
        mockAnimal.setAdoptionStatusCd("입양됨");

        when(animalRepository.findById(animalSeq)).thenReturn(java.util.Optional.of(mockAnimal));

        // when / then
        assertThrows(IllegalStateException.class, () -> animalService.adoptAnimal(animalSeq, userEmail));
    }
}
