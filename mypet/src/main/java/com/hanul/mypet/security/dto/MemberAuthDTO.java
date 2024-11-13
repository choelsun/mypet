package com.hanul.mypet.security.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class MemberAuthDTO extends User {

    private String email;
    private String name;
    private boolean fromSocial;
    private String area;
    private String cityArea; 
    private String detailArea; 
    private String phone;
    private String password; 

    public MemberAuthDTO(String username,
                         String password,
                         String name,
                         String area,
                         String cityArea,
                         String detailArea,
                         String phone,
                         boolean fromSocial,
                         Collection<? extends GrantedAuthority> authorities) {

        super(username, password, authorities);

        this.email = username;
        this.name = name;
        this.area = area;
        this.cityArea = cityArea; // 추가된 필드 초기화
        this.detailArea = detailArea; // 추가된 필드 초기화
        this.phone = phone;
        this.password = password; // 추가된 필드 초기화
        this.fromSocial = fromSocial;
    }
}
