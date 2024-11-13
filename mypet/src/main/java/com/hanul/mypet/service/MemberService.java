package com.hanul.mypet.service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.hanul.mypet.dto.MemberDTO;
import com.hanul.mypet.entity.MemberEntity;
import com.hanul.mypet.entity.MemberRole;
import com.hanul.mypet.security.dto.MemberAuthDTO;

import jakarta.servlet.http.HttpSession;

public interface MemberService {
	
	
	
	String signup(MemberDTO memberDTO);
	
	MemberEntity signin(String email, String password, HttpSession httpSession);
	
    void modify(MemberEntity loggedInUser, HttpSession httpSession);
	
	boolean verifyEmailCode(String email, String verificationCode);
	
	void deleteMember(String email);
	
	boolean checkEmailExists(String email);
	
	Optional<MemberEntity> findByEmail(String email);
	
	default MemberEntity dtoToEntity(MemberDTO memberDTO) {
		
		if (memberDTO.getRole() == null) {
			memberDTO.setRole(MemberRole.MEMBER.name());
		}
		
		Set<MemberRole> roles = Set.of(MemberRole.valueOf(memberDTO.getRole()));
		
		return MemberEntity.builder()
				.email(memberDTO.getEmail())
				.password(memberDTO.getPassword())
				.name(memberDTO.getName())
				.area(memberDTO.getArea())
				.phone(memberDTO.getPhone())
				.fromSocial(memberDTO.isFromSocial())
				.roleSet(roles)
				.build();
				
	}
	
	default MemberDTO EntityToDto(MemberEntity memberEntity) {
		return MemberDTO.builder()
				.email(memberEntity.getEmail())
				.password(memberEntity.getPassword())
				.name(memberEntity.getName())
				.area(memberEntity.getArea())
				.phone(memberEntity.getPhone())
				.fromSocial(memberEntity.isFromSocial())
				.role(memberEntity.getRoleSet().stream()
					.map(Enum::name)
					.collect(Collectors.joining(", ")))
				.build();
	}
	
	
}
