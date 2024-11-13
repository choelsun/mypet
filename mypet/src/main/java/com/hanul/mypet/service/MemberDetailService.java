package com.hanul.mypet.service;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hanul.mypet.entity.MemberEntity;
import com.hanul.mypet.repository.MemberRepository;
import com.hanul.mypet.security.dto.MemberAuthDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("MemberDetailService loadUserByUsername() : {}", username);

        Optional<MemberEntity> result =
                memberRepository.findByEmail(username, false);

        if (result.isEmpty()) {
            throw new UsernameNotFoundException("다시 시도해 주세요.");
        }

        MemberEntity memberEntity = result.get();

        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("Member : {}", memberEntity);

        MemberAuthDTO memberAuthDTO = new MemberAuthDTO(
                memberEntity.getEmail(),
                memberEntity.getPassword(),
                memberEntity.getName(),
                memberEntity.getArea(),
                memberEntity.getCityArea(), // cityArea 값 추가
                memberEntity.getDetailArea(), // detailArea 값 추가
                memberEntity.getPhone(),
                memberEntity.isFromSocial(),
                memberEntity.getRoleSet()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                        .collect(Collectors.toSet())
        );

        return memberAuthDTO;
    }
}
