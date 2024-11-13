package com.hanul.mypet.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.hanul.mypet.dto.AnimalInfoDTO;
import com.hanul.mypet.service.AnimalService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AnimalController {

    private final AnimalService animalService;

    @GetMapping("/animals")
    public String getAnimalList(@RequestParam(defaultValue = "1") int pageNo,
                                @RequestParam(defaultValue = "10") int pageSize,
                                Model model) {
        log.info("Fetching animal information for page: {}, pageSize: {}", pageNo, pageSize);

        List<AnimalInfoDTO> animalList = animalService.getAnimalListFromDB(pageNo, pageSize);
        log.info("Retrieved animal list size: {}", animalList.size());

        model.addAttribute("animalList", animalList);
        return "animals/list";
    }

    @GetMapping("/animals/{animalSeq}")
    public String getAnimalDetail(@PathVariable String animalSeq, Model model) {
        log.info("Fetching details for animal with ID: {}", animalSeq);

        AnimalInfoDTO animal = animalService.getAnimalDetailFromDB(animalSeq);
        log.info("Retrieved animal details: {}", animal);

        model.addAttribute("animal", animal);
        return "animals/details";
    }
}
