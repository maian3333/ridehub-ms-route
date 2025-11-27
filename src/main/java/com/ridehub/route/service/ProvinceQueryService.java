package com.ridehub.route.service;

import com.ridehub.route.domain.*; // for static metamodels
import com.ridehub.route.domain.Province;
import com.ridehub.route.repository.ProvinceRepository;
import com.ridehub.route.service.criteria.ProvinceCriteria;
import com.ridehub.route.service.dto.ProvinceDTO;
import com.ridehub.route.service.mapper.ProvinceMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Province} entities in the database.
 * The main input is a {@link ProvinceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProvinceDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProvinceQueryService extends QueryService<Province> {

    private static final Logger LOG = LoggerFactory.getLogger(ProvinceQueryService.class);

    private final ProvinceRepository provinceRepository;

    private final ProvinceMapper provinceMapper;

    public ProvinceQueryService(ProvinceRepository provinceRepository, ProvinceMapper provinceMapper) {
        this.provinceRepository = provinceRepository;
        this.provinceMapper = provinceMapper;
    }

    /**
     * Return a {@link List} of {@link ProvinceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProvinceDTO> findByCriteria(ProvinceCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<Province> specification = createSpecification(criteria);
        return provinceMapper.toDto(provinceRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProvinceCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Province> specification = createSpecification(criteria);
        return provinceRepository.count(specification);
    }

    /**
     * Function to convert {@link ProvinceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Province> createSpecification(ProvinceCriteria criteria) {
        Specification<Province> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Province_.id),
                buildStringSpecification(criteria.getProvinceCode(), Province_.provinceCode),
                buildStringSpecification(criteria.getName(), Province_.name),
                buildStringSpecification(criteria.getNameEn(), Province_.nameEn),
                buildStringSpecification(criteria.getFullName(), Province_.fullName),
                buildStringSpecification(criteria.getFullNameEn(), Province_.fullNameEn),
                buildStringSpecification(criteria.getCodeName(), Province_.codeName),
                buildRangeSpecification(criteria.getAdministrativeUnitId(), Province_.administrativeUnitId),
                buildRangeSpecification(criteria.getAdministrativeRegionId(), Province_.administrativeRegionId),
                buildRangeSpecification(criteria.getCreatedAt(), Province_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), Province_.updatedAt),
                buildSpecification(criteria.getIsDeleted(), Province_.isDeleted),
                buildRangeSpecification(criteria.getDeletedAt(), Province_.deletedAt),
                buildSpecification(criteria.getDeletedBy(), Province_.deletedBy),
                buildSpecification(criteria.getDistrictsId(), root -> root.join(Province_.districts, JoinType.LEFT).get(District_.id))
            );
        }
        return specification;
    }
}
