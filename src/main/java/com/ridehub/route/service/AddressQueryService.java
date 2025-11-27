package com.ridehub.route.service;

import com.ridehub.route.domain.*; // for static metamodels
import com.ridehub.route.domain.Address;
import com.ridehub.route.repository.AddressRepository;
import com.ridehub.route.service.criteria.AddressCriteria;
import com.ridehub.route.service.dto.AddressDTO;
import com.ridehub.route.service.mapper.AddressMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Address} entities in the database.
 * The main input is a {@link AddressCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link AddressDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AddressQueryService extends QueryService<Address> {

    private static final Logger LOG = LoggerFactory.getLogger(AddressQueryService.class);

    private final AddressRepository addressRepository;

    private final AddressMapper addressMapper;

    public AddressQueryService(AddressRepository addressRepository, AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
    }

    /**
     * Return a {@link List} of {@link AddressDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<AddressDTO> findByCriteria(AddressCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<Address> specification = createSpecification(criteria);
        return addressMapper.toDto(addressRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AddressCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Address> specification = createSpecification(criteria);
        return addressRepository.count(specification);
    }

    /**
     * Function to convert {@link AddressCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Address> createSpecification(AddressCriteria criteria) {
        Specification<Address> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Address_.id),
                buildStringSpecification(criteria.getStreetAddress(), Address_.streetAddress),
                buildRangeSpecification(criteria.getLatitude(), Address_.latitude),
                buildRangeSpecification(criteria.getLongitude(), Address_.longitude),
                buildRangeSpecification(criteria.getCreatedAt(), Address_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), Address_.updatedAt),
                buildSpecification(criteria.getIsDeleted(), Address_.isDeleted),
                buildRangeSpecification(criteria.getDeletedAt(), Address_.deletedAt),
                buildSpecification(criteria.getDeletedBy(), Address_.deletedBy),
                buildSpecification(criteria.getStationId(), root -> root.join(Address_.station, JoinType.LEFT).get(Station_.id)),
                buildSpecification(criteria.getWardId(), root -> root.join(Address_.ward, JoinType.LEFT).get(Ward_.id))
            );
        }
        return specification;
    }
}
