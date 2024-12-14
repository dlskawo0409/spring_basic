package com.dlskawo0409.demo.common.Image.dto.request;

import com.dlskawo0409.demo.common.Image.domain.ImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
public record ImageInsertByUrlDTO(
        String referenceId,
        ImageType imageType,
        String imageUrl
) {
}
