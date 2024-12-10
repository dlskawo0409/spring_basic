package com.dlskawo0409.demo.common.Image.dto.request;

import com.dlskawo0409.demo.common.Image.domain.ImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImageInsertDTO {
    @Schema(description = "참조할 Id, member_id or apart_id", required = true, example = "1")
    private String referenceId;
    @Schema(description = "PROFILE, APARTMENT", required = true, example = "APARTMENT")
    private ImageType imageType;
}
