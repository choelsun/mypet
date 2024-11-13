package com.hanul.mypet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hanul.mypet.service.Impl.MailServiceImpl;

import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping(value = "/api/mail")
@Log4j2
public class MailController {

	@Autowired
	MailServiceImpl mailServiceImpl;
	
	@PostMapping(value = "/confirm.json")
	public String mailConfirm(@RequestParam(name = "email") String email,
							  @RequestParam(name = "domain") String domain) throws Exception {
		String fullEmail = email + "@" + domain;
		
		String code = mailServiceImpl.sendSimpleMessage(fullEmail);
		
		log.info("메일 전송 요청 이메일 : {}", fullEmail);
		log.info("사용자에게 발송 한 인증 코드 ===>==>=> {}", code);
		return code;
	}
}
