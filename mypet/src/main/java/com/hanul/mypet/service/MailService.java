package com.hanul.mypet.service;

import java.io.UnsupportedEncodingException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public interface MailService {
	// 메일 내용
	MimeMessage creatMessage(String to) throws MessagingException, UnsupportedEncodingException;
	// 랜덤 인증 코드
	String createKey();
	// 발송
	String sendSimpleMessage(String to) throws Exception;
	// 인증 코드 확인
	boolean verifyCode(String email, String verificationCode);
}
