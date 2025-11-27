package com.ridehub.route.service;

import com.ridehub.route.domain.*; // for static metamodels
import com.ridehub.route.domain.FileRoute;
import com.ridehub.route.repository.FileRouteRepository;
import com.ridehub.route.service.criteria.FileRouteCriteria;
import com.ridehub.route.service.dto.FileRouteDTO;
import com.ridehub.route.service.mapper.FileRouteMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link FileRoute} entities in the database.
 * The main input is a {@link FileRouteCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link FileRouteDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FileRouteQueryService extends QueryService<FileRoute> {

    private static final Logger LOG = LoggerFactory.getLogger(FileRouteQueryService.class);

    private final FileRouteRepository fileRouteRepository;

    private final FileRouteMapper fileRouteMapper;

    public FileRouteQueryService(FileRouteRepository fileRouteRepository, FileRouteMapper fileRouteMapper) {
        this.fileRouteRepository = fileRouteRepository;
        this.fileRouteMapper = fileRouteMapper;
    }

    /**
     * Return a {@link List} of {@link FileRouteDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<FileRouteDTO> findByCriteria(FileRouteCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<FileRoute> specification = createSpecification(criteria);
        return fileRouteMapper.toDto(fileRouteRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FileRouteCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<FileRoute> specification = createSpecification(criteria);
        return fileRouteRepository.count(specification);
    }

    /**
     * Function to convert {@link FileRouteCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<FileRoute> createSpecification(FileRouteCriteria criteria) {
        Specification<FileRoute> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), FileRoute_.id),
                buildStringSpecification(criteria.getBucket(), FileRoute_.bucket),
                buildStringSpecification(criteria.getObjectKey(), FileRoute_.objectKey),
                buildStringSpecification(criteria.getContentType(), FileRoute_.contentType),
                buildRangeSpecification(criteria.getSize(), FileRoute_.size),
                buildRangeSpecification(criteria.getCreatedAt(), FileRoute_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), FileRoute_.updatedAt),
                buildSpecification(criteria.getIsDeleted(), FileRoute_.isDeleted),
                buildRangeSpecification(criteria.getDeletedAt(), FileRoute_.deletedAt),
                buildSpecification(criteria.getDeletedBy(), FileRoute_.deletedBy)
            );
        }
        return specification;
    }
}
