package com.ridehub.route.web.rest;

import com.ridehub.route.repository.AttendantRepository;
import com.ridehub.route.service.AttendantQueryService;
import com.ridehub.route.service.AttendantService;
import com.ridehub.route.service.criteria.AttendantCriteria;
import com.ridehub.route.service.dto.AttendantDTO;
import com.ridehub.route.service.dto.request.SimpleAttendantRequestDTO;
import com.ridehub.route.service.dto.response.SimpleAttendantResponseDTO;
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
 * REST controller for managing {@link com.ridehub.route.domain.Attendant}.
 */
@RestController
@RequestMapping("/api/attendants")
public class AttendantResource {

    private static final Logger LOG = LoggerFactory.getLogger(AttendantResource.class);

    private static final String ENTITY_NAME = "msRouteAttendant";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AttendantService attendantService;

    private final AttendantRepository attendantRepository;

    private final AttendantQueryService attendantQueryService;

    public AttendantResource(
        AttendantService attendantService,
        AttendantRepository attendantRepository,
        AttendantQueryService attendantQueryService
    ) {
        this.attendantService = attendantService;
        this.attendantRepository = attendantRepository;
        this.attendantQueryService = attendantQueryService;
    }

    /**
     * {@code POST  /attendants} : Create a new attendant.
     *
     * @param attendantDTO the attendantDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new attendantDTO, or with status {@code 400 (Bad Request)} if the attendant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AttendantDTO> createAttendant(@Valid @RequestBody AttendantDTO attendantDTO) throws URISyntaxException {
        LOG.debug("REST request to save Attendant : {}", attendantDTO);
        if (attendantDTO.getId() != null) {
            throw new BadRequestAlertException("A new attendant cannot already have an ID", ENTITY_NAME, "idexists");
        }
        attendantDTO = attendantService.save(attendantDTO);
        return ResponseEntity.created(new URI("/api/attendants/" + attendantDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, attendantDTO.getId().toString()))
            .body(attendantDTO);
    }

    /**
     * {@code PUT  /attendants/:id} : Updates an existing attendant.
     *
     * @param id the id of the attendantDTO to save.
     * @param attendantDTO the attendantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated attendantDTO,
     * or with status {@code 400 (Bad Request)} if the attendantDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the attendantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AttendantDTO> updateAttendant(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AttendantDTO attendantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Attendant : {}, {}", id, attendantDTO);
        if (attendantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, attendantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!attendantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        attendantDTO = attendantService.update(attendantDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, attendantDTO.getId().toString()))
            .body(attendantDTO);
    }

    /**
     * {@code PATCH  /attendants/:id} : Partial updates given fields of an existing attendant, field will ignore if it is null
     *
     * @param id the id of the attendantDTO to save.
     * @param attendantDTO the attendantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated attendantDTO,
     * or with status {@code 400 (Bad Request)} if the attendantDTO is not valid,
     * or with status {@code 404 (Not Found)} if the attendantDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the attendantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AttendantDTO> partialUpdateAttendant(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AttendantDTO attendantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Attendant partially : {}, {}", id, attendantDTO);
        if (attendantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, attendantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!attendantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AttendantDTO> result = attendantService.partialUpdate(attendantDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, attendantDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /attendants} : get all the attendants.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of attendants in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AttendantDTO>> getAllAttendants(AttendantCriteria criteria) {
        LOG.debug("REST request to get Attendants by criteria: {}", criteria);

        List<AttendantDTO> entityList = attendantQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /attendants/count} : count all the attendants.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countAttendants(AttendantCriteria criteria) {
        LOG.debug("REST request to count Attendants by criteria: {}", criteria);
        return ResponseEntity.ok().body(attendantQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /attendants/:id} : get the "id" attendant.
     *
     * @param id the id of the attendantDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the attendantDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AttendantDTO> getAttendant(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Attendant : {}", id);
        Optional<AttendantDTO> attendantDTO = attendantService.findOne(id);
        return ResponseUtil.wrapOrNotFound(attendantDTO);
    }

    /**
     * {@code DELETE  /attendants/:id} : delete the "id" attendant.
     *
     * @param id the id of the attendantDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendant(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Attendant : {}", id);
        attendantService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code POST  /attendants/simple} : Create a new attendant with simplified input.
     * This endpoint automatically manages staff creation, so users don't need to handle staff details.
     *
     * @param requestDTO the simplified attendant data to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new attendant response.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/simple")
    public ResponseEntity<SimpleAttendantResponseDTO> createSimpleAttendant(@Valid @RequestBody SimpleAttendantRequestDTO requestDTO) throws URISyntaxException {
        LOG.debug("REST request to create simple Attendant : {}", requestDTO);

        SimpleAttendantResponseDTO result = attendantService.createSimpleAttendant(requestDTO);
        return ResponseEntity.created(new URI("/api/attendants/simple/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /attendants/simple/:id} : Update an existing attendant with simplified input.
     * This endpoint automatically manages staff updates, so users don't need to handle staff details.
     *
     * @param id the id of the attendant to update.
     * @param requestDTO the simplified attendant data to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated attendant response.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/simple/{id}")
    public ResponseEntity<SimpleAttendantResponseDTO> updateSimpleAttendant(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SimpleAttendantRequestDTO requestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update simple Attendant : {}, {}", id, requestDTO);

        SimpleAttendantResponseDTO result = attendantService.updateSimpleAttendant(id, requestDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /attendants/simple/:id} : Get an attendant by id with simplified response.
     *
     * @param id the id of the attendant to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the simplified attendant response, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/simple/{id}")
    public ResponseEntity<SimpleAttendantResponseDTO> getSimpleAttendant(@PathVariable("id") Long id) {
        LOG.debug("REST request to get simple Attendant : {}", id);
        Optional<SimpleAttendantResponseDTO> result = attendantService.findSimpleAttendantById(id);
        return ResponseUtil.wrapOrNotFound(result);
    }
}
