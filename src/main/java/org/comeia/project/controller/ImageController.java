package org.comeia.project.controller;

import static org.comeia.project.search.ImageSpecification.listAllByCriteria;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.comeia.project.converter.ImageConverter;
import org.comeia.project.domain.Image;
import org.comeia.project.domain.Person;
import org.comeia.project.dto.ImageDTO;
import org.comeia.project.dto.ImageFilterDTO;
import org.comeia.project.dto.PersonDTO;
import org.comeia.project.enumerator.ImageType;
import org.comeia.project.locale.ErrorMessageKeys;
import org.comeia.project.repository.ImageRepository;
import org.comeia.project.search.SearchCriteria;
import org.comeia.project.validator.ImageValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.comeia.project.service.StorageService;
import org.comeia.project.storage.StorageProperties;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = "/api/v1/image")
@AllArgsConstructor
public class ImageController extends ResourceController {

	private final ImageRepository repository;
	private final ImageConverter converter;
	private final ImageValidator validator;
	private final StorageService storageService;
	
	
	@GetMapping
	public MappingJacksonValue listByCriteria(Pageable pageable,
			@RequestParam(required = false) String attributes,
			ImageFilterDTO filter) {
		
		List<SearchCriteria> criterias = ImageFilterDTO.buildCriteria(filter);
		Page<ImageDTO> pages = this.repository.findAll(listAllByCriteria(criterias), pageable)
				.map(converter::from);
		return buildResponse(pages, attributes);
	}
	
	@GetMapping(path = "/types")
	public MappingJacksonValue types(@RequestParam(required = false) String attributes) {
		ImageType[] types = ImageType.values();
		return buildResponse(types, attributes);
	}
	
	@GetMapping(path = "{id}")
	public MappingJacksonValue getById(@PathVariable long id,
			@RequestParam(required = false) String attributes) {
		
		ImageDTO dto = this.repository.findByIdAndDeletedIsFalse(id)
				.map(this.converter::from)
				.orElseThrow(() -> throwsException(ErrorMessageKeys.ERROR_IMAGE_NOT_FOUND_BY_ID, String.valueOf(id)));
		return buildResponse(dto, attributes);
	}
	
	@PostMapping
	public MappingJacksonValue create(@Validated @RequestBody ImageDTO dto,
			@RequestParam(required = false) String attributes) {
		
		if(Objects.isNull(dto)) {
			throw new HttpMessageNotReadableException("Required request body is missing");
		}
		
		ImageDTO imageDTO = Optional.of(dto)
				.map(this.converter::to)
				.map(this.repository::save)
				.map(this.converter::from)
				.orElseThrow(() -> throwsException("Error"));
		
		return buildResponse(imageDTO, attributes);
	}
	
	@PutMapping(path = "{id}")
	public MappingJacksonValue update(@PathVariable long id,
			@Validated @RequestBody ImageDTO dto,
			@RequestParam(required = false) String attributes) {
		
		if(Objects.isNull(dto)) {
			throw new HttpMessageNotReadableException("Required request body is missing");
		}
		
		ImageDTO imageDTO = this.repository.findByIdAndDeletedIsFalse(id)
				.map(image -> this.converter.to(dto, image))
				.map(this.repository::save)
				.map(this.converter::from)
				.orElseThrow(() -> throwsException(String.valueOf(id)));
		
		return buildResponse(imageDTO, attributes);
	}
	
	@DeleteMapping(path = "{id}")
	public void delete(@PathVariable long id) {
		
		ImageDTO dto = this.repository.findByIdAndDeletedIsFalse(id)
				.map(this.converter::from)
				.orElseThrow(() -> throwsException(ErrorMessageKeys.ERROR_IMAGE_NOT_FOUND_BY_ID, String.valueOf(id)));
		
		Image image = this.converter.to(dto);
		image.setDeleted(true);
		image.setId(dto.getId());
		this.repository.save(image);
		
	}
	
	@PostMapping("/")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {

		storageService.store(file);
		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file.getOriginalFilename() + "!");

		return "redirect:/";
	}
}
