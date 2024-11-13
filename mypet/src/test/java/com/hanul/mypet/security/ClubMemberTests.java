package com.hanul.mypet.security;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hanul.mypet.entity.MemberEntity;
import com.hanul.mypet.repository.MemberRepository;

import lombok.extern.log4j.Log4j2;

@SpringBootTest
@Log4j2
class ClubMemberTests {
	
	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
//	@Test
//	void ClubMemberTest() {
//		IntStream.range(1, 100).forEach(i -> {
//			ClubMember clubMember = ClubMember.builder()
//										.email("user" + i + "@gmail.com")
//										.password(passwordEncoder.encode("1111"))
//										.name("사용자" + i + "ㅋㅋ")
//										.fromSocial(false)
//										.build();
//			
//				clubMember.addMemberRole(ClubMemberRole.USER);
//				
//			if (i > 80) {
//				clubMember.addMemberRole(ClubMemberRole.MANAGER);
//			}
//			
//			if (i > 90) {
//				clubMember.addMemberRole(ClubMemberRole.ADMIN);
//			}
//			clubMemberRepository.save(clubMember);
//		});
//	}
	
	@Test
	public void testRead() {
		Optional<MemberEntity> result = memberRepository.findByEmail("member4@gmail.com", false);
				
		MemberEntity memberEntity = result.get();
		
		log.info("testRead() : {}", memberEntity);
				
	}

}
