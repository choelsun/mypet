package com.hanul.mypet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {

	private String email;
	
	private String password;
	
	private String name;
	
	private String area;
	
	private String cityArea;
	
	private String detailArea;
	
	private String phone;
	
	private boolean fromSocial;
	
	@Builder.Default
	private String role = "MEMBER";
}
