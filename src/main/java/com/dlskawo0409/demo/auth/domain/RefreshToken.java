package com.dlskawo0409.demo.auth.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 14440)
public class RefreshToken {

	@Id
	private String refreshToken;
	private Long memberId;

	public RefreshToken(String refreshToken, Long memberId) {
		this.refreshToken = refreshToken;
		this.memberId = memberId;
	}
}