package com.spring.boardweb.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.spring.boardweb.entity.CustomUserDetails;
import com.spring.boardweb.entity.User;
import com.spring.boardweb.oauth.provider.KakaoUserInfo;
import com.spring.boardweb.oauth.provider.Oauth2UserInfo;
import com.spring.boardweb.repository.UserRepository;

@Service
public class Oauth2UserService extends DefaultOAuth2UserService{
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	//토큰이 발행된 후 사용자 정보에 대한 후처리 함수
	//소셜 로그인 버튼 클릭 -> 사용자 동의 창 -> 사용자 동의 후 로그인 완료 -> code리턴 -> 토큰 수령
	// -> 토큰을 통한 유저정보 획득 -> loadUser 함수 자동 호출 -> 사용자 정보를 커스터마이징
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		
		String userName = "";
		String providerId = "";
		
		Oauth2UserInfo oauth2UserInfo = null;
		
		//소셜 카테고리 검증
		if(userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
			oauth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
			providerId = oauth2UserInfo.getProviderId();
			userName = oauth2UserInfo.getName();
			
		} else {
			System.out.println("카카오 로그인만 지원합니다.");
		}
		//kakao
		String provider = oauth2UserInfo.getProvider();
		//kakao_19940508
		String userId = provider + "_" + providerId;
		String password = passwordEncoder.encode(oauth2UserInfo.getName());
		String email = oauth2UserInfo.getEmail();
		String role = "ROLE_USER";
		
		//사용자가 가입되어 있는지 검사할 Entity 객체 생성
		User user;
		
		if(userRepository.findById(userId).isPresent()) {
			//가입이 되어있으면 정보 담아줌
			user = userRepository.findById(userId).get();
		} else {
			//가입이 안되어있으면 null 리턴하여 가입 진행
			user = null;
		}
		
		//소셜 로그인 정보로 가입 진행
		if(user == null) {
			user = new User();
			user.setUserId(userId);
			user.setUserPw(password);
			user.setUserNm(oauth2UserInfo.getName());
			user.setUserMail(email);
			user.setRole(role);
			
			//사용자 정보 DB에 저장
			userRepository.save(user);
		}
		
		//세션에 로그인 정보 저장
		return new CustomUserDetails(user, oAuth2User.getAttributes());
	}
}
