package com.ridehub.route.web.rest;

import com.ridehub.route.repository.StationRepository;
import com.ridehub.route.service.StationQueryService;
import com.ridehub.route.service.StationService;
import com.ridehub.route.service.criteria.StationCriteria;
import com.ridehub.route.service.dto.StationDTO;
import com.ridehub.route.service.vm.StationWithRoutesVM;
import com.ridehub.route.web.rest.errors.BadRequestAlertException;
import com.ridehub.route.web.rest.errors.ElasticsearchExceptionMapper;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ridehub.route.domain.Station}.
 */
@RestController
@RequestMapping("/api/stations")
public class StationResource {

    private static final Logger LOG = LoggerFactory.getLogger(StationResource.class);

    private static final String ENTITY_NAME = "msRouteStation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StationService stationService;

    private final StationRepository stationRepository;

    private final StationQueryService stationQueryService;

    public StationResource(StationService stationService, StationRepository stationRepository,
            StationQueryService stationQueryService) {
        this.stationService = stationService;
        this.stationRepository = stationRepository;
        this.stationQueryService = stationQueryService;
    }

    /**
     * {@code POST  /stations} : Create a new station.
     *
     * @param stationDTO the stationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new stationDTO, or with status {@code 400 (Bad Request)} if
     *         the station has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StationDTO> createStation(@Valid @RequestBody StationDTO stationDTO)
            throws URISyntaxException {
        LOG.debug("REST request to save Station : {}", stationDTO);
        if (stationDTO.getId() != null) {
            throw new BadRequestAlertException("A new station cannot already have an ID", ENTITY_NAME, "idexists");
        }
        stationDTO = stationService.save(stationDTO);
        return ResponseEntity.created(new URI("/api/stations/" + stationDTO.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME,
                        stationDTO.getId().toString()))
                .body(stationDTO);
    }

    /**
     * {@code PUT  /stations/:id} : Updates an existing station.
     *
     * @param id         the id of the stationDTO to save.
     * @param stationDTO the stationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated stationDTO,
     *         or with status {@code 400 (Bad Request)} if the stationDTO is not
     *         valid,
     *         or with status {@code 500 (Internal Server Error)} if the stationDTO
     *         couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StationDTO> updateStation(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody StationDTO stationDTO) throws URISyntaxException {
        LOG.debug("REST request to update Station : {}, {}", id, stationDTO);
        if (stationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        stationDTO = stationService.update(stationDTO);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
                        stationDTO.getId().toString()))
                .body(stationDTO);
    }

    /**
     * {@code PATCH  /stations/:id} : Partial updates given fields of an existing
     * station, field will ignore if it is null
     *
     * @param id         the id of the stationDTO to save.
     * @param stationDTO the stationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated stationDTO,
     *         or with status {@code 400 (Bad Request)} if the stationDTO is not
     *         valid,
     *         or with status {@code 404 (Not Found)} if the stationDTO is not
     *         found,
     *         or with status {@code 500 (Internal Server Error)} if the stationDTO
     *         couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StationDTO> partialUpdateStation(
            @PathVariable(value = "id", required = false) final Long id,
            @NotNull @RequestBody StationDTO stationDTO) throws URISyntaxException {
        LOG.debug("REST request to partial update Station partially : {}, {}", id, stationDTO);
        if (stationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StationDTO> result = stationService.partialUpdate(stationDTO);

        return ResponseUtil.wrapOrNotFound(
                result,
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stationDTO.getId().toString()));
    }

    /**
     * {@code GET  /stations} : get all the stations.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of stations in body.
     */
    @GetMapping("")
    public ResponseEntity<List<StationDTO>> getAllStations(
            StationCriteria criteria,
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get Stations by criteria: {}", criteria);

        Page<StationDTO> page = stationQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil
                .generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /stations/count} : count all the stations.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count
     *         in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countStations(StationCriteria criteria) {
        LOG.debug("REST request to count Stations by criteria: {}", criteria);
        return ResponseEntity.ok().body(stationQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /stations/:id} : get the "id" station.
     *
     * @param id the id of the stationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the stationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StationDTO> getStation(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Station : {}", id);
        Optional<StationDTO> stationDTO = stationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stationDTO);
    }

    /**
     * {@code DELETE  /stations/:id} : delete the "id" station.
     *
     * @param id the id of the stationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Station : {}", id);
        stationService.delete(id);
        return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
    }

    /**
     * {@code SEARCH  /stations/_search?query=:query} : search for the station
     * corresponding
     * to the query.
     *
     * @param query    the query of the station search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<StationDTO>> searchStations(
            @RequestParam("query") String query,
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to search for a page of Stations for query {}", query);
        try {
            Page<StationDTO> page = stationService.search(query, pageable);
            HttpHeaders headers = PaginationUtil
                    .generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }

    /**
     * {@code GET  /stations/with-routes} : get all stations with their associated
     * routes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of stations with routes in body.
     */
    @GetMapping("/with-routes")
    public ResponseEntity<List<StationWithRoutesVM>> getStationsWithRoutes(
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get all stations with routes");
        Page<StationWithRoutesVM> page = stationService.getStationsWithRoutes(pageable);
        HttpHeaders headers = PaginationUtil
                .generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /stations/:id/with-routes} : get the station with all related
     * routes.
     *
     * @param id the ID of the station to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and body
     *         containing the station
     *         with its routes (as origin or destination), or with status
     *         {@code 404 (Not Found)} if not found.
     */
    @GetMapping("/{id}/with-routes")
    public ResponseEntity<StationWithRoutesVM> getStationsWithRoutesAndId(@PathVariable("id") Long id,
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get Station with routes: id={}, page={}, size={}, sort={}",
                id,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());
        Optional<StationWithRoutesVM> stationDTO = stationService.getStationsWithRoutesAndId(id, pageable);
        return ResponseUtil.wrapOrNotFound(stationDTO);
    }
}
