package com.dlskawo0409.demo.common.Image.domain;

import com.dlskawo0409.demo.common.domain.BasicEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Image extends BasicEntity {
    @Schema(hidden = true)
    private Long imageId;

    @Schema(hidden = true)
    private String imageUrl;

    private ImageType imageType;

    private String referenceId;

    public static String makeImageName(MultipartFile multipartFile){
        return UUID.randomUUID() +
                ImageExtension.from(multipartFile.getOriginalFilename());
    }

    public static MediaType getMediaTypeForFileName(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();

        return switch (extension) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            case "gif" -> MediaType.IMAGE_GIF;
            default -> MediaType.APPLICATION_OCTET_STREAM; // 알 수 없는 확장자의 경우 기본 설정
        };
    }
}