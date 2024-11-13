package com.hanul.mypet.common.jasypt;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import lombok.extern.log4j.Log4j2;

@EnableEncryptableProperties
// 환경설정 시 Bean으로 생성해주는 어노테이션
@Configuration
@Log4j2
public class JasyptConfig {
														// 윈도우에서 환경변수 설정 한 키 값
	private static final String JASYPT_PASSWORD_ENV_VAR = "JASYPT_ENCRYPTOR_PASSWORD";
	
	private final String password;
	
	public JasyptConfig(Environment env) {
		this.password = env.getProperty(JASYPT_PASSWORD_ENV_VAR);
		
		if (this.password == null || this.password.isEmpty()) {
			log.error("JASYPT_ENCRYPTOR_PASSWORD is not set or empty");
			throw new IllegalStateException("JASYPT_ENCRYPTOR_PASSWORD must be set");
		}
	}
	
	@Bean("jasyptStringEncryptor")
	public StringEncryptor stringEncryptor() {
		PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
		
		SimpleStringPBEConfig config = new SimpleStringPBEConfig();
		
		config.setPassword(password);
		config.setPoolSize("1");
		// 암호화 방식 알고리즘
		config.setAlgorithm("PBEWithMD5AndDES");
		// 인코딩 될 방식을 선택 (사용법은 여러가지)
		config.setStringOutputType("base64");
		config.setKeyObtentionIterations("1000");
		
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
		encryptor.setConfig(config);
		
		log.info("Jasypt StringEncryptor configured successfully");
		
		return encryptor;
	}
}
