package com.dlskawo0409.demo.auth.application;

import org.springframework.stereotype.Service;

import com.dlskawo0409.demo.auth.domain.RefreshToken;
import com.dlskawo0409.demo.auth.domain.RefreshTokenRepository;
import com.dlskawo0409.demo.auth.exception.AuthErrorCode;
import com.dlskawo0409.demo.auth.exception.AuthException;
import com.dlskawo0409.demo.auth.jwt.JWTUtil;
import com.dlskawo0409.demo.common.exception.BadRequestException;
import com.dlskawo0409.demo.member.domain.Member;
import com.dlskawo0409.demo.member.domain.MemberRepository;
import com.dlskawo0409.demo.member.exception.MemberErrorCode;
import com.dlskawo0409.demo.member.exception.MemberException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class redisRefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;
	private final JWTUtil jwtUtil;
	private final MemberRepository memberRepository;

	public String regenerateAccessToken(final RefreshToken refreshToken) throws
		AuthException.AuthBadRequestException,
		MemberException.MemberBadRequestException {
		RefreshToken refreshToken1 = refreshTokenRepository.findById(refreshToken.getRefreshToken())
			.orElseThrow(() -> new AuthException.AuthBadRequestException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUNDED));

		Member member = memberRepository.findById(refreshToken1.getMemberId())
			.orElseThrow(() -> new MemberException.MemberBadRequestException(MemberErrorCode.MEMBER_NOT_FOUND));

		return jwtUtil.createJwt("access", member.getUsername(), member.getRole().getKey(), 60000000L,
			member.getMemberId());
	}
}
