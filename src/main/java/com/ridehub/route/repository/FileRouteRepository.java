package com.ridehub.route.repository;

import com.ridehub.route.domain.FileRoute;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FileRoute entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FileRouteRepository extends JpaRepository<FileRoute, Long>, JpaSpecificationExecutor<FileRoute> {}
