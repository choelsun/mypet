package com.hanul.mypet.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hanul.mypet.dto.AnimalInfoDTO;
import com.hanul.mypet.dto.ShelterInfoDTO;
import com.hanul.mypet.service.AnimalService;
import com.hanul.mypet.service.ShelterService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/shelter")
public class ShelterController {

    private final ShelterService shelterService;
    private final AnimalService animalService;
    
    @GetMapping("/list")
    public String getShelterList(@RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                                 @RequestParam(name = "numOfRows", defaultValue = "10") int numOfRows,
                                 Model model) {
        log.info("페이지 번호: {}, 한 페이지 결과 수: {}", pageNo, numOfRows);

        // 보호센터 리스트 가져오기
        List<ShelterInfoDTO> shelterList = shelterService.getShelterList(pageNo, numOfRows);
        log.info("가져온 보호센터 리스트 크기: {}", shelterList.size());

        // 보호센터 정보 로그 추가
        for (ShelterInfoDTO shelter : shelterList) {
            log.info("Shelter Name: {}, CareRegNo: {}", shelter.getCareNm(), shelter.getCareRegNo());
        }

        // 총 보호센터 수 가져오기
        int totalShelters = shelterService.getTotalShelters();
        int totalPages = (int) Math.ceil((double) totalShelters / numOfRows);
        log.info("총 보호센터 수: {}, 총 페이지 수: {}", totalShelters, totalPages);

        // 페이지 계산 변수 설정
        int currentStartPage = Math.max(((pageNo - 1) / 10) * 10 + 1, 1);
        int currentEndPage = Math.min(currentStartPage + 9, totalPages);
        log.info("현재 시작 페이지: {}, 현재 종료 페이지: {}", currentStartPage, currentEndPage);

        // Thymeleaf에서 사용할 모델 설정
        model.addAttribute("shelterList", shelterList);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentStartPage", currentStartPage);
        model.addAttribute("currentEndPage", currentEndPage);
        model.addAttribute("numOfRows", numOfRows);

        return "shelter/list";
    }

    @GetMapping("/detail")
    public String getShelterDetail(@RequestParam("careNm") String careNm,
                                   @RequestParam("careAddr") String careAddr,
                                   Model model) {
        log.info("보호센터 정보 가져오기: 이름 = {}, 주소 = {}", careNm, careAddr);

        if (careNm == null || careNm.trim().isEmpty() || careAddr == null || careAddr.trim().isEmpty()) {
            log.warn("careNm 또는 careAddr 값이 null이거나 비어 있습니다.");
            return "redirect:/shelter/list"; // 목록 페이지로 리다이렉트하거나, 에러 페이지로 이동
        }

        ShelterInfoDTO shelterInfo = shelterService.getShelterByNameAndAddress(careNm, careAddr);
        if (shelterInfo == null) {
            log.warn("해당 이름과 주소에 대한 보호센터 정보를 찾을 수 없습니다.");
            return "redirect:/shelter/list";
        }

        log.info("가져온 보호센터 정보: {}", shelterInfo);

        List<AnimalInfoDTO> animalList = animalService.getAnimalsByShelterIdFromDB(shelterInfo.getCareRegNo());
        log.info("가져온 유기동물 목록 크기: {}", animalList.size());

        model.addAttribute("shelterInfo", shelterInfo);
        model.addAttribute("animalList", animalList);

        return "shelter/detail";
    }
}