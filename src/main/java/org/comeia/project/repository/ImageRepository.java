package org.comeia.project.repository;

import java.util.Optional;

import org.comeia.project.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ImageRepository extends JpaRepository<Image, Long>, JpaSpecificationExecutor<Image> {

	Optional<Image> findByIdAndDeletedIsFalse(long id);
}

