package uk.gov.justice.laa.springboot.microservice.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.springboot.microservice.entity.OcrOutputEntity;

/**
 * Repository for managing {@link OcrOutputEntity} persistence.
 */
@Repository
public interface OcrOutputRepository extends JpaRepository<OcrOutputEntity, UUID> {
}
