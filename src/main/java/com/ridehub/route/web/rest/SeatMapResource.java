package com.ridehub.route.web.rest;

import com.ridehub.route.repository.SeatMapRepository;
import com.ridehub.route.service.SeatMapQueryService;
import com.ridehub.route.service.SeatMapService;
import com.ridehub.route.service.criteria.SeatMapCriteria;
import com.ridehub.route.service.dto.SeatMapDTO;
import com.ridehub.route.service.vm.SeatMapDetailVM;
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
 * REST controller for managing {@link com.ridehub.route.domain.SeatMap}.
 */
@RestController
@RequestMapping("/api/seat-maps")
public class SeatMapResource {

    private static final Logger LOG = LoggerFactory.getLogger(SeatMapResource.class);

    private static final String ENTITY_NAME = "msRouteSeatMap";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SeatMapService seatMapService;

    private final SeatMapRepository seatMapRepository;

    private final SeatMapQueryService seatMapQueryService;

    public SeatMapResource(SeatMapService seatMapService, SeatMapRepository seatMapRepository, SeatMapQueryService seatMapQueryService) {
        this.seatMapService = seatMapService;
        this.seatMapRepository = seatMapRepository;
        this.seatMapQueryService = seatMapQueryService;
    }

    /**
     * {@code POST  /seat-maps} : Create a new seatMap.
     *
     * @param seatMapDTO the seatMapDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new seatMapDTO, or with status {@code 400 (Bad Request)} if the seatMap has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SeatMapDTO> createSeatMap(@Valid @RequestBody SeatMapDTO seatMapDTO) throws URISyntaxException {
        LOG.debug("REST request to save SeatMap : {}", seatMapDTO);
        if (seatMapDTO.getId() != null) {
            throw new BadRequestAlertException("A new seatMap cannot already have an ID", ENTITY_NAME, "idexists");
        }
        seatMapDTO = seatMapService.save(seatMapDTO);
        return ResponseEntity.created(new URI("/api/seat-maps/" + seatMapDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, seatMapDTO.getId().toString()))
            .body(seatMapDTO);
    }

    /**
     * {@code PUT  /seat-maps/:id} : Updates an existing seatMap.
     *
     * @param id the id of the seatMapDTO to save.
     * @param seatMapDTO the seatMapDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated seatMapDTO,
     * or with status {@code 400 (Bad Request)} if the seatMapDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the seatMapDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SeatMapDTO> updateSeatMap(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SeatMapDTO seatMapDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SeatMap : {}, {}", id, seatMapDTO);
        if (seatMapDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, seatMapDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!seatMapRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        seatMapDTO = seatMapService.update(seatMapDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, seatMapDTO.getId().toString()))
            .body(seatMapDTO);
    }

    /**
     * {@code PATCH  /seat-maps/:id} : Partial updates given fields of an existing seatMap, field will ignore if it is null
     *
     * @param id the id of the seatMapDTO to save.
     * @param seatMapDTO the seatMapDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated seatMapDTO,
     * or with status {@code 400 (Bad Request)} if the seatMapDTO is not valid,
     * or with status {@code 404 (Not Found)} if the seatMapDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the seatMapDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SeatMapDTO> partialUpdateSeatMap(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SeatMapDTO seatMapDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SeatMap partially : {}, {}", id, seatMapDTO);
        if (seatMapDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, seatMapDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!seatMapRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SeatMapDTO> result = seatMapService.partialUpdate(seatMapDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, seatMapDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /seat-maps} : get all the seatMaps.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of seatMaps in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SeatMapDTO>> getAllSeatMaps(SeatMapCriteria criteria) {
        LOG.debug("REST request to get SeatMaps by criteria: {}", criteria);

        List<SeatMapDTO> entityList = seatMapQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /seat-maps/count} : count all the seatMaps.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countSeatMaps(SeatMapCriteria criteria) {
        LOG.debug("REST request to count SeatMaps by criteria: {}", criteria);
        return ResponseEntity.ok().body(seatMapQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /seat-maps/:id} : get the "id" seatMap.
     *
     * @param id the id of the seatMapDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the seatMapDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SeatMapDTO> getSeatMap(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SeatMap : {}", id);
        Optional<SeatMapDTO> seatMapDTO = seatMapService.findOne(id);
        return ResponseUtil.wrapOrNotFound(seatMapDTO);
    }

    /**
     * {@code DELETE  /seat-maps/:id} : delete the "id" seatMap.
     *
     * @param id the id of the seatMapDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeatMap(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SeatMap : {}", id);
        seatMapService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code GET  /seat-maps/:id/detail} : get the seat map detail with floors and seats.
     *
     * @param id the id of the seat map to retrieve detail for.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the SeatMapDetailVM, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}/detail")
    public ResponseEntity<SeatMapDetailVM> getSeatMapDetail(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SeatMap Detail : {}", id);
        Optional<SeatMapDetailVM> seatMapDetailVM = seatMapQueryService.findDetail(id);
        return ResponseUtil.wrapOrNotFound(seatMapDetailVM);
    }


}
