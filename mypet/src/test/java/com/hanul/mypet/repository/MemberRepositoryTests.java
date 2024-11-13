package com.hanul.mypet.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hanul.mypet.entity.MemberEntity;

@SpringBootTest
class MemberRepositoryTests {
	
	@Autowired
	private MemberRepository memberRepository;

	@Test
	void insertMemberTest() {
		IntStream.rangeClosed(1, 50).forEach(i -> {
			MemberEntity memberEntity = MemberEntity.builder()
										.email("member" + i + "@gmail.com")
										.password("1111")
										.name("이름" + i)
										.area("지역" + i)
										.phone("010-1234-123" + i)
										.build();
			
			memberRepository.save(memberEntity);
		});
	}

}
