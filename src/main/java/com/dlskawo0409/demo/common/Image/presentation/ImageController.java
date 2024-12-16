package com.dlskawo0409.demo.common.Image.presentation;

import com.dlskawo0409.demo.common.Image.application.ImageService;
import com.dlskawo0409.demo.common.Image.domain.ImageType;
import com.dlskawo0409.demo.common.Image.dto.request.ImageInsertDTO;
import com.dlskawo0409.demo.common.Image.exception.ImageException;
import com.dlskawo0409.demo.common.storage.application.StorageService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
@Tag(name = "Image", description = "이미지 API")
public class ImageController {
    private final ImageService imageService;

    @GetMapping("/{imageType}/{imageId}")
    public ResponseEntity<?> getImageByImageIdController(
            @PathVariable("imageId") Long imageId,
            @Parameter(description = "Image Type", schema = @Schema(implementation = ImageType.class))
            @PathVariable("imageType") ImageType imageType ) throws IOException {
        return ResponseEntity.ok(imageService.getPreSignedURL(imageId));
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registrationImageController( @Valid @RequestPart("information") ImageInsertDTO imageInsertDTO,
                                                          @Parameter(
                                                                  description = "multipart/form-data 형식의 이미지를 input으로 받습니다.",
                                                                  content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
                                                          )
                                                          @RequestPart("image") MultipartFile multipartFile) throws Exception {
        imageService.upload(multipartFile,imageInsertDTO.getReferenceId(),imageInsertDTO.getImageType());
        return new ResponseEntity<>("이미지가 성공적으로 추가되었습니다.", HttpStatus.CREATED);
    }


//    @PostMapping("/url")
//    public ResponseEntity<?> registrationImageByURLController(@RequestBody ImageInsertByURLDTO imageInsertByURLDTO){
//        imageService.upload(imageInsertByURLDTO);
//        return new ResponseEntity<>("이미지가 성공적으로 추가되었습니다.", HttpStatus.CREATED);
//    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable("image-id") Long imageId) throws ImageException.ImageBadRequestException {
        imageService.delete(imageId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
