package com.ridehub.route.service;

import com.ridehub.route.domain.*; // for static metamodels
import com.ridehub.route.domain.District;
import com.ridehub.route.repository.DistrictRepository;
import com.ridehub.route.service.criteria.DistrictCriteria;
import com.ridehub.route.service.dto.DistrictDTO;
import com.ridehub.route.service.mapper.DistrictMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link District} entities in the database.
 * The main input is a {@link DistrictCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link DistrictDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DistrictQueryService extends QueryService<District> {

    private static final Logger LOG = LoggerFactory.getLogger(DistrictQueryService.class);

    private final DistrictRepository districtRepository;

    private final DistrictMapper districtMapper;

    public DistrictQueryService(DistrictRepository districtRepository, DistrictMapper districtMapper) {
        this.districtRepository = districtRepository;
        this.districtMapper = districtMapper;
    }

    /**
     * Return a {@link List} of {@link DistrictDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<DistrictDTO> findByCriteria(DistrictCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<District> specification = createSpecification(criteria);
        return districtMapper.toDto(districtRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DistrictCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<District> specification = createSpecification(criteria);
        return districtRepository.count(specification);
    }

    /**
     * Function to convert {@link DistrictCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<District> createSpecification(DistrictCriteria criteria) {
        Specification<District> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), District_.id),
                buildStringSpecification(criteria.getDistrictCode(), District_.districtCode),
                buildStringSpecification(criteria.getName(), District_.name),
                buildStringSpecification(criteria.getNameEn(), District_.nameEn),
                buildStringSpecification(criteria.getFullName(), District_.fullName),
                buildStringSpecification(criteria.getFullNameEn(), District_.fullNameEn),
                buildStringSpecification(criteria.getCodeName(), District_.codeName),
                buildRangeSpecification(criteria.getAdministrativeUnitId(), District_.administrativeUnitId),
                buildRangeSpecification(criteria.getCreatedAt(), District_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), District_.updatedAt),
                buildSpecification(criteria.getIsDeleted(), District_.isDeleted),
                buildRangeSpecification(criteria.getDeletedAt(), District_.deletedAt),
                buildSpecification(criteria.getDeletedBy(), District_.deletedBy),
                buildSpecification(criteria.getWardsId(), root -> root.join(District_.wards, JoinType.LEFT).get(Ward_.id)),
                buildSpecification(criteria.getProvinceId(), root -> root.join(District_.province, JoinType.LEFT).get(Province_.id))
            );
        }
        return specification;
    }
}
