package com.ridehub.route.service;

import com.ridehub.route.domain.*; // for static metamodels
import com.ridehub.route.domain.SeatMap;
import com.ridehub.route.repository.SeatMapRepository;
import com.ridehub.route.service.criteria.SeatMapCriteria;
import com.ridehub.route.service.dto.FloorDTO;
import com.ridehub.route.service.dto.SeatDTO;
import com.ridehub.route.service.dto.SeatMapDTO;
import com.ridehub.route.service.mapper.SeatMapMapper;
import com.ridehub.route.service.vm.SeatMapDetailVM;

import jakarta.persistence.criteria.JoinType;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link SeatMap} entities in the
 * database.
 * The main input is a {@link SeatMapCriteria} which gets converted to
 * {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SeatMapDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SeatMapQueryService extends QueryService<SeatMap> {

    private static final Logger LOG = LoggerFactory.getLogger(SeatMapQueryService.class);

    private final SeatMapRepository seatMapRepository;

    private final SeatMapMapper seatMapMapper;

    private final FloorQueryService floorQueryService;

    private final SeatQueryService seatQueryService;

    public SeatMapQueryService(SeatMapRepository seatMapRepository, SeatMapMapper seatMapMapper,
            FloorQueryService floorQueryService, SeatQueryService seatQueryService) {
        this.seatMapRepository = seatMapRepository;
        this.seatMapMapper = seatMapMapper;
        this.floorQueryService = floorQueryService;
        this.seatQueryService = seatQueryService;
    }

    /**
     * Return a {@link List} of {@link SeatMapDTO} which matches the criteria from
     * the database.
     * 
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SeatMapDTO> findByCriteria(SeatMapCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<SeatMap> specification = createSpecification(criteria);
        return seatMapMapper.toDto(seatMapRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * 
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SeatMapCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<SeatMap> specification = createSpecification(criteria);
        return seatMapRepository.count(specification);
    }

    /**
     * Function to convert {@link SeatMapCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SeatMap> createSpecification(SeatMapCriteria criteria) {
        Specification<SeatMap> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), SeatMap_.id),
                buildStringSpecification(criteria.getName(), SeatMap_.name),
                buildRangeSpecification(criteria.getCreatedAt(), SeatMap_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), SeatMap_.updatedAt),
                buildSpecification(criteria.getIsDeleted(), SeatMap_.isDeleted),
                buildRangeSpecification(criteria.getDeletedAt(), SeatMap_.deletedAt),
                buildSpecification(criteria.getDeletedBy(), SeatMap_.deletedBy),
                buildSpecification(criteria.getSeatMapImgId(), root -> root.join(SeatMap_.seatMapImg, JoinType.LEFT).get(FileRoute_.id))
            );
        }
        return specification;
    }

    @Transactional(readOnly = true)
    public Optional<SeatMapDetailVM> findDetail(Long id) {
        return seatMapRepository.findById(id).map(seatMap -> {
            // Get floors for this seat map
            List<FloorDTO> floorDTOs = floorQueryService.findFloorsBySeatMapId(seatMap.getId());

            // seats grouped by floorId
            Map<Long, List<SeatDTO>> seatsByFloorId = Map.of();
            if (!floorDTOs.isEmpty()) {
                List<Long> floorIds = floorDTOs.stream()
                        .map(FloorDTO::getId)
                        .filter(Objects::nonNull)
                        .toList();

                if (!floorIds.isEmpty()) {
                    // Get seats for all floors
                    List<SeatDTO> seatDTOs = seatQueryService.findByFloorIds(floorIds);

                    seatsByFloorId = seatDTOs.stream()
                            .filter(s -> s.getFloor() != null && s.getFloor().getId() != null)
                            .collect(Collectors.groupingBy(
                                    s -> s.getFloor().getId(),
                                    Collectors.toList()));
                }
            }

            return new SeatMapDetailVM(floorDTOs, seatsByFloorId);
        });
    }
}
