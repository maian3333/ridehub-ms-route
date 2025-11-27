package com.ridehub.route.service;

import com.ridehub.route.domain.*; // for static metamodels
import com.ridehub.route.domain.Staff;
import com.ridehub.route.repository.StaffRepository;
import com.ridehub.route.service.criteria.StaffCriteria;
import com.ridehub.route.service.dto.StaffDTO;
import com.ridehub.route.service.mapper.StaffMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Staff} entities in the database.
 * The main input is a {@link StaffCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link StaffDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StaffQueryService extends QueryService<Staff> {

    private static final Logger LOG = LoggerFactory.getLogger(StaffQueryService.class);

    private final StaffRepository staffRepository;

    private final StaffMapper staffMapper;

    public StaffQueryService(StaffRepository staffRepository, StaffMapper staffMapper) {
        this.staffRepository = staffRepository;
        this.staffMapper = staffMapper;
    }

    /**
     * Return a {@link List} of {@link StaffDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<StaffDTO> findByCriteria(StaffCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<Staff> specification = createSpecification(criteria);
        return staffMapper.toDto(staffRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StaffCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Staff> specification = createSpecification(criteria);
        return staffRepository.count(specification);
    }

    /**
     * Function to convert {@link StaffCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Staff> createSpecification(StaffCriteria criteria) {
        Specification<Staff> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Staff_.id),
                buildStringSpecification(criteria.getName(), Staff_.name),
                buildRangeSpecification(criteria.getAge(), Staff_.age),
                buildSpecification(criteria.getGender(), Staff_.gender),
                buildStringSpecification(criteria.getPhoneNumber(), Staff_.phoneNumber),
                buildSpecification(criteria.getStatus(), Staff_.status),
                buildRangeSpecification(criteria.getCreatedAt(), Staff_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), Staff_.updatedAt),
                buildSpecification(criteria.getIsDeleted(), Staff_.isDeleted),
                buildRangeSpecification(criteria.getDeletedAt(), Staff_.deletedAt),
                buildSpecification(criteria.getDeletedBy(), Staff_.deletedBy),
                buildSpecification(criteria.getDriverId(), root -> root.join(Staff_.driver, JoinType.LEFT).get(Driver_.id)),
                buildSpecification(criteria.getAttendantId(), root -> root.join(Staff_.attendant, JoinType.LEFT).get(Attendant_.id))
            );
        }
        return specification;
    }
}
