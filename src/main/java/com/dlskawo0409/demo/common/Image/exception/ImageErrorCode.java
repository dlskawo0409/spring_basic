package com.dlskawo0409.demo.common.Image.exception;

import lombok.Getter;

@Getter
public enum ImageErrorCode {

    ILLEGAL_IMAGE_FILE_EXTENSION("10000", "지원하지 않는 이미지 파일입니다."),
    IMAGE_FILE_IS_NULL("10001", "이미지가 선택되지 않았습니다."),
    IMAGE_URL_AND_REFERENCE_ID_IS_NULL("10002", "이미지URL과 referenceId가 없습니다."),
    CANT_NOT_FIND_IMAGE("10003","이미지를 찾을 수 없습니다."),
    IMAGE_TYPE_IS_NULL("10004","이미지 타입이 선택되지 않았습니다.");

    private final String code;
    private final String message;

    ImageErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}