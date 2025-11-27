package com.ridehub.route.web.rest;

import com.ridehub.route.repository.FileRouteRepository;
import com.ridehub.route.service.FileRouteQueryService;
import com.ridehub.route.service.FileRouteService;
import com.ridehub.route.service.criteria.FileRouteCriteria;
import com.ridehub.route.service.dto.FileRouteDTO;
import com.ridehub.route.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ridehub.route.domain.FileRoute}.
 */
@RestController
@RequestMapping("/api/file-routes")
public class FileRouteResource {

    private static final Logger LOG = LoggerFactory.getLogger(FileRouteResource.class);

    private static final String ENTITY_NAME = "msRouteFileRoute";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FileRouteService fileRouteService;

    private final FileRouteRepository fileRouteRepository;

    private final FileRouteQueryService fileRouteQueryService;

    public FileRouteResource(
        FileRouteService fileRouteService,
        FileRouteRepository fileRouteRepository,
        FileRouteQueryService fileRouteQueryService
    ) {
        this.fileRouteService = fileRouteService;
        this.fileRouteRepository = fileRouteRepository;
        this.fileRouteQueryService = fileRouteQueryService;
    }

    /**
     * {@code POST  /file-routes} : Create a new fileRoute.
     *
     * @param fileRouteDTO the fileRouteDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fileRouteDTO, or with status {@code 400 (Bad Request)} if the fileRoute has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FileRouteDTO> createFileRoute(@Valid @RequestBody FileRouteDTO fileRouteDTO) throws URISyntaxException {
        LOG.debug("REST request to save FileRoute : {}", fileRouteDTO);
        if (fileRouteDTO.getId() != null) {
            throw new BadRequestAlertException("A new fileRoute cannot already have an ID", ENTITY_NAME, "idexists");
        }
        fileRouteDTO = fileRouteService.save(fileRouteDTO);
        return ResponseEntity.created(new URI("/api/file-routes/" + fileRouteDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, fileRouteDTO.getId().toString()))
            .body(fileRouteDTO);
    }

    /**
     * {@code PUT  /file-routes/:id} : Updates an existing fileRoute.
     *
     * @param id the id of the fileRouteDTO to save.
     * @param fileRouteDTO the fileRouteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fileRouteDTO,
     * or with status {@code 400 (Bad Request)} if the fileRouteDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the fileRouteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FileRouteDTO> updateFileRoute(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FileRouteDTO fileRouteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update FileRoute : {}, {}", id, fileRouteDTO);
        if (fileRouteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fileRouteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fileRouteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        fileRouteDTO = fileRouteService.update(fileRouteDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, fileRouteDTO.getId().toString()))
            .body(fileRouteDTO);
    }

    /**
     * {@code PATCH  /file-routes/:id} : Partial updates given fields of an existing fileRoute, field will ignore if it is null
     *
     * @param id the id of the fileRouteDTO to save.
     * @param fileRouteDTO the fileRouteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fileRouteDTO,
     * or with status {@code 400 (Bad Request)} if the fileRouteDTO is not valid,
     * or with status {@code 404 (Not Found)} if the fileRouteDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the fileRouteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FileRouteDTO> partialUpdateFileRoute(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FileRouteDTO fileRouteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update FileRoute partially : {}, {}", id, fileRouteDTO);
        if (fileRouteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fileRouteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fileRouteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FileRouteDTO> result = fileRouteService.partialUpdate(fileRouteDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, fileRouteDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /file-routes} : get all the fileRoutes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fileRoutes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<FileRouteDTO>> getAllFileRoutes(FileRouteCriteria criteria) {
        LOG.debug("REST request to get FileRoutes by criteria: {}", criteria);

        List<FileRouteDTO> entityList = fileRouteQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /file-routes/count} : count all the fileRoutes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countFileRoutes(FileRouteCriteria criteria) {
        LOG.debug("REST request to count FileRoutes by criteria: {}", criteria);
        return ResponseEntity.ok().body(fileRouteQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /file-routes/:id} : get the "id" fileRoute.
     *
     * @param id the id of the fileRouteDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fileRouteDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FileRouteDTO> getFileRoute(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FileRoute : {}", id);
        Optional<FileRouteDTO> fileRouteDTO = fileRouteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(fileRouteDTO);
    }

    /**
     * {@code DELETE  /file-routes/:id} : delete the "id" fileRoute.
     *
     * @param id the id of the fileRouteDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFileRoute(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FileRoute : {}", id);
        fileRouteService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
