package com.dlskawo0409.demo.auth.presentation;

import com.dlskawo0409.demo.auth.application.RedisRefreshTokenService;
import com.dlskawo0409.demo.auth.domain.RefreshEntity;
import com.dlskawo0409.demo.auth.domain.RefreshRepository;
import com.dlskawo0409.demo.auth.domain.RefreshToken;
import com.dlskawo0409.demo.auth.domain.RefreshTokenRepository;
import com.dlskawo0409.demo.auth.dto.response.AccessAndRefreshToken;
import com.dlskawo0409.demo.auth.exception.AuthException;
import com.dlskawo0409.demo.auth.jwt.JWTUtil;

import com.dlskawo0409.demo.member.exception.MemberException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
public class JWTController {
	private JWTUtil jwtUtil;
//	private RefreshRepository refreshRepository;
	private final RedisRefreshTokenService redisRefreshTokenService;


	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) throws MemberException.MemberBadRequestException, AuthException.AuthBadRequestException {

		//get refresh token
		String refresh = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("refresh")) {
				refresh = cookie.getValue();
			}
		}

		if (refresh == null) {

			//response status code
			return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
		}

		//expired check
//		try {
//			jwtUtil.isExpired(refresh);
//		} catch (ExpiredJwtException e) {
//
//			//response status code
//			return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
//		}

		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		// String category = jwtUtil.getCategory(refresh);
		//
		// if (!category.equals("refresh")) {
		//
		// 	//response status code
		// 	return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
		// }
		//
		// //DB에 저장되어 있는지 확인
		// Boolean isExist = refreshRepository.existsByRefresh(refresh);
		// if (!isExist) {
		//
		// 	//response body
		// 	return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
		// }

//		String username = jwtUtil.getUsername(refresh);
//		String role = jwtUtil.getRole(refresh).getKey();
//		Long memberId = jwtUtil.getMemberId(refresh);

		//make new JWT
//		String newAccess = jwtUtil.createJwt("access", username, role, memberId);
		// String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L, memberId);
		//Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
		// refreshRepository.deleteByRefresh(refresh);
		// addRefreshEntity(username, newRefresh, 86400000L);

		//redis
		AccessAndRefreshToken accessAndRefreshToken = redisRefreshTokenService.getAccessAndRefreshToken(refresh);

		//response
		response.setHeader("access", accessAndRefreshToken.accessToken());
		response.addCookie(createCookie("refresh", accessAndRefreshToken.refreshToken()));

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/oauth2")
	public ResponseEntity<?> getJWTByCookie(HttpServletRequest request, HttpServletResponse response) {
		String authorization = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			System.out.println(cookie);
			if (cookie.getName().equals("Authorization")) {

				authorization = cookie.getValue();
			}
		}

		if (authorization == null) {

			//response status code
			return new ResponseEntity<>("authorization token null", HttpStatus.BAD_REQUEST);
		}

		//expired check
		try {
			jwtUtil.isExpired(authorization);
		} catch (ExpiredJwtException e) {

			//response status code
			return new ResponseEntity<>("authorization token expired", HttpStatus.BAD_REQUEST);
		}

		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		String category = jwtUtil.getCategory(authorization);

		if (!category.equals("access")) {

			//response status code
			return new ResponseEntity<>("invalid authorization token", HttpStatus.BAD_REQUEST);
		}

		String username = jwtUtil.getUsername(authorization);
		String role = jwtUtil.getRole(authorization).getKey();
		Long memberId = jwtUtil.getMemberId(authorization);

		//make new JWT
		String newAccess = jwtUtil.createJwt("access", username, role, memberId);

		//response
		response.setHeader("access", newAccess);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private Cookie createCookie(String key, String value) {

		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(14440);
		cookie.setSecure(false);
		cookie.setPath("/");
		cookie.setHttpOnly(true);

		return cookie;
	}

//	private void addRefreshEntity(String username, String refresh, Long expiredMs) {
//
//		Date date = new Date(System.currentTimeMillis() + expiredMs);
//
//		RefreshEntity refreshEntity = RefreshEntity.builder()
//			.username(username)
//			.refresh(refresh)
//			.expiration(date.toString())
//			.build();
//
//		refreshRepository.save(refreshEntity);
//	}

}