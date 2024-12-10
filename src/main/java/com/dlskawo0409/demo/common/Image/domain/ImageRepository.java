package com.dlskawo0409.demo.common.Image.domain;


import java.util.List;

public interface ImageRepository{
    Image findByImageId(Long imageId);
    boolean save(Image image);
    boolean delete(Long imageId);
    boolean update(Image image);
//    List<ImageResponseDTO> findByReferenceId(@Param("referenceId") String referenceId, @Param("imageType") ImageType imageType);

}