package com.hanul.mypet.service.Impl;

import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.hanul.mypet.service.MailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class MailServiceImpl implements MailService{

	@Autowired
	JavaMailSender mailSender;
	
	// 사용자가 이메일로 받을 인증 번호
	private String ePw;
	
	private ConcurrentHashMap<String, String> codeStorage = new ConcurrentHashMap<>();
	
	@Override
	public MimeMessage creatMessage(String to) throws MessagingException, UnsupportedEncodingException {
		log.info("메일 받을 사용자 : {}", to);
		log.info("인증 번호 : {}", ePw);
		
		MimeMessage message = mailSender.createMimeMessage();
		
		message.addRecipients(RecipientType.TO, to);
		message.setSubject("이메일 인증 코드입니다");
		
        String msgg = "";
        msgg += "<h1>안녕하세요</h1>";
        msgg += "<br>";
        msgg += "<p>아래 인증코드를 회원 가입 페이지에 입력해주세요</p>";
        msgg += "<br>";
        msgg += "<br>";
        msgg += "<div align='center' style='border:1px solid black'>";
        msgg += "<h3 style='color:blue'>회원가입 인증코드 입니다</h3>";
        msgg += "<div style='font-size:130%'>";
        msgg += "<strong>" + ePw + "</strong></div><br/>" ; // 메일에 인증번호 ePw 넣기
        msgg += "</div>";
		
        message.setText(msgg, "utf-8", "html");
        message.setFrom(new InternetAddress("cjftns91@naver.com", "From.MyPet"));;
        
        log.info("creatMessage 함수에서 생성 된 메시지 : {}", msgg);
        log.info("creatMessage 함수에서 생성 된 리턴 메시지 : {}", message);
        
		return message;
	}

	@Override
	public String createKey() {
		int leftLimit = 48;   // 0
		int rightLimit = 122; // z
		int targetStringLength = 10;
		Random random = new Random();
		
		String key = random.ints(leftLimit, rightLimit + 1)
									.filter(i -> (i <= 57 || i >= 65) && (i <=90 || i >=97))
									.limit(targetStringLength)
									.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
									.toString();
		
		log.info("생성 된 인증 코드 : {}", key);
	
		return key;
	}
	
    // 메일 발송
    // sendSimpleMessage 의 매개변수 to는 이메일 주소가 되고,
    // MimeMessage 객체 안에 내가 전송할 메일의 내용을 담는다
    // bean으로 등록해둔 javaMail 객체를 사용하여 이메일을 발송한다

	@Override
	public String sendSimpleMessage(String to) throws Exception {
		
		ePw = createKey();
		codeStorage.put(to, ePw);
		log.info("생성 된 랜덤 인증 코드 : {}", ePw);
		
		MimeMessage message = creatMessage(to);
		
		log.info("생성 된 메시지 : {}", message);
		
		try {
			mailSender.send(message);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
		return ePw;
	}
	
	@Override
	public boolean verifyCode(String email, String verificationCode) {
		return verificationCode.equals(codeStorage.get(email));
	}

}
