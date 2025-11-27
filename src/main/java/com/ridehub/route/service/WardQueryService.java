package com.ridehub.route.service;

import com.ridehub.route.domain.*; // for static metamodels
import com.ridehub.route.domain.Ward;
import com.ridehub.route.repository.WardRepository;
import com.ridehub.route.service.criteria.WardCriteria;
import com.ridehub.route.service.dto.WardDTO;
import com.ridehub.route.service.mapper.WardMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Ward} entities in the database.
 * The main input is a {@link WardCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link WardDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WardQueryService extends QueryService<Ward> {

    private static final Logger LOG = LoggerFactory.getLogger(WardQueryService.class);

    private final WardRepository wardRepository;

    private final WardMapper wardMapper;

    public WardQueryService(WardRepository wardRepository, WardMapper wardMapper) {
        this.wardRepository = wardRepository;
        this.wardMapper = wardMapper;
    }

    /**
     * Return a {@link Page} of {@link WardDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<WardDTO> findByCriteria(WardCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Ward> specification = createSpecification(criteria);
        return wardRepository.findAll(specification, page).map(wardMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(WardCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Ward> specification = createSpecification(criteria);
        return wardRepository.count(specification);
    }

    /**
     * Function to convert {@link WardCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Ward> createSpecification(WardCriteria criteria) {
        Specification<Ward> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Ward_.id),
                buildStringSpecification(criteria.getWardCode(), Ward_.wardCode),
                buildStringSpecification(criteria.getName(), Ward_.name),
                buildStringSpecification(criteria.getNameEn(), Ward_.nameEn),
                buildStringSpecification(criteria.getFullName(), Ward_.fullName),
                buildStringSpecification(criteria.getFullNameEn(), Ward_.fullNameEn),
                buildStringSpecification(criteria.getCodeName(), Ward_.codeName),
                buildRangeSpecification(criteria.getAdministrativeUnitId(), Ward_.administrativeUnitId),
                buildRangeSpecification(criteria.getCreatedAt(), Ward_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), Ward_.updatedAt),
                buildSpecification(criteria.getIsDeleted(), Ward_.isDeleted),
                buildRangeSpecification(criteria.getDeletedAt(), Ward_.deletedAt),
                buildSpecification(criteria.getDeletedBy(), Ward_.deletedBy),
                buildSpecification(criteria.getAddressesId(), root -> root.join(Ward_.addresses, JoinType.LEFT).get(Address_.id)),
                buildSpecification(criteria.getDistrictId(), root -> root.join(Ward_.district, JoinType.LEFT).get(District_.id))
            );
        }
        return specification;
    }
}
