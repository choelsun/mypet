package com.hanul.mypet.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hanul.mypet.dto.MemberDTO;
import com.hanul.mypet.entity.MemberEntity;
import com.hanul.mypet.repository.MemberRepository;
import com.hanul.mypet.security.dto.MemberAuthDTO;
import com.hanul.mypet.service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/member")
@Log4j2
@RequiredArgsConstructor
public class MemberController {

	@Autowired
	private MemberService memberService;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/main")
	public String mainPage(Model model, Principal principal, HttpSession session) {
	    Optional<MemberEntity> optionalMemberEntity = principal != null ? memberRepository.findByEmail(principal.getName()) : Optional.empty();
	    MemberEntity loggedInUser = optionalMemberEntity.orElse(null);

	    model.addAttribute("loggedInUser", loggedInUser);
	    session.setAttribute("loggedInUser", loggedInUser);
	    return "member/main";
	}

	@GetMapping("/terms")
	public void termsGET() {
	}
	
	@PostMapping("/terms")
	public String termsPOST(@RequestParam("agree") boolean agree) {
	    if (agree) {
	        // 동의한 경우
	        return "redirect:/member/signup";
	    } else {
	        // 동의하지 않은 경우
	        return "redirect:/terms";  // 다시 약관 페이지로 리다이렉트
	    }
	}

	@GetMapping("/signup")
	public String signupPage(Model model) {
	    model.addAttribute("CityArea", ""); // 기본 값을 빈 문자열로 설정
	    model.addAttribute("detailArea", ""); // 기본 값을 빈 문자열로 설정
	    return "member/signup";
	}

	@PostMapping("/signup")
	public String signupPOST(MemberDTO memberDTO, RedirectAttributes redirectAttributes) {
	    try {
	        log.info("회원가입 요청: {}", memberDTO);
	        memberService.signup(memberDTO);
	        redirectAttributes.addFlashAttribute("signupSuccess", true);
	        return "redirect:/main"; // 회원가입 후 메인 페이지로 이동
	    } catch (Exception e) {
	        log.error("회원가입 중 오류: {}", e.getMessage());
	        redirectAttributes.addFlashAttribute("errorSignup", "가입 실패. 확인 바람.");
	        return "redirect:/member/signup"; // 오류 시 다시 회원가입 페이지로 이동
	    }
	}

	@GetMapping("/signin")
	public String signinGET(Model model) {
		return "member/signin";
	}

	@PostMapping("/signin")
	public String signinPOST(@RequestParam String email,
	                         @RequestParam String password,
	                         RedirectAttributes redirectAttributes,
	                         HttpSession httpSession) {
	    MemberEntity memberEntity = memberService.signin(email, password, httpSession);

	    if (memberEntity != null) {
	        // 로그인 성공 시 세션에 사용자 정보를 담습니다.
	        httpSession.setAttribute("loggedInUser", memberEntity);
	        log.info("로그인 성공: {}", memberEntity);
	        log.info("세션에 담긴 사용자 정보: {}", httpSession.getId());
	        return "redirect:/member/main";
	    } else {
	        redirectAttributes.addFlashAttribute("error", "로그인 실패. 이메일 또는 비밀번호를 확인하세요");
	    }
	    return "redirect:/member/signin";
	}

	
	@GetMapping("/logout")
	public String logout(HttpSession httpSession) {
		httpSession.invalidate();
		return "redirect:/member/main";
	}
	

	@PostMapping("/check-email")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> checkEmail(@RequestBody Map<String, String> emailMap) {
		Map<String, Object> response = new HashMap<>();
		String email = emailMap.get("email");
		try {
			log.info("이메일 중복 확인 요청: {}", email);

			boolean exists = memberService.checkEmailExists(email);

			response.put("exists", exists);
			response.put("message", exists ? "이미 존재하는 이메일 입니다" : "사용 가능한 이메일입니다");
		} catch (Exception e) {
			log.error("이메일 중복 확인 중 오류: {}", e.getMessage());

			response.put("success", false);
			response.put("errorCheckEmail", "이메일 중복 확인 중 오류가 발생했습니다");
		}
		return ResponseEntity.ok(response);
	}


	@GetMapping("/myinfo")
	public String getProfile(Principal principal, Model model) {
	    if (principal == null) {
	        return "redirect:/member/signin"; // 로그인 페이지로 리다이렉트
	    }

	    Optional<MemberEntity> optionalMemberEntity = memberRepository.findByEmail(principal.getName());
	    MemberEntity loggedInUser = optionalMemberEntity.orElse(null);

	    if (loggedInUser == null) {
	        return "redirect:/member/signin?error"; // 사용자를 찾을 수 없는 경우
	    }

	    model.addAttribute("loggedInUser", loggedInUser);
	    return "member/myinfo"; // myinfo 페이지로 이동
	}



	// 비밀번호 확인 페이지 열기
	@GetMapping("/verifyPassword")
	public String showVerifyPasswordForm(Principal principal, Model model) {
	    if (principal == null) {
	        return "redirect:/member/signin?error";
	    }
	    return "member/verifyPassword"; // 비밀번호 확인 페이지로 이동
	}

    // 비밀번호 확인 처리
	@PostMapping("/verifyPassword")
	public String verifyPassword(@RequestParam("password") String password, 
	                             Principal principal, 
	                             HttpSession session,
	                             RedirectAttributes redirectAttributes) {
	    if (principal == null) {
	        return "redirect:/member/signin?error";
	    }

	    // 로그인한 사용자 정보 가져오기
	    String username = principal.getName();
	    Optional<MemberEntity> optionalMemberEntity = memberRepository.findByEmail(username);
	    MemberEntity loggedInUser = optionalMemberEntity.orElse(null);

	    if (loggedInUser == null || !passwordEncoder.matches(password, loggedInUser.getPassword())) {
	        redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
	        return "redirect:/member/verifyPassword";
	    }

	    // 세션에 사용자 정보 저장
	    session.setAttribute("loggedInUser", loggedInUser);

	    return "redirect:/member/editprofile"; // 정보 수정 페이지로 리다이렉트
	}




	@GetMapping("/editprofile")
	public String showEditProfileForm(Model model, HttpSession session) {
	    MemberEntity loggedInUser = (MemberEntity) session.getAttribute("loggedInUser");

	    if (loggedInUser == null) {
	        return "redirect:/member/signin";
	    }

	    model.addAttribute("loggedInUser", loggedInUser);
	    model.addAttribute("selectedArea", loggedInUser.getArea());
	    model.addAttribute("selectedDetailArea", loggedInUser.getDetailArea());

	    return "member/editprofile";
	}

	// 정보 수정 처리
	@PostMapping("/editprofile")
	public String updateProfile(@AuthenticationPrincipal MemberAuthDTO loggedInUser,
	                            @ModelAttribute MemberDTO memberDTO,
	                            RedirectAttributes redirectAttributes) {
	    if (loggedInUser == null) {
	        redirectAttributes.addFlashAttribute("error", "사용자 정보를 찾을 수 없습니다.");
	        return "redirect:/member/editprofile";
	    }

	    // 수정할 정보 업데이트
	    if (memberDTO.getName() != null) {
	        loggedInUser.setName(memberDTO.getName());
	    }
	    if (memberDTO.getArea() != null) {
	        loggedInUser.setArea(memberDTO.getArea());
	    }
	    if (memberDTO.getCityArea() != null) {
	        loggedInUser.setCityArea(memberDTO.getCityArea());
	    }
	    if (memberDTO.getDetailArea() != null) {
	        loggedInUser.setDetailArea(memberDTO.getDetailArea());
	    }
	    if (memberDTO.getPhone() != null) {
	        loggedInUser.setPhone(memberDTO.getPhone());
	    }
	    if (memberDTO.getPassword() != null && !memberDTO.getPassword().isEmpty()) {
	        loggedInUser.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
	    }

	    // MemberEntity 업데이트 및 저장
	    Optional<MemberEntity> optionalMemberEntity = memberRepository.findByEmail(loggedInUser.getEmail());
	    if (optionalMemberEntity.isPresent()) {
	        MemberEntity memberEntity = optionalMemberEntity.get();
	        memberEntity.setName(loggedInUser.getName());
	        memberEntity.setArea(loggedInUser.getArea());
	        memberEntity.setCityArea(loggedInUser.getCityArea());
	        memberEntity.setDetailArea(loggedInUser.getDetailArea());
	        memberEntity.setPhone(loggedInUser.getPhone());
	        memberEntity.setPassword(loggedInUser.getPassword());

	        memberRepository.save(memberEntity);
	    }

	    redirectAttributes.addFlashAttribute("success", "정보가 성공적으로 수정되었습니다.");
	    return "redirect:/member/myinfo"; // 수정 완료 후 myinfo 페이지로 리다이렉트
	}
	
	@GetMapping("/bye")
	public String showByePage(Principal principal, Model model) {
	    if (principal == null) {
	        return "redirect:/member/signin?error";
	    }
	    return "member/bye"; // 탈퇴 확인 페이지로 이동
	}

    // 탈퇴 처리
    @PostMapping("/bye")
    public String deleteAccount(@RequestParam("password") String password, 
                                Principal principal, 
                                HttpSession httpSession,
                                RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/member/signin?error";
        }

        // 현재 로그인된 사용자 이메일을 가져와 사용자 정보 조회
        String email = principal.getName();
        Optional<MemberEntity> optionalMemberEntity = memberRepository.findByEmail(email);
        MemberEntity loggedInUser = optionalMemberEntity.orElse(null);

        if (loggedInUser == null) {
            return "redirect:/member/signin?error";
        }

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, loggedInUser.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/member/bye";
        }

        // 회원 정보 삭제
        memberService.deleteMember(loggedInUser.getEmail());

        // 세션 무효화
        httpSession.invalidate();

        // 탈퇴 성공 메시지 설정
        redirectAttributes.addFlashAttribute("message", "탈퇴가 완료되었습니다.");
        return "redirect:/member/main"; // 탈퇴 후 메인 페이지로 리다이렉트
    }
}
