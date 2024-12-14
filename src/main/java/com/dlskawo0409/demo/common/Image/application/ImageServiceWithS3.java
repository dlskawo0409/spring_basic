package com.dlskawo0409.demo.common.Image.application;

import com.dlskawo0409.demo.common.Image.domain.Image;
import com.dlskawo0409.demo.common.Image.domain.ImageRepository;
import com.dlskawo0409.demo.common.Image.domain.ImageType;
import com.dlskawo0409.demo.common.Image.dto.request.ImageInsertByUrlDTO;
import com.dlskawo0409.demo.common.Image.exception.ImageErrorCode;
import com.dlskawo0409.demo.common.Image.exception.ImageException;
import com.dlskawo0409.demo.common.storage.application.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.beans.Transient;
import java.io.IOException;


@Service
@RequiredArgsConstructor
public class ImageServiceWithS3 implements ImageService{
    private final StorageService storageService;
    private final ImageRepository imageRepository;

    @Transient
    @Override
    public Image upload(MultipartFile multipartFile, String referenceId, ImageType imageType) throws IOException {
        String imageName = Image.makeImageName(multipartFile);
        Image image = Image.builder()
                .imageUrl(imageName)
                .referenceId(referenceId)
                .imageType(imageType)
                .build();
        storageService.store(multipartFile, imageType.getUrl()+"/"+imageName);
        imageRepository.save(image);
        return image;
    }


    @Override
    public void upload(ImageInsertByUrlDTO imageInsertByUrlDTO) {
        Image image = Image.builder()
                .imageUrl(imageInsertByUrlDTO.imageUrl())
                .referenceId(imageInsertByUrlDTO.referenceId())
                .imageType(imageInsertByUrlDTO.imageType())
                .build();
        imageRepository.save(image);
    }


    @Override
    @Transactional
    public void delete(Long imageId) throws ImageException.ImageBadRequestException {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageException.ImageBadRequestException(ImageErrorCode.CANT_NOT_FIND_IMAGE));
        storageService.deleteOne(image.getImageUrl());
        imageRepository.delete(image);
    }

//    @Override
//    public void delete(Image image){
//        storageService.deleteOne(image.getImageUrl());
//    }


    @Override
    public String getPreSignedURL(Long imageId) throws ImageException.ImageBadRequestException {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageException.ImageBadRequestException(ImageErrorCode.CANT_NOT_FIND_IMAGE))
                .getImageUrl();
    }

    @Override
    public Image update(MultipartFile multipartFile, Long imageId, ImageType imageType) throws IOException {
        String imageName = Image.makeImageName(multipartFile);
        storageService.store(multipartFile,imageType.getUrl()+"/"+imageName);
        Image image = Image.builder()
                .imageId(imageId)
                .imageUrl(imageName)
                .build();

        return imageRepository.save(image);
    }

//    @Override
//    public List<ImageResponseDTO> getImage(String referenceId, ImageType imageType) {
//        return imageRepository.findByReferenceId(referenceId ,imageType);
//    }

//    @Override
//    public List<Long> getImageIdList(String referenceId) {
//        return imageRepository.findByImageList(referenceId);
//    }


}
