package com.dlskawo0409.demo.auth.application;

import com.dlskawo0409.demo.auth.dto.response.AccessAndRefreshToken;
import jakarta.persistence.Access;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dlskawo0409.demo.auth.domain.RefreshToken;
import com.dlskawo0409.demo.auth.domain.RefreshTokenRepository;
import com.dlskawo0409.demo.auth.exception.AuthErrorCode;
import com.dlskawo0409.demo.auth.exception.AuthException;
import com.dlskawo0409.demo.auth.jwt.JWTUtil;
import com.dlskawo0409.demo.member.domain.Member;
import com.dlskawo0409.demo.member.domain.MemberRepository;
import com.dlskawo0409.demo.member.exception.MemberErrorCode;
import com.dlskawo0409.demo.member.exception.MemberException;

import lombok.RequiredArgsConstructor;

import java.sql.Ref;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisRefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;
	private final JWTUtil jwtUtil;
	private final MemberRepository memberRepository;


	public Member getMemberByRefreshToken(final String refreshToken) throws
		AuthException.AuthBadRequestException,
		MemberException.MemberBadRequestException {
		RefreshToken refreshToken1 = refreshTokenRepository.findById(refreshToken)
			.orElseThrow(() -> new AuthException.AuthBadRequestException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUNDED));

		System.out.println(refreshToken1);


        return memberRepository.findById(refreshToken1.getMemberId())
            .orElseThrow(() -> new MemberException.MemberBadRequestException(MemberErrorCode.MEMBER_NOT_FOUND));
	}

	public String generateRefreshToken(Long memberId){
		String refreshToken = UUID.randomUUID().toString();
		RefreshToken redis = RefreshToken.builder()
				.refreshToken(refreshToken)
				.memberId(memberId)
				.build();
		log.info("userDetails.getUser().getId() = {}", memberId);
		refreshTokenRepository.save(redis);
		return refreshToken;
	}

	protected void deleteRefreshToken(String refreshToken){
		System.out.println(refreshToken);
		refreshTokenRepository.deleteById(refreshToken);
	}

	public AccessAndRefreshToken getAccessAndRefreshToken(final String refreshToken) throws MemberException.MemberBadRequestException, AuthException.AuthBadRequestException {
		Member member = getMemberByRefreshToken(refreshToken);
		String newAccessToken = jwtUtil.createJwt("access", member.getUsername(), member.getRole().getKey(), member.getMemberId());
        String newRefreshToken = generateRefreshToken(member.getMemberId());

		deleteRefreshToken(refreshToken);

		return AccessAndRefreshToken.builder()
				.accessToken(newAccessToken)
				.refreshToken(newRefreshToken).build();
	}


}
