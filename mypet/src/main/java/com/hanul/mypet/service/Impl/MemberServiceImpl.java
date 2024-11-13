package com.hanul.mypet.service.Impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanul.mypet.dto.MemberDTO;
import com.hanul.mypet.entity.MemberEntity;
import com.hanul.mypet.repository.MemberRepository;
import com.hanul.mypet.service.MailService;
import com.hanul.mypet.service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	@Autowired
	private final MemberRepository memberRepository;
	@Autowired
	private final MailService mailService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	@Override
	@Transactional
	public String signup(MemberDTO memberDTO) {
	    log.info("가입 요청: {}", memberDTO);

	    // DTO -> Entity 변환
	    MemberEntity memberEntity = dtoToEntity(memberDTO);
	    log.info("DTO to Entity 변환 완료: {}", memberEntity);

	    // 비밀번호 암호화
	    String encodePassword = passwordEncoder.encode(memberDTO.getPassword());
	    memberEntity.setPassword(encodePassword);
	    log.info("비밀번호 암호화 완료: {}", memberEntity.getPassword());

	    // 엔티티 저장
	    memberRepository.save(memberEntity);
	    log.info("가입 성공: {}", memberEntity);

	    return null;
	}


	@Override
	public boolean verifyEmailCode(String email, String verificationCode) {
		return mailService.verifyCode(email, verificationCode);
	}

	@Override
	public boolean checkEmailExists(String email) {
		return memberRepository.existsByEmail(email);
	}

    @Override
    public MemberEntity signin(String email, String password, HttpSession httpSession) {
        Optional<MemberEntity> optionalMemberEntity = memberRepository.findByEmail(email);
        if (optionalMemberEntity.isPresent() && passwordEncoder.matches(password, optionalMemberEntity.get().getPassword())) {
            MemberEntity memberEntity = optionalMemberEntity.get();
            httpSession.setAttribute("loggedInUser", memberEntity);
            log.info("로그인 성공 후 세션에 저장된 사용자 정보: {}", httpSession.getAttribute("loggedInUser"));
            return memberEntity;
        }
        return null;
    }

	@Override
	public void modify(MemberEntity loggedInUser, HttpSession httpSession) {
        log.info("Starting member modification for: {}", loggedInUser.getEmail());

        // 현재 로그인된 사용자의 정보를 가져옵니다.
        MemberEntity memberEntity = memberRepository.findById(loggedInUser.getEmail())
                .orElseThrow(() -> {
                    log.error("Member not found: {}", loggedInUser.getEmail());
                    return new RuntimeException("Member not found");
                });

        // 수정할 정보 로그 출력
        log.info("Current member info: {}", memberEntity);

        // 수정 가능한 필드만 업데이트
        if (loggedInUser.getArea() != null) {
            memberEntity.setArea(loggedInUser.getArea());
            log.info("Updated area for member: {}", loggedInUser.getEmail());
        }

        if (loggedInUser.getPhone() != null) {
            memberEntity.setPhone(loggedInUser.getPhone());
            log.info("Updated phone for member: {}", loggedInUser.getEmail());
        }

        // 비밀번호가 변경된 경우만 해시하여 업데이트
        if (loggedInUser.getPassword() != null && !loggedInUser.getPassword().isEmpty()) {
            log.info("Updating password for member: {}", loggedInUser.getEmail());
            memberEntity.setPassword(passwordEncoder.encode(loggedInUser.getPassword()));
        }

        // 수정된 정보를 저장합니다.
        memberRepository.save(memberEntity);
        log.info("Member info updated successfully: {}", memberEntity);

        // 세션 정보를 업데이트 할 수 있다면 여기서 처리
        httpSession.setAttribute("loggedInUser", memberEntity);
        log.info("Session updated for member: {}", loggedInUser.getEmail());
    }

	@Override
	public void deleteMember(String email) {

	    Optional<MemberEntity> result = memberRepository.findById(email);
	    
	    if (result.isPresent()) {
	        memberRepository.delete(result.get());
	        log.info("회원 삭제 완료: {}", email);
	    } else {
	        log.warn("회원 삭제 실패 - 이메일을 찾을 수 없음: {}", email);
	        throw new RuntimeException("회원 정보를 찾을 수 없습니다.");
	    }
	}
	
	@Override
	public Optional<MemberEntity> findByEmail(String email) {
	    return memberRepository.findByEmail(email);
	}


}
