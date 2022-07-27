package com.spring.boardweb.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Data;

@Data
public class CustomUserDetails implements UserDetails, OAuth2User{
	private User user;
	
	public CustomUserDetails(User user) {
		this.user = user;
	}
	
	//소셜 로그인 정보를 담을 맵
	Map<String, Object> attributes;
	
	//Oauth 로그인 시에 사용될 생성자
	public CustomUserDetails(User user, Map<String, Object> attributes) {
		this.user = user;
		this.attributes = attributes;
	}
	
	
	
	
	//권한정보 제공
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
		auths.add(
			new GrantedAuthority() {
				@Override
				public String getAuthority() {
					return user.getRole();
				}
			}
		);
		
		return auths;
	}
	
	//비밀번호 정보 제공
	@Override
	public String getPassword() {
		return user.getUserPw();
	}
	
	//ID 정보 제공
	@Override
	public String getUsername() {
		return user.getUserId();
	}
	
	//계정 만료 여부
	//사용 안 할 때 항상 true 반환하도록 처리
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	//계정 잠김 여부
	//사용 안 할 때 항상 true 반환하도록 처리
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	//계정 인증정보를 항상 저장할 지에 대한 여부
	//true 처리할 시 모든 인증정보를 만료시키지 않도록 주의
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	//계정의 활성화 여부
	@Override
	public boolean isEnabled() {
		return true;
	}
	
	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	
	@Override
	public String getName() {
		return null;
	}
	
}
