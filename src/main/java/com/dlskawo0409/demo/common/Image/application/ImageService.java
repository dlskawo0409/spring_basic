package com.dlskawo0409.demo.common.Image.application;

import com.dlskawo0409.demo.common.Image.domain.Image;
import com.dlskawo0409.demo.common.Image.domain.ImageType;
import com.dlskawo0409.demo.common.Image.dto.response.ImageResponseDTO;
import com.dlskawo0409.demo.common.Image.exception.ImageException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {
    Image upload(MultipartFile multipartFile, String referenceId, ImageType imageType) throws IOException;
    void delete (Long imageId);
    String getPresignedURL(Long imageId) throws ImageException.ImageBadRequestException;
    Image update(MultipartFile multipartFile, Long imageId, ImageType imageType);
    List<ImageResponseDTO> getImage(String referenceId, ImageType imageType);
//    List<Long> getImageIdList(String referenceId);
}
