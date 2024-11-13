package com.hanul.mypet.jasypt;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import lombok.extern.log4j.Log4j2;

@SpringBootTest
// 테스트 할 때 JUnit 화면에서 설정 한 이름으로 나타내주어 구분하기 편함
@DisplayName("암복호화 테스트ㅋㅋ")
@Log4j2
class JasyptConfigTest {

	private static final String JASYPT_PASSWORD_ENV_VAR = "JASYPT_ENCRYPTOR_PASSWORD";
	private final String password;
	
	@Autowired
	public JasyptConfigTest(Environment env) {
		this.password = env.getProperty(JASYPT_PASSWORD_ENV_VAR);
		
		if (this.password == null || this.password.isEmpty()) {
			log.error("JASYPT_ENCRYPTOR_PASSWORD is not set or empty");
			throw new IllegalStateException("JASYPT_ENCRYPTOR_PASSWORD must be set");
		}
		
		log.info("JASYPT_ENCRYPTOR_PASSWORD is set and not empty");
	}
	
	@Test
	void stringEncyptor() {
		String dbUrl = "jdbc:oracle:thin:@192.168.0.15:1521:XE";
		String dbUsername = "mypet";
		String dbPassword = "0000";
		
		System.out.println("En_dbUrl : " + jasyptEncoding(dbUrl));
		System.out.println("En_dbUsername : " + jasyptEncoding(dbUsername));
		System.out.println("En_dbPassword : " + jasyptEncoding(dbPassword));
	}
	@Test
	void stringDecryptor() {
		String dbUrl = "UjrwUTNVSBFRSGuf9U1M3UgwDKlE77h8Y+ZCcSxfYwn6BmyE1GF4i59Z9acXjah+";
		String dbUsername = "lg/XUoVklqUajy9+ML2v2Q==";
		String dbPassword = "ck/kHM3MgtW5B/ytgl1DWA==";
		
		System.out.println("DE_dbUrl : " + jasyptDecoding(dbUrl));
		System.out.println("DE_dbUsername : " + jasyptDecoding(dbUsername));
		System.out.println("DE_dbPassword : " + jasyptDecoding(dbPassword));
	}
	
	private String jasyptEncoding(String value) {
		return jasyptProcessor(true, value);
	}
	private String jasyptDecoding(String value) {
		return jasyptProcessor(false, value);
	}
	private String jasyptProcessor(boolean encrypt, String value) {
		StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
		pbeEnc.setAlgorithm("PBEWithMD5AndDES");
		pbeEnc.setPassword(password);
		
		return encrypt ? pbeEnc.encrypt(value) : pbeEnc.decrypt(value);
	}

}
