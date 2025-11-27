package com.ridehub.route.web.rest;

import com.ridehub.route.repository.DriverRepository;
import com.ridehub.route.service.DriverQueryService;
import com.ridehub.route.service.DriverService;
import com.ridehub.route.service.criteria.DriverCriteria;
import com.ridehub.route.service.dto.DriverDTO;
import com.ridehub.route.service.dto.request.SimpleDriverRequestDTO;
import com.ridehub.route.service.dto.response.SimpleDriverResponseDTO;
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
 * REST controller for managing {@link com.ridehub.route.domain.Driver}.
 */
@RestController
@RequestMapping("/api/drivers")
public class DriverResource {

    private static final Logger LOG = LoggerFactory.getLogger(DriverResource.class);

    private static final String ENTITY_NAME = "msRouteDriver";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DriverService driverService;

    private final DriverRepository driverRepository;

    private final DriverQueryService driverQueryService;

    public DriverResource(DriverService driverService, DriverRepository driverRepository, DriverQueryService driverQueryService) {
        this.driverService = driverService;
        this.driverRepository = driverRepository;
        this.driverQueryService = driverQueryService;
    }

    /**
     * {@code POST  /drivers} : Create a new driver.
     *
     * @param driverDTO the driverDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new driverDTO, or with status {@code 400 (Bad Request)} if the driver has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DriverDTO> createDriver(@Valid @RequestBody DriverDTO driverDTO) throws URISyntaxException {
        LOG.debug("REST request to save Driver : {}", driverDTO);
        if (driverDTO.getId() != null) {
            throw new BadRequestAlertException("A new driver cannot already have an ID", ENTITY_NAME, "idexists");
        }
        driverDTO = driverService.save(driverDTO);
        return ResponseEntity.created(new URI("/api/drivers/" + driverDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, driverDTO.getId().toString()))
            .body(driverDTO);
    }

    /**
     * {@code PUT  /drivers/:id} : Updates an existing driver.
     *
     * @param id the id of the driverDTO to save.
     * @param driverDTO the driverDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated driverDTO,
     * or with status {@code 400 (Bad Request)} if the driverDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the driverDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DriverDTO> updateDriver(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DriverDTO driverDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Driver : {}, {}", id, driverDTO);
        if (driverDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, driverDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!driverRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        driverDTO = driverService.update(driverDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, driverDTO.getId().toString()))
            .body(driverDTO);
    }

    /**
     * {@code PATCH  /drivers/:id} : Partial updates given fields of an existing driver, field will ignore if it is null
     *
     * @param id the id of the driverDTO to save.
     * @param driverDTO the driverDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated driverDTO,
     * or with status {@code 400 (Bad Request)} if the driverDTO is not valid,
     * or with status {@code 404 (Not Found)} if the driverDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the driverDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DriverDTO> partialUpdateDriver(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DriverDTO driverDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Driver partially : {}, {}", id, driverDTO);
        if (driverDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, driverDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!driverRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DriverDTO> result = driverService.partialUpdate(driverDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, driverDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /drivers} : get all the drivers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of drivers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DriverDTO>> getAllDrivers(DriverCriteria criteria) {
        LOG.debug("REST request to get Drivers by criteria: {}", criteria);

        List<DriverDTO> entityList = driverQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /drivers/count} : count all the drivers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDrivers(DriverCriteria criteria) {
        LOG.debug("REST request to count Drivers by criteria: {}", criteria);
        return ResponseEntity.ok().body(driverQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /drivers/:id} : get the "id" driver.
     *
     * @param id the id of the driverDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the driverDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DriverDTO> getDriver(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Driver : {}", id);
        Optional<DriverDTO> driverDTO = driverService.findOne(id);
        return ResponseUtil.wrapOrNotFound(driverDTO);
    }

    /**
     * {@code DELETE  /drivers/:id} : delete the "id" driver.
     *
     * @param id the id of the driverDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Driver : {}", id);
        driverService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code POST  /drivers/simple} : Create a new driver with simplified input.
     * This endpoint automatically manages staff creation, so users don't need to handle staff details.
     *
     * @param requestDTO the simplified driver data to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new driver response.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/simple")
    public ResponseEntity<SimpleDriverResponseDTO> createSimpleDriver(@Valid @RequestBody SimpleDriverRequestDTO requestDTO) throws URISyntaxException {
        LOG.debug("REST request to create simple Driver : {}", requestDTO);

        SimpleDriverResponseDTO result = driverService.createSimpleDriver(requestDTO);
        return ResponseEntity.created(new URI("/api/drivers/simple/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /drivers/simple/:id} : Update an existing driver with simplified input.
     * This endpoint automatically manages staff updates, so users don't need to handle staff details.
     *
     * @param id the id of the driver to update.
     * @param requestDTO the simplified driver data to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated driver response.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/simple/{id}")
    public ResponseEntity<SimpleDriverResponseDTO> updateSimpleDriver(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SimpleDriverRequestDTO requestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update simple Driver : {}, {}", id, requestDTO);

        SimpleDriverResponseDTO result = driverService.updateSimpleDriver(id, requestDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /drivers/simple/:id} : Get a driver by id with simplified response.
     *
     * @param id the id of the driver to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the simplified driver response, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/simple/{id}")
    public ResponseEntity<SimpleDriverResponseDTO> getSimpleDriver(@PathVariable("id") Long id) {
        LOG.debug("REST request to get simple Driver : {}", id);
        Optional<SimpleDriverResponseDTO> result = driverService.findSimpleDriverById(id);
        return ResponseUtil.wrapOrNotFound(result);
    }
}
