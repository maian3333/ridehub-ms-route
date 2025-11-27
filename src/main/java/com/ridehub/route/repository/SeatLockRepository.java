package com.ridehub.route.repository;

import com.ridehub.route.domain.SeatLock;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SeatLock entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SeatLockRepository extends JpaRepository<SeatLock, Long>, JpaSpecificationExecutor<SeatLock> {}
