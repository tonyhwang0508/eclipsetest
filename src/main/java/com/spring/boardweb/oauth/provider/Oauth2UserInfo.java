package com.spring.boardweb.oauth.provider;

//여러가지의 소셜 로그인을 구현하기 위한 인터페이스파일
public interface Oauth2UserInfo {
	String getProviderId();
	String getProvider();
	String getEmail();
	String getName();

}
