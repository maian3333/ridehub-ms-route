package com.ridehub.route.web.rest;

import com.ridehub.route.repository.RouteRepository;
import com.ridehub.route.service.RouteQueryService;
import com.ridehub.route.service.RouteService;
import com.ridehub.route.service.criteria.RouteCriteria;
import com.ridehub.route.service.dto.RouteDTO;
import com.ridehub.route.service.dto.request.RouteStationRequestDTO;
import com.ridehub.route.service.dto.response.RouteStationResponseDTO;
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
 * REST controller for managing {@link com.ridehub.route.domain.Route}.
 */
@RestController
@RequestMapping("/api/routes")
public class RouteResource {

    private static final Logger LOG = LoggerFactory.getLogger(RouteResource.class);

    private static final String ENTITY_NAME = "msRouteRoute";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RouteService routeService;

    private final RouteRepository routeRepository;

    private final RouteQueryService routeQueryService;

    public RouteResource(RouteService routeService, RouteRepository routeRepository, RouteQueryService routeQueryService) {
        this.routeService = routeService;
        this.routeRepository = routeRepository;
        this.routeQueryService = routeQueryService;
    }

    /**
     * {@code POST  /routes} : Create a new route.
     *
     * @param routeDTO the routeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new routeDTO, or with status {@code 400 (Bad Request)} if the route has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RouteDTO> createRoute(@Valid @RequestBody RouteDTO routeDTO) throws URISyntaxException {
        LOG.debug("REST request to save Route : {}", routeDTO);
        if (routeDTO.getId() != null) {
            throw new BadRequestAlertException("A new route cannot already have an ID", ENTITY_NAME, "idexists");
        }
        routeDTO = routeService.save(routeDTO);
        return ResponseEntity.created(new URI("/api/routes/" + routeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, routeDTO.getId().toString()))
            .body(routeDTO);
    }

    /**
     * {@code PUT  /routes/:id} : Updates an existing route.
     *
     * @param id the id of the routeDTO to save.
     * @param routeDTO the routeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated routeDTO,
     * or with status {@code 400 (Bad Request)} if the routeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the routeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RouteDTO> updateRoute(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RouteDTO routeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Route : {}, {}", id, routeDTO);
        if (routeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, routeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!routeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        routeDTO = routeService.update(routeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, routeDTO.getId().toString()))
            .body(routeDTO);
    }

    /**
     * {@code PATCH  /routes/:id} : Partial updates given fields of an existing route, field will ignore if it is null
     *
     * @param id the id of the routeDTO to save.
     * @param routeDTO the routeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated routeDTO,
     * or with status {@code 400 (Bad Request)} if the routeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the routeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the routeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RouteDTO> partialUpdateRoute(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RouteDTO routeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Route partially : {}, {}", id, routeDTO);
        if (routeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, routeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!routeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RouteDTO> result = routeService.partialUpdate(routeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, routeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /routes} : get all the routes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of routes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<RouteDTO>> getAllRoutes(
        RouteCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Routes by criteria: {}", criteria);

        Page<RouteDTO> page = routeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /routes/count} : count all the routes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countRoutes(RouteCriteria criteria) {
        LOG.debug("REST request to count Routes by criteria: {}", criteria);
        return ResponseEntity.ok().body(routeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /routes/:id} : get the "id" route.
     *
     * @param id the id of the routeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the routeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RouteDTO> getRoute(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Route : {}", id);
        Optional<RouteDTO> routeDTO = routeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(routeDTO);
    }

    /**
     * {@code DELETE  /routes/:id} : delete the "id" route.
     *
     * @param id the id of the routeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Route : {}", id);
        routeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /routes/_search?query=:query} : search for the route corresponding
     * to the query.
     *
     * @param query the query of the route search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<RouteDTO>> searchRoutes(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Routes for query {}", query);
        try {
            Page<RouteDTO> page = routeService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }

    /**
     * {@code POST  /routes/station} : Create a new route based on station ID with flag-based logic.
     *
     * @param requestDTO the route station request containing station ID and flag.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new route response, or with status {@code 400 (Bad Request)} if the request is invalid.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/station")
    public ResponseEntity<RouteStationResponseDTO> createRouteWithStation(@Valid @RequestBody RouteStationRequestDTO requestDTO) throws URISyntaxException {
        LOG.debug("REST request to create Route with station : {}", requestDTO);

        RouteStationResponseDTO result = routeService.createRouteWithStation(requestDTO);
        return ResponseEntity.created(new URI("/api/routes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /routes/{id}/station} : Update an existing route based on station ID with flag-based logic.
     *
     * @param id the id of the route to update.
     * @param requestDTO the route station request containing station ID and flag.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated route response,
     * or with status {@code 400 (Bad Request)} if the request is invalid,
     * or with status {@code 404 (Not Found)} if the route is not found.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/station/{id}")
    public ResponseEntity<RouteStationResponseDTO> updateRouteWithStation(
        @PathVariable("id") Long id,
        @Valid @RequestBody RouteStationRequestDTO requestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Route {} with station : {}", id, requestDTO);

        RouteStationResponseDTO result = routeService.updateRouteWithStation(id, requestDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }
}
