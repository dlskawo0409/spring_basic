package com.dlskawo0409.demo.common.Image.domain;

import com.dlskawo0409.demo.common.exception.BadRequestException;

import java.util.Arrays;

import static com.dlskawo0409.demo.common.Image.exception.ImageErrorCode.ILLEGAL_IMAGE_FILE_EXTENSION;

public enum ImageExtension {

    JPEG(".jpeg"),
    JPG(".jpg"),
    JFIF(".jfif"),
    PNG(".png"),
    SVG(".svg"),
    ;


    private final String extension;

    ImageExtension(final String extension) {
        this.extension = extension;
    }

    public static String from(String imageFileName) {
        boolean isValidExtension = Arrays.stream(values())
                .anyMatch(imageExtension -> imageFileName.toLowerCase().endsWith(imageExtension.getExtension().toLowerCase()));

        if (isValidExtension) {
            return imageFileName;
        } else {
            throw new BadRequestException.ImageBadRequestException(ILLEGAL_IMAGE_FILE_EXTENSION);
        }
    }



    public String getExtension() {
        return extension;
    }

}