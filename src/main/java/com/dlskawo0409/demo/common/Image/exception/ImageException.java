package com.dlskawo0409.demo.common.Image.exception;

import com.dlskawo0409.demo.common.exception.ErrorCode;
import org.apache.coyote.BadRequestException;

public class ImageException {
    public static class ImageBadRequestException extends BadRequestException {
        public ImageBadRequestException(ImageErrorCode errorCode) {
            super(String.valueOf(new ErrorCode<>(errorCode.getCode(), errorCode.getMessage())));
        }
    }

}
