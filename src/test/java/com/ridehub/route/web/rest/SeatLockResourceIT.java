package com.ridehub.route.web.rest;

import static com.ridehub.route.domain.SeatLockAsserts.*;
import static com.ridehub.route.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.route.IntegrationTest;
import com.ridehub.route.domain.SeatLock;
import com.ridehub.route.domain.Trip;
import com.ridehub.route.domain.enumeration.LockStatus;
import com.ridehub.route.repository.SeatLockRepository;
import com.ridehub.route.service.dto.SeatLockDTO;
import com.ridehub.route.service.mapper.SeatLockMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link SeatLockResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SeatLockResourceIT {

    private static final String DEFAULT_SEAT_NO = "AAAAAAAAAA";
    private static final String UPDATED_SEAT_NO = "BBBBBBBBBB";

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;
    private static final Long SMALLER_USER_ID = 1L - 1L;

    private static final LockStatus DEFAULT_STATUS = LockStatus.HELD;
    private static final LockStatus UPDATED_STATUS = LockStatus.EXPIRED;

    private static final Instant DEFAULT_EXPIRES_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRES_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_IDEMPOTENCY_KEY = "AAAAAAAAAA";
    private static final String UPDATED_IDEMPOTENCY_KEY = "BBBBBBBBBB";

    private static final Long DEFAULT_BOOKING_ID = 1L;
    private static final Long UPDATED_BOOKING_ID = 2L;
    private static final Long SMALLER_BOOKING_ID = 1L - 1L;

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

    private static final String ENTITY_API_URL = "/api/seat-locks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SeatLockRepository seatLockRepository;

    @Autowired
    private SeatLockMapper seatLockMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSeatLockMockMvc;

    private SeatLock seatLock;

    private SeatLock insertedSeatLock;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SeatLock createEntity(EntityManager em) {
        SeatLock seatLock = new SeatLock()
            .seatNo(DEFAULT_SEAT_NO)
            .userId(DEFAULT_USER_ID)
            .status(DEFAULT_STATUS)
            .expiresAt(DEFAULT_EXPIRES_AT)
            .idempotencyKey(DEFAULT_IDEMPOTENCY_KEY)
            .bookingId(DEFAULT_BOOKING_ID)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .isDeleted(DEFAULT_IS_DELETED)
            .deletedAt(DEFAULT_DELETED_AT)
            .deletedBy(DEFAULT_DELETED_BY);
        // Add required entity
        Trip trip;
        if (TestUtil.findAll(em, Trip.class).isEmpty()) {
            trip = TripResourceIT.createEntity(em);
            em.persist(trip);
            em.flush();
        } else {
            trip = TestUtil.findAll(em, Trip.class).get(0);
        }
        seatLock.setTrip(trip);
        return seatLock;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SeatLock createUpdatedEntity(EntityManager em) {
        SeatLock updatedSeatLock = new SeatLock()
            .seatNo(UPDATED_SEAT_NO)
            .userId(UPDATED_USER_ID)
            .status(UPDATED_STATUS)
            .expiresAt(UPDATED_EXPIRES_AT)
            .idempotencyKey(UPDATED_IDEMPOTENCY_KEY)
            .bookingId(UPDATED_BOOKING_ID)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        // Add required entity
        Trip trip;
        if (TestUtil.findAll(em, Trip.class).isEmpty()) {
            trip = TripResourceIT.createUpdatedEntity(em);
            em.persist(trip);
            em.flush();
        } else {
            trip = TestUtil.findAll(em, Trip.class).get(0);
        }
        updatedSeatLock.setTrip(trip);
        return updatedSeatLock;
    }

    @BeforeEach
    void initTest() {
        seatLock = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedSeatLock != null) {
            seatLockRepository.delete(insertedSeatLock);
            insertedSeatLock = null;
        }
    }

    @Test
    @Transactional
    void createSeatLock() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SeatLock
        SeatLockDTO seatLockDTO = seatLockMapper.toDto(seatLock);
        var returnedSeatLockDTO = om.readValue(
            restSeatLockMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(seatLockDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SeatLockDTO.class
        );

        // Validate the SeatLock in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSeatLock = seatLockMapper.toEntity(returnedSeatLockDTO);
        assertSeatLockUpdatableFieldsEquals(returnedSeatLock, getPersistedSeatLock(returnedSeatLock));

        insertedSeatLock = returnedSeatLock;
    }

    @Test
    @Transactional
    void createSeatLockWithExistingId() throws Exception {
        // Create the SeatLock with an existing ID
        seatLock.setId(1L);
        SeatLockDTO seatLockDTO = seatLockMapper.toDto(seatLock);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSeatLockMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(seatLockDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SeatLock in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSeatNoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        seatLock.setSeatNo(null);

        // Create the SeatLock, which fails.
        SeatLockDTO seatLockDTO = seatLockMapper.toDto(seatLock);

        restSeatLockMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(seatLockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        seatLock.setStatus(null);

        // Create the SeatLock, which fails.
        SeatLockDTO seatLockDTO = seatLockMapper.toDto(seatLock);

        restSeatLockMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(seatLockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkExpiresAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        seatLock.setExpiresAt(null);

        // Create the SeatLock, which fails.
        SeatLockDTO seatLockDTO = seatLockMapper.toDto(seatLock);

        restSeatLockMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(seatLockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        seatLock.setCreatedAt(null);

        // Create the SeatLock, which fails.
        SeatLockDTO seatLockDTO = seatLockMapper.toDto(seatLock);

        restSeatLockMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(seatLockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSeatLocks() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList
        restSeatLockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(seatLock.getId().intValue())))
            .andExpect(jsonPath("$.[*].seatNo").value(hasItem(DEFAULT_SEAT_NO)))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].expiresAt").value(hasItem(DEFAULT_EXPIRES_AT.toString())))
            .andExpect(jsonPath("$.[*].idempotencyKey").value(hasItem(DEFAULT_IDEMPOTENCY_KEY)))
            .andExpect(jsonPath("$.[*].bookingId").value(hasItem(DEFAULT_BOOKING_ID.intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));
    }

    @Test
    @Transactional
    void getSeatLock() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get the seatLock
        restSeatLockMockMvc
            .perform(get(ENTITY_API_URL_ID, seatLock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(seatLock.getId().intValue()))
            .andExpect(jsonPath("$.seatNo").value(DEFAULT_SEAT_NO))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.expiresAt").value(DEFAULT_EXPIRES_AT.toString()))
            .andExpect(jsonPath("$.idempotencyKey").value(DEFAULT_IDEMPOTENCY_KEY))
            .andExpect(jsonPath("$.bookingId").value(DEFAULT_BOOKING_ID.intValue()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()))
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY.toString()));
    }

    @Test
    @Transactional
    void getSeatLocksByIdFiltering() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        Long id = seatLock.getId();

        defaultSeatLockFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSeatLockFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSeatLockFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSeatLocksBySeatNoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where seatNo equals to
        defaultSeatLockFiltering("seatNo.equals=" + DEFAULT_SEAT_NO, "seatNo.equals=" + UPDATED_SEAT_NO);
    }

    @Test
    @Transactional
    void getAllSeatLocksBySeatNoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where seatNo in
        defaultSeatLockFiltering("seatNo.in=" + DEFAULT_SEAT_NO + "," + UPDATED_SEAT_NO, "seatNo.in=" + UPDATED_SEAT_NO);
    }

    @Test
    @Transactional
    void getAllSeatLocksBySeatNoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where seatNo is not null
        defaultSeatLockFiltering("seatNo.specified=true", "seatNo.specified=false");
    }

    @Test
    @Transactional
    void getAllSeatLocksBySeatNoContainsSomething() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where seatNo contains
        defaultSeatLockFiltering("seatNo.contains=" + DEFAULT_SEAT_NO, "seatNo.contains=" + UPDATED_SEAT_NO);
    }

    @Test
    @Transactional
    void getAllSeatLocksBySeatNoNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where seatNo does not contain
        defaultSeatLockFiltering("seatNo.doesNotContain=" + UPDATED_SEAT_NO, "seatNo.doesNotContain=" + DEFAULT_SEAT_NO);
    }

    @Test
    @Transactional
    void getAllSeatLocksByUserIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where userId equals to
        defaultSeatLockFiltering("userId.equals=" + DEFAULT_USER_ID, "userId.equals=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllSeatLocksByUserIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where userId in
        defaultSeatLockFiltering("userId.in=" + DEFAULT_USER_ID + "," + UPDATED_USER_ID, "userId.in=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllSeatLocksByUserIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where userId is not null
        defaultSeatLockFiltering("userId.specified=true", "userId.specified=false");
    }

    @Test
    @Transactional
    void getAllSeatLocksByUserIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where userId is greater than or equal to
        defaultSeatLockFiltering("userId.greaterThanOrEqual=" + DEFAULT_USER_ID, "userId.greaterThanOrEqual=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllSeatLocksByUserIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where userId is less than or equal to
        defaultSeatLockFiltering("userId.lessThanOrEqual=" + DEFAULT_USER_ID, "userId.lessThanOrEqual=" + SMALLER_USER_ID);
    }

    @Test
    @Transactional
    void getAllSeatLocksByUserIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where userId is less than
        defaultSeatLockFiltering("userId.lessThan=" + UPDATED_USER_ID, "userId.lessThan=" + DEFAULT_USER_ID);
    }

    @Test
    @Transactional
    void getAllSeatLocksByUserIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where userId is greater than
        defaultSeatLockFiltering("userId.greaterThan=" + SMALLER_USER_ID, "userId.greaterThan=" + DEFAULT_USER_ID);
    }

    @Test
    @Transactional
    void getAllSeatLocksByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where status equals to
        defaultSeatLockFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllSeatLocksByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where status in
        defaultSeatLockFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllSeatLocksByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where status is not null
        defaultSeatLockFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllSeatLocksByExpiresAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where expiresAt equals to
        defaultSeatLockFiltering("expiresAt.equals=" + DEFAULT_EXPIRES_AT, "expiresAt.equals=" + UPDATED_EXPIRES_AT);
    }

    @Test
    @Transactional
    void getAllSeatLocksByExpiresAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where expiresAt in
        defaultSeatLockFiltering("expiresAt.in=" + DEFAULT_EXPIRES_AT + "," + UPDATED_EXPIRES_AT, "expiresAt.in=" + UPDATED_EXPIRES_AT);
    }

    @Test
    @Transactional
    void getAllSeatLocksByExpiresAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where expiresAt is not null
        defaultSeatLockFiltering("expiresAt.specified=true", "expiresAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSeatLocksByIdempotencyKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where idempotencyKey equals to
        defaultSeatLockFiltering("idempotencyKey.equals=" + DEFAULT_IDEMPOTENCY_KEY, "idempotencyKey.equals=" + UPDATED_IDEMPOTENCY_KEY);
    }

    @Test
    @Transactional
    void getAllSeatLocksByIdempotencyKeyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where idempotencyKey in
        defaultSeatLockFiltering(
            "idempotencyKey.in=" + DEFAULT_IDEMPOTENCY_KEY + "," + UPDATED_IDEMPOTENCY_KEY,
            "idempotencyKey.in=" + UPDATED_IDEMPOTENCY_KEY
        );
    }

    @Test
    @Transactional
    void getAllSeatLocksByIdempotencyKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where idempotencyKey is not null
        defaultSeatLockFiltering("idempotencyKey.specified=true", "idempotencyKey.specified=false");
    }

    @Test
    @Transactional
    void getAllSeatLocksByIdempotencyKeyContainsSomething() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where idempotencyKey contains
        defaultSeatLockFiltering(
            "idempotencyKey.contains=" + DEFAULT_IDEMPOTENCY_KEY,
            "idempotencyKey.contains=" + UPDATED_IDEMPOTENCY_KEY
        );
    }

    @Test
    @Transactional
    void getAllSeatLocksByIdempotencyKeyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where idempotencyKey does not contain
        defaultSeatLockFiltering(
            "idempotencyKey.doesNotContain=" + UPDATED_IDEMPOTENCY_KEY,
            "idempotencyKey.doesNotContain=" + DEFAULT_IDEMPOTENCY_KEY
        );
    }

    @Test
    @Transactional
    void getAllSeatLocksByBookingIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where bookingId equals to
        defaultSeatLockFiltering("bookingId.equals=" + DEFAULT_BOOKING_ID, "bookingId.equals=" + UPDATED_BOOKING_ID);
    }

    @Test
    @Transactional
    void getAllSeatLocksByBookingIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where bookingId in
        defaultSeatLockFiltering("bookingId.in=" + DEFAULT_BOOKING_ID + "," + UPDATED_BOOKING_ID, "bookingId.in=" + UPDATED_BOOKING_ID);
    }

    @Test
    @Transactional
    void getAllSeatLocksByBookingIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where bookingId is not null
        defaultSeatLockFiltering("bookingId.specified=true", "bookingId.specified=false");
    }

    @Test
    @Transactional
    void getAllSeatLocksByBookingIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where bookingId is greater than or equal to
        defaultSeatLockFiltering(
            "bookingId.greaterThanOrEqual=" + DEFAULT_BOOKING_ID,
            "bookingId.greaterThanOrEqual=" + UPDATED_BOOKING_ID
        );
    }

    @Test
    @Transactional
    void getAllSeatLocksByBookingIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where bookingId is less than or equal to
        defaultSeatLockFiltering("bookingId.lessThanOrEqual=" + DEFAULT_BOOKING_ID, "bookingId.lessThanOrEqual=" + SMALLER_BOOKING_ID);
    }

    @Test
    @Transactional
    void getAllSeatLocksByBookingIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where bookingId is less than
        defaultSeatLockFiltering("bookingId.lessThan=" + UPDATED_BOOKING_ID, "bookingId.lessThan=" + DEFAULT_BOOKING_ID);
    }

    @Test
    @Transactional
    void getAllSeatLocksByBookingIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where bookingId is greater than
        defaultSeatLockFiltering("bookingId.greaterThan=" + SMALLER_BOOKING_ID, "bookingId.greaterThan=" + DEFAULT_BOOKING_ID);
    }

    @Test
    @Transactional
    void getAllSeatLocksByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where createdAt equals to
        defaultSeatLockFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSeatLocksByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where createdAt in
        defaultSeatLockFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSeatLocksByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where createdAt is not null
        defaultSeatLockFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSeatLocksByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where updatedAt equals to
        defaultSeatLockFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllSeatLocksByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where updatedAt in
        defaultSeatLockFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllSeatLocksByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where updatedAt is not null
        defaultSeatLockFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSeatLocksByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where isDeleted equals to
        defaultSeatLockFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllSeatLocksByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where isDeleted in
        defaultSeatLockFiltering("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED, "isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllSeatLocksByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where isDeleted is not null
        defaultSeatLockFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllSeatLocksByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where deletedAt equals to
        defaultSeatLockFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllSeatLocksByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where deletedAt in
        defaultSeatLockFiltering("deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT, "deletedAt.in=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllSeatLocksByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where deletedAt is not null
        defaultSeatLockFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSeatLocksByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where deletedBy equals to
        defaultSeatLockFiltering("deletedBy.equals=" + DEFAULT_DELETED_BY, "deletedBy.equals=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllSeatLocksByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where deletedBy in
        defaultSeatLockFiltering("deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY, "deletedBy.in=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllSeatLocksByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        // Get all the seatLockList where deletedBy is not null
        defaultSeatLockFiltering("deletedBy.specified=true", "deletedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllSeatLocksByTripIsEqualToSomething() throws Exception {
        Trip trip;
        if (TestUtil.findAll(em, Trip.class).isEmpty()) {
            seatLockRepository.saveAndFlush(seatLock);
            trip = TripResourceIT.createEntity(em);
        } else {
            trip = TestUtil.findAll(em, Trip.class).get(0);
        }
        em.persist(trip);
        em.flush();
        seatLock.setTrip(trip);
        seatLockRepository.saveAndFlush(seatLock);
        Long tripId = trip.getId();
        // Get all the seatLockList where trip equals to tripId
        defaultSeatLockShouldBeFound("tripId.equals=" + tripId);

        // Get all the seatLockList where trip equals to (tripId + 1)
        defaultSeatLockShouldNotBeFound("tripId.equals=" + (tripId + 1));
    }

    private void defaultSeatLockFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSeatLockShouldBeFound(shouldBeFound);
        defaultSeatLockShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSeatLockShouldBeFound(String filter) throws Exception {
        restSeatLockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(seatLock.getId().intValue())))
            .andExpect(jsonPath("$.[*].seatNo").value(hasItem(DEFAULT_SEAT_NO)))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].expiresAt").value(hasItem(DEFAULT_EXPIRES_AT.toString())))
            .andExpect(jsonPath("$.[*].idempotencyKey").value(hasItem(DEFAULT_IDEMPOTENCY_KEY)))
            .andExpect(jsonPath("$.[*].bookingId").value(hasItem(DEFAULT_BOOKING_ID.intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));

        // Check, that the count call also returns 1
        restSeatLockMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSeatLockShouldNotBeFound(String filter) throws Exception {
        restSeatLockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSeatLockMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSeatLock() throws Exception {
        // Get the seatLock
        restSeatLockMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSeatLock() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the seatLock
        SeatLock updatedSeatLock = seatLockRepository.findById(seatLock.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSeatLock are not directly saved in db
        em.detach(updatedSeatLock);
        updatedSeatLock
            .seatNo(UPDATED_SEAT_NO)
            .userId(UPDATED_USER_ID)
            .status(UPDATED_STATUS)
            .expiresAt(UPDATED_EXPIRES_AT)
            .idempotencyKey(UPDATED_IDEMPOTENCY_KEY)
            .bookingId(UPDATED_BOOKING_ID)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        SeatLockDTO seatLockDTO = seatLockMapper.toDto(updatedSeatLock);

        restSeatLockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, seatLockDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(seatLockDTO))
            )
            .andExpect(status().isOk());

        // Validate the SeatLock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSeatLockToMatchAllProperties(updatedSeatLock);
    }

    @Test
    @Transactional
    void putNonExistingSeatLock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seatLock.setId(longCount.incrementAndGet());

        // Create the SeatLock
        SeatLockDTO seatLockDTO = seatLockMapper.toDto(seatLock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSeatLockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, seatLockDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(seatLockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SeatLock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSeatLock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seatLock.setId(longCount.incrementAndGet());

        // Create the SeatLock
        SeatLockDTO seatLockDTO = seatLockMapper.toDto(seatLock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSeatLockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(seatLockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SeatLock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSeatLock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seatLock.setId(longCount.incrementAndGet());

        // Create the SeatLock
        SeatLockDTO seatLockDTO = seatLockMapper.toDto(seatLock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSeatLockMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(seatLockDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SeatLock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSeatLockWithPatch() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the seatLock using partial update
        SeatLock partialUpdatedSeatLock = new SeatLock();
        partialUpdatedSeatLock.setId(seatLock.getId());

        partialUpdatedSeatLock
            .seatNo(UPDATED_SEAT_NO)
            .expiresAt(UPDATED_EXPIRES_AT)
            .bookingId(UPDATED_BOOKING_ID)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restSeatLockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSeatLock.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSeatLock))
            )
            .andExpect(status().isOk());

        // Validate the SeatLock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSeatLockUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSeatLock, seatLock), getPersistedSeatLock(seatLock));
    }

    @Test
    @Transactional
    void fullUpdateSeatLockWithPatch() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the seatLock using partial update
        SeatLock partialUpdatedSeatLock = new SeatLock();
        partialUpdatedSeatLock.setId(seatLock.getId());

        partialUpdatedSeatLock
            .seatNo(UPDATED_SEAT_NO)
            .userId(UPDATED_USER_ID)
            .status(UPDATED_STATUS)
            .expiresAt(UPDATED_EXPIRES_AT)
            .idempotencyKey(UPDATED_IDEMPOTENCY_KEY)
            .bookingId(UPDATED_BOOKING_ID)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restSeatLockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSeatLock.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSeatLock))
            )
            .andExpect(status().isOk());

        // Validate the SeatLock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSeatLockUpdatableFieldsEquals(partialUpdatedSeatLock, getPersistedSeatLock(partialUpdatedSeatLock));
    }

    @Test
    @Transactional
    void patchNonExistingSeatLock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seatLock.setId(longCount.incrementAndGet());

        // Create the SeatLock
        SeatLockDTO seatLockDTO = seatLockMapper.toDto(seatLock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSeatLockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, seatLockDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(seatLockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SeatLock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSeatLock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seatLock.setId(longCount.incrementAndGet());

        // Create the SeatLock
        SeatLockDTO seatLockDTO = seatLockMapper.toDto(seatLock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSeatLockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(seatLockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SeatLock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSeatLock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seatLock.setId(longCount.incrementAndGet());

        // Create the SeatLock
        SeatLockDTO seatLockDTO = seatLockMapper.toDto(seatLock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSeatLockMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(seatLockDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SeatLock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSeatLock() throws Exception {
        // Initialize the database
        insertedSeatLock = seatLockRepository.saveAndFlush(seatLock);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the seatLock
        restSeatLockMockMvc
            .perform(delete(ENTITY_API_URL_ID, seatLock.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return seatLockRepository.count();
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

    protected SeatLock getPersistedSeatLock(SeatLock seatLock) {
        return seatLockRepository.findById(seatLock.getId()).orElseThrow();
    }

    protected void assertPersistedSeatLockToMatchAllProperties(SeatLock expectedSeatLock) {
        assertSeatLockAllPropertiesEquals(expectedSeatLock, getPersistedSeatLock(expectedSeatLock));
    }

    protected void assertPersistedSeatLockToMatchUpdatableProperties(SeatLock expectedSeatLock) {
        assertSeatLockAllUpdatablePropertiesEquals(expectedSeatLock, getPersistedSeatLock(expectedSeatLock));
    }
}
