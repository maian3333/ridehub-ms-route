package com.ridehub.route.web.rest;

import com.ridehub.route.repository.FloorRepository;
import com.ridehub.route.service.FloorQueryService;
import com.ridehub.route.service.FloorService;
import com.ridehub.route.service.criteria.FloorCriteria;
import com.ridehub.route.service.dto.FloorDTO;
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
 * REST controller for managing {@link com.ridehub.route.domain.Floor}.
 */
@RestController
@RequestMapping("/api/floors")
public class FloorResource {

    private static final Logger LOG = LoggerFactory.getLogger(FloorResource.class);

    private static final String ENTITY_NAME = "msRouteFloor";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FloorService floorService;

    private final FloorRepository floorRepository;

    private final FloorQueryService floorQueryService;

    public FloorResource(FloorService floorService, FloorRepository floorRepository, FloorQueryService floorQueryService) {
        this.floorService = floorService;
        this.floorRepository = floorRepository;
        this.floorQueryService = floorQueryService;
    }

    /**
     * {@code POST  /floors} : Create a new floor.
     *
     * @param floorDTO the floorDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new floorDTO, or with status {@code 400 (Bad Request)} if the floor has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FloorDTO> createFloor(@Valid @RequestBody FloorDTO floorDTO) throws URISyntaxException {
        LOG.debug("REST request to save Floor : {}", floorDTO);
        if (floorDTO.getId() != null) {
            throw new BadRequestAlertException("A new floor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        floorDTO = floorService.save(floorDTO);
        return ResponseEntity.created(new URI("/api/floors/" + floorDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, floorDTO.getId().toString()))
            .body(floorDTO);
    }

    /**
     * {@code PUT  /floors/:id} : Updates an existing floor.
     *
     * @param id the id of the floorDTO to save.
     * @param floorDTO the floorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated floorDTO,
     * or with status {@code 400 (Bad Request)} if the floorDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the floorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FloorDTO> updateFloor(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FloorDTO floorDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Floor : {}, {}", id, floorDTO);
        if (floorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, floorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!floorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        floorDTO = floorService.update(floorDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, floorDTO.getId().toString()))
            .body(floorDTO);
    }

    /**
     * {@code PATCH  /floors/:id} : Partial updates given fields of an existing floor, field will ignore if it is null
     *
     * @param id the id of the floorDTO to save.
     * @param floorDTO the floorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated floorDTO,
     * or with status {@code 400 (Bad Request)} if the floorDTO is not valid,
     * or with status {@code 404 (Not Found)} if the floorDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the floorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FloorDTO> partialUpdateFloor(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FloorDTO floorDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Floor partially : {}, {}", id, floorDTO);
        if (floorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, floorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!floorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FloorDTO> result = floorService.partialUpdate(floorDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, floorDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /floors} : get all the floors.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of floors in body.
     */
    @GetMapping("")
    public ResponseEntity<List<FloorDTO>> getAllFloors(FloorCriteria criteria) {
        LOG.debug("REST request to get Floors by criteria: {}", criteria);

        List<FloorDTO> entityList = floorQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /floors/count} : count all the floors.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countFloors(FloorCriteria criteria) {
        LOG.debug("REST request to count Floors by criteria: {}", criteria);
        return ResponseEntity.ok().body(floorQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /floors/:id} : get the "id" floor.
     *
     * @param id the id of the floorDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the floorDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FloorDTO> getFloor(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Floor : {}", id);
        Optional<FloorDTO> floorDTO = floorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(floorDTO);
    }

    /**
     * {@code DELETE  /floors/:id} : delete the "id" floor.
     *
     * @param id the id of the floorDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFloor(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Floor : {}", id);
        floorService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
