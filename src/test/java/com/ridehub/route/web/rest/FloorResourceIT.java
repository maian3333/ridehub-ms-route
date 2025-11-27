package com.ridehub.route.web.rest;

import static com.ridehub.route.domain.FloorAsserts.*;
import static com.ridehub.route.web.rest.TestUtil.createUpdateProxyForBean;
import static com.ridehub.route.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.route.IntegrationTest;
import com.ridehub.route.domain.Floor;
import com.ridehub.route.domain.SeatMap;
import com.ridehub.route.repository.FloorRepository;
import com.ridehub.route.service.dto.FloorDTO;
import com.ridehub.route.service.mapper.FloorMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link FloorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FloorResourceIT {

    private static final Integer DEFAULT_FLOOR_NO = 1;
    private static final Integer UPDATED_FLOOR_NO = 2;
    private static final Integer SMALLER_FLOOR_NO = 1 - 1;

    private static final BigDecimal DEFAULT_PRICE_FACTOR_FLOOR = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE_FACTOR_FLOOR = new BigDecimal(2);
    private static final BigDecimal SMALLER_PRICE_FACTOR_FLOOR = new BigDecimal(1 - 1);

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_IS_DELETED = false;
    private static final Boolean UPDATED_IS_DELETED = true;

    private static final Instant DEFAULT_DELETED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DELETED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final UUID DEFAULT_DELETED_BY = UUID.randomUUID();
    private static final UUID UPDATED_DELETED_BY = UUID.randomUUID();

    private static final String ENTITY_API_URL = "/api/floors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private FloorMapper floorMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFloorMockMvc;

    private Floor floor;

    private Floor insertedFloor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Floor createEntity(EntityManager em) {
        Floor floor = new Floor()
            .floorNo(DEFAULT_FLOOR_NO)
            .priceFactorFloor(DEFAULT_PRICE_FACTOR_FLOOR)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .isDeleted(DEFAULT_IS_DELETED)
            .deletedAt(DEFAULT_DELETED_AT)
            .deletedBy(DEFAULT_DELETED_BY);
        // Add required entity
        SeatMap seatMap;
        if (TestUtil.findAll(em, SeatMap.class).isEmpty()) {
            seatMap = SeatMapResourceIT.createEntity();
            em.persist(seatMap);
            em.flush();
        } else {
            seatMap = TestUtil.findAll(em, SeatMap.class).get(0);
        }
        floor.setSeatMap(seatMap);
        return floor;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Floor createUpdatedEntity(EntityManager em) {
        Floor updatedFloor = new Floor()
            .floorNo(UPDATED_FLOOR_NO)
            .priceFactorFloor(UPDATED_PRICE_FACTOR_FLOOR)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        // Add required entity
        SeatMap seatMap;
        if (TestUtil.findAll(em, SeatMap.class).isEmpty()) {
            seatMap = SeatMapResourceIT.createUpdatedEntity();
            em.persist(seatMap);
            em.flush();
        } else {
            seatMap = TestUtil.findAll(em, SeatMap.class).get(0);
        }
        updatedFloor.setSeatMap(seatMap);
        return updatedFloor;
    }

    @BeforeEach
    void initTest() {
        floor = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedFloor != null) {
            floorRepository.delete(insertedFloor);
            insertedFloor = null;
        }
    }

    @Test
    @Transactional
    void createFloor() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Floor
        FloorDTO floorDTO = floorMapper.toDto(floor);
        var returnedFloorDTO = om.readValue(
            restFloorMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(floorDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FloorDTO.class
        );

        // Validate the Floor in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFloor = floorMapper.toEntity(returnedFloorDTO);
        assertFloorUpdatableFieldsEquals(returnedFloor, getPersistedFloor(returnedFloor));

        insertedFloor = returnedFloor;
    }

    @Test
    @Transactional
    void createFloorWithExistingId() throws Exception {
        // Create the Floor with an existing ID
        floor.setId(1L);
        FloorDTO floorDTO = floorMapper.toDto(floor);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFloorMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(floorDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Floor in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFloorNoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        floor.setFloorNo(null);

        // Create the Floor, which fails.
        FloorDTO floorDTO = floorMapper.toDto(floor);

        restFloorMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(floorDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        floor.setCreatedAt(null);

        // Create the Floor, which fails.
        FloorDTO floorDTO = floorMapper.toDto(floor);

        restFloorMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(floorDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFloors() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList
        restFloorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(floor.getId().intValue())))
            .andExpect(jsonPath("$.[*].floorNo").value(hasItem(DEFAULT_FLOOR_NO)))
            .andExpect(jsonPath("$.[*].priceFactorFloor").value(hasItem(sameNumber(DEFAULT_PRICE_FACTOR_FLOOR))))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));
    }

    @Test
    @Transactional
    void getFloor() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get the floor
        restFloorMockMvc
            .perform(get(ENTITY_API_URL_ID, floor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(floor.getId().intValue()))
            .andExpect(jsonPath("$.floorNo").value(DEFAULT_FLOOR_NO))
            .andExpect(jsonPath("$.priceFactorFloor").value(sameNumber(DEFAULT_PRICE_FACTOR_FLOOR)))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()))
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY.toString()));
    }

    @Test
    @Transactional
    void getFloorsByIdFiltering() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        Long id = floor.getId();

        defaultFloorFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultFloorFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultFloorFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFloorsByFloorNoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where floorNo equals to
        defaultFloorFiltering("floorNo.equals=" + DEFAULT_FLOOR_NO, "floorNo.equals=" + UPDATED_FLOOR_NO);
    }

    @Test
    @Transactional
    void getAllFloorsByFloorNoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where floorNo in
        defaultFloorFiltering("floorNo.in=" + DEFAULT_FLOOR_NO + "," + UPDATED_FLOOR_NO, "floorNo.in=" + UPDATED_FLOOR_NO);
    }

    @Test
    @Transactional
    void getAllFloorsByFloorNoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where floorNo is not null
        defaultFloorFiltering("floorNo.specified=true", "floorNo.specified=false");
    }

    @Test
    @Transactional
    void getAllFloorsByFloorNoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where floorNo is greater than or equal to
        defaultFloorFiltering("floorNo.greaterThanOrEqual=" + DEFAULT_FLOOR_NO, "floorNo.greaterThanOrEqual=" + UPDATED_FLOOR_NO);
    }

    @Test
    @Transactional
    void getAllFloorsByFloorNoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where floorNo is less than or equal to
        defaultFloorFiltering("floorNo.lessThanOrEqual=" + DEFAULT_FLOOR_NO, "floorNo.lessThanOrEqual=" + SMALLER_FLOOR_NO);
    }

    @Test
    @Transactional
    void getAllFloorsByFloorNoIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where floorNo is less than
        defaultFloorFiltering("floorNo.lessThan=" + UPDATED_FLOOR_NO, "floorNo.lessThan=" + DEFAULT_FLOOR_NO);
    }

    @Test
    @Transactional
    void getAllFloorsByFloorNoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where floorNo is greater than
        defaultFloorFiltering("floorNo.greaterThan=" + SMALLER_FLOOR_NO, "floorNo.greaterThan=" + DEFAULT_FLOOR_NO);
    }

    @Test
    @Transactional
    void getAllFloorsByPriceFactorFloorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where priceFactorFloor equals to
        defaultFloorFiltering(
            "priceFactorFloor.equals=" + DEFAULT_PRICE_FACTOR_FLOOR,
            "priceFactorFloor.equals=" + UPDATED_PRICE_FACTOR_FLOOR
        );
    }

    @Test
    @Transactional
    void getAllFloorsByPriceFactorFloorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where priceFactorFloor in
        defaultFloorFiltering(
            "priceFactorFloor.in=" + DEFAULT_PRICE_FACTOR_FLOOR + "," + UPDATED_PRICE_FACTOR_FLOOR,
            "priceFactorFloor.in=" + UPDATED_PRICE_FACTOR_FLOOR
        );
    }

    @Test
    @Transactional
    void getAllFloorsByPriceFactorFloorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where priceFactorFloor is not null
        defaultFloorFiltering("priceFactorFloor.specified=true", "priceFactorFloor.specified=false");
    }

    @Test
    @Transactional
    void getAllFloorsByPriceFactorFloorIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where priceFactorFloor is greater than or equal to
        defaultFloorFiltering(
            "priceFactorFloor.greaterThanOrEqual=" + DEFAULT_PRICE_FACTOR_FLOOR,
            "priceFactorFloor.greaterThanOrEqual=" + UPDATED_PRICE_FACTOR_FLOOR
        );
    }

    @Test
    @Transactional
    void getAllFloorsByPriceFactorFloorIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where priceFactorFloor is less than or equal to
        defaultFloorFiltering(
            "priceFactorFloor.lessThanOrEqual=" + DEFAULT_PRICE_FACTOR_FLOOR,
            "priceFactorFloor.lessThanOrEqual=" + SMALLER_PRICE_FACTOR_FLOOR
        );
    }

    @Test
    @Transactional
    void getAllFloorsByPriceFactorFloorIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where priceFactorFloor is less than
        defaultFloorFiltering(
            "priceFactorFloor.lessThan=" + UPDATED_PRICE_FACTOR_FLOOR,
            "priceFactorFloor.lessThan=" + DEFAULT_PRICE_FACTOR_FLOOR
        );
    }

    @Test
    @Transactional
    void getAllFloorsByPriceFactorFloorIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where priceFactorFloor is greater than
        defaultFloorFiltering(
            "priceFactorFloor.greaterThan=" + SMALLER_PRICE_FACTOR_FLOOR,
            "priceFactorFloor.greaterThan=" + DEFAULT_PRICE_FACTOR_FLOOR
        );
    }

    @Test
    @Transactional
    void getAllFloorsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where createdAt equals to
        defaultFloorFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllFloorsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where createdAt in
        defaultFloorFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllFloorsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where createdAt is not null
        defaultFloorFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllFloorsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where updatedAt equals to
        defaultFloorFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllFloorsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where updatedAt in
        defaultFloorFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllFloorsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where updatedAt is not null
        defaultFloorFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllFloorsByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where isDeleted equals to
        defaultFloorFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllFloorsByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where isDeleted in
        defaultFloorFiltering("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED, "isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllFloorsByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where isDeleted is not null
        defaultFloorFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllFloorsByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where deletedAt equals to
        defaultFloorFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllFloorsByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where deletedAt in
        defaultFloorFiltering("deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT, "deletedAt.in=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllFloorsByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where deletedAt is not null
        defaultFloorFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllFloorsByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where deletedBy equals to
        defaultFloorFiltering("deletedBy.equals=" + DEFAULT_DELETED_BY, "deletedBy.equals=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllFloorsByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where deletedBy in
        defaultFloorFiltering("deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY, "deletedBy.in=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllFloorsByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        // Get all the floorList where deletedBy is not null
        defaultFloorFiltering("deletedBy.specified=true", "deletedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllFloorsBySeatMapIsEqualToSomething() throws Exception {
        SeatMap seatMap;
        if (TestUtil.findAll(em, SeatMap.class).isEmpty()) {
            floorRepository.saveAndFlush(floor);
            seatMap = SeatMapResourceIT.createEntity();
        } else {
            seatMap = TestUtil.findAll(em, SeatMap.class).get(0);
        }
        em.persist(seatMap);
        em.flush();
        floor.setSeatMap(seatMap);
        floorRepository.saveAndFlush(floor);
        Long seatMapId = seatMap.getId();
        // Get all the floorList where seatMap equals to seatMapId
        defaultFloorShouldBeFound("seatMapId.equals=" + seatMapId);

        // Get all the floorList where seatMap equals to (seatMapId + 1)
        defaultFloorShouldNotBeFound("seatMapId.equals=" + (seatMapId + 1));
    }

    private void defaultFloorFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultFloorShouldBeFound(shouldBeFound);
        defaultFloorShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFloorShouldBeFound(String filter) throws Exception {
        restFloorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(floor.getId().intValue())))
            .andExpect(jsonPath("$.[*].floorNo").value(hasItem(DEFAULT_FLOOR_NO)))
            .andExpect(jsonPath("$.[*].priceFactorFloor").value(hasItem(sameNumber(DEFAULT_PRICE_FACTOR_FLOOR))))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));

        // Check, that the count call also returns 1
        restFloorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFloorShouldNotBeFound(String filter) throws Exception {
        restFloorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFloorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingFloor() throws Exception {
        // Get the floor
        restFloorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFloor() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the floor
        Floor updatedFloor = floorRepository.findById(floor.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFloor are not directly saved in db
        em.detach(updatedFloor);
        updatedFloor
            .floorNo(UPDATED_FLOOR_NO)
            .priceFactorFloor(UPDATED_PRICE_FACTOR_FLOOR)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        FloorDTO floorDTO = floorMapper.toDto(updatedFloor);

        restFloorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, floorDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(floorDTO))
            )
            .andExpect(status().isOk());

        // Validate the Floor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFloorToMatchAllProperties(updatedFloor);
    }

    @Test
    @Transactional
    void putNonExistingFloor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        floor.setId(longCount.incrementAndGet());

        // Create the Floor
        FloorDTO floorDTO = floorMapper.toDto(floor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFloorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, floorDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(floorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Floor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFloor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        floor.setId(longCount.incrementAndGet());

        // Create the Floor
        FloorDTO floorDTO = floorMapper.toDto(floor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFloorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(floorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Floor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFloor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        floor.setId(longCount.incrementAndGet());

        // Create the Floor
        FloorDTO floorDTO = floorMapper.toDto(floor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFloorMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(floorDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Floor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFloorWithPatch() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the floor using partial update
        Floor partialUpdatedFloor = new Floor();
        partialUpdatedFloor.setId(floor.getId());

        partialUpdatedFloor
            .floorNo(UPDATED_FLOOR_NO)
            .priceFactorFloor(UPDATED_PRICE_FACTOR_FLOOR)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restFloorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFloor.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFloor))
            )
            .andExpect(status().isOk());

        // Validate the Floor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFloorUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedFloor, floor), getPersistedFloor(floor));
    }

    @Test
    @Transactional
    void fullUpdateFloorWithPatch() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the floor using partial update
        Floor partialUpdatedFloor = new Floor();
        partialUpdatedFloor.setId(floor.getId());

        partialUpdatedFloor
            .floorNo(UPDATED_FLOOR_NO)
            .priceFactorFloor(UPDATED_PRICE_FACTOR_FLOOR)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restFloorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFloor.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFloor))
            )
            .andExpect(status().isOk());

        // Validate the Floor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFloorUpdatableFieldsEquals(partialUpdatedFloor, getPersistedFloor(partialUpdatedFloor));
    }

    @Test
    @Transactional
    void patchNonExistingFloor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        floor.setId(longCount.incrementAndGet());

        // Create the Floor
        FloorDTO floorDTO = floorMapper.toDto(floor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFloorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, floorDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(floorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Floor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFloor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        floor.setId(longCount.incrementAndGet());

        // Create the Floor
        FloorDTO floorDTO = floorMapper.toDto(floor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFloorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(floorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Floor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFloor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        floor.setId(longCount.incrementAndGet());

        // Create the Floor
        FloorDTO floorDTO = floorMapper.toDto(floor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFloorMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(floorDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Floor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFloor() throws Exception {
        // Initialize the database
        insertedFloor = floorRepository.saveAndFlush(floor);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the floor
        restFloorMockMvc
            .perform(delete(ENTITY_API_URL_ID, floor.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return floorRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Floor getPersistedFloor(Floor floor) {
        return floorRepository.findById(floor.getId()).orElseThrow();
    }

    protected void assertPersistedFloorToMatchAllProperties(Floor expectedFloor) {
        assertFloorAllPropertiesEquals(expectedFloor, getPersistedFloor(expectedFloor));
    }

    protected void assertPersistedFloorToMatchUpdatableProperties(Floor expectedFloor) {
        assertFloorAllUpdatablePropertiesEquals(expectedFloor, getPersistedFloor(expectedFloor));
    }
}
