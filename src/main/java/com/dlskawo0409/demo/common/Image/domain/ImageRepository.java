package com.dlskawo0409.demo.common.Image.domain;


import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
//    List<ImageResponseDTO> findByReferenceId(@Param("referenceId") String referenceId, @Param("imageType") ImageType imageType);

}