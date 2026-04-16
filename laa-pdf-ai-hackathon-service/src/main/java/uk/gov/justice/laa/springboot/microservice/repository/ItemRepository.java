package uk.gov.justice.laa.springboot.microservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.justice.laa.springboot.microservice.entity.ItemEntity;

/** Items scaffold — pending deletion. */
@Deprecated
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
}