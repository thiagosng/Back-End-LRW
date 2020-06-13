package org.comeia.project.converter;

import static java.util.Optional.ofNullable;

import java.util.Objects;

import org.comeia.project.domain.Image;
import org.comeia.project.dto.ImageDTO;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ImageConverter implements Converter<Image, ImageDTO> {

	@Override
	public ImageDTO from(Image entity) {
		
		if(Objects.isNull(entity)) {
			return null;
		}
		
		ImageDTO dto = new ImageDTO();
		
		ofNullable(entity.getId())
			.ifPresent(dto::setId);
		
		ofNullable(entity.getFullName())
			.ifPresent(dto::setFullName);
		
		ofNullable(entity.getImageType())
			.ifPresent(dto::setImageType);
		
		return dto;
	}
	
	@Override
	public Image to(ImageDTO dto, Image entity) {
		
		if(Objects.isNull(dto)) {
			return null;
		}

		if(Objects.isNull(entity)) {
			entity = new Image();
		}
		
		ofNullable(dto.getFullName())
			.ifPresent(entity::setFullName);
		
		ofNullable(dto.getImageType())
			.ifPresent(entity::setImageType);
		
		return entity;
	}
}