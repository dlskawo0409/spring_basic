package com.dlskawo0409.demo.auth.exception;

import org.apache.coyote.BadRequestException;

import com.dlskawo0409.demo.common.exception.ErrorCode;

public class AuthException {

	public static class AuthBadRequestException extends BadRequestException {
		public AuthBadRequestException(AuthErrorCode errorCode) {
			super(String.valueOf(new ErrorCode<>(errorCode.getCode(), errorCode.getMessage())));
		}
	}

}
