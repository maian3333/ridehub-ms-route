package com.ridehub.route.web.rest;

import static com.ridehub.route.domain.StationAsserts.*;
import static com.ridehub.route.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.route.IntegrationTest;
import com.ridehub.route.domain.Address;
import com.ridehub.route.domain.FileRoute;
import com.ridehub.route.domain.Station;
import com.ridehub.route.repository.StationRepository;
import com.ridehub.route.repository.search.StationSearchRepository;
import com.ridehub.route.service.dto.StationDTO;
import com.ridehub.route.service.mapper.StationMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link StationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StationResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

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

    private static final String ENTITY_API_URL = "/api/stations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/stations/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private StationMapper stationMapper;

    @Autowired
    private StationSearchRepository stationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStationMockMvc;

    private Station station;

    private Station insertedStation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Station createEntity(EntityManager em) {
        Station station = new Station()
            .name(DEFAULT_NAME)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .description(DEFAULT_DESCRIPTION)
            .active(DEFAULT_ACTIVE)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .isDeleted(DEFAULT_IS_DELETED)
            .deletedAt(DEFAULT_DELETED_AT)
            .deletedBy(DEFAULT_DELETED_BY);
        // Add required entity
        Address address;
        if (TestUtil.findAll(em, Address.class).isEmpty()) {
            address = AddressResourceIT.createEntity(em);
            em.persist(address);
            em.flush();
        } else {
            address = TestUtil.findAll(em, Address.class).get(0);
        }
        station.setAddress(address);
        return station;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Station createUpdatedEntity(EntityManager em) {
        Station updatedStation = new Station()
            .name(UPDATED_NAME)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .description(UPDATED_DESCRIPTION)
            .active(UPDATED_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        // Add required entity
        Address address;
        if (TestUtil.findAll(em, Address.class).isEmpty()) {
            address = AddressResourceIT.createUpdatedEntity(em);
            em.persist(address);
            em.flush();
        } else {
            address = TestUtil.findAll(em, Address.class).get(0);
        }
        updatedStation.setAddress(address);
        return updatedStation;
    }

    @BeforeEach
    void initTest() {
        station = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedStation != null) {
            stationRepository.delete(insertedStation);
            stationSearchRepository.delete(insertedStation);
            insertedStation = null;
        }
    }

    @Test
    @Transactional
    void createStation() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stationSearchRepository.findAll());
        // Create the Station
        StationDTO stationDTO = stationMapper.toDto(station);
        var returnedStationDTO = om.readValue(
            restStationMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stationDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StationDTO.class
        );

        // Validate the Station in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStation = stationMapper.toEntity(returnedStationDTO);
        assertStationUpdatableFieldsEquals(returnedStation, getPersistedStation(returnedStation));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(stationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedStation = returnedStation;
    }

    @Test
    @Transactional
    void createStationWithExistingId() throws Exception {
        // Create the Station with an existing ID
        station.setId(1L);
        StationDTO stationDTO = stationMapper.toDto(station);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stationSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restStationMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Station in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stationSearchRepository.findAll());
        // set the field null
        station.setName(null);

        // Create the Station, which fails.
        StationDTO stationDTO = stationMapper.toDto(station);

        restStationMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stationSearchRepository.findAll());
        // set the field null
        station.setActive(null);

        // Create the Station, which fails.
        StationDTO stationDTO = stationMapper.toDto(station);

        restStationMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stationSearchRepository.findAll());
        // set the field null
        station.setCreatedAt(null);

        // Create the Station, which fails.
        StationDTO stationDTO = stationMapper.toDto(station);

        restStationMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllStations() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList
        restStationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(station.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));
    }

    @Test
    @Transactional
    void getStation() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get the station
        restStationMockMvc
            .perform(get(ENTITY_API_URL_ID, station.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(station.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()))
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY.toString()));
    }

    @Test
    @Transactional
    void getStationsByIdFiltering() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        Long id = station.getId();

        defaultStationFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultStationFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultStationFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStationsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where name equals to
        defaultStationFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStationsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where name in
        defaultStationFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStationsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where name is not null
        defaultStationFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllStationsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where name contains
        defaultStationFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStationsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where name does not contain
        defaultStationFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllStationsByPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where phoneNumber equals to
        defaultStationFiltering("phoneNumber.equals=" + DEFAULT_PHONE_NUMBER, "phoneNumber.equals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllStationsByPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where phoneNumber in
        defaultStationFiltering(
            "phoneNumber.in=" + DEFAULT_PHONE_NUMBER + "," + UPDATED_PHONE_NUMBER,
            "phoneNumber.in=" + UPDATED_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllStationsByPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where phoneNumber is not null
        defaultStationFiltering("phoneNumber.specified=true", "phoneNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllStationsByPhoneNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where phoneNumber contains
        defaultStationFiltering("phoneNumber.contains=" + DEFAULT_PHONE_NUMBER, "phoneNumber.contains=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllStationsByPhoneNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where phoneNumber does not contain
        defaultStationFiltering("phoneNumber.doesNotContain=" + UPDATED_PHONE_NUMBER, "phoneNumber.doesNotContain=" + DEFAULT_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllStationsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where description equals to
        defaultStationFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllStationsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where description in
        defaultStationFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllStationsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where description is not null
        defaultStationFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllStationsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where description contains
        defaultStationFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllStationsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where description does not contain
        defaultStationFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllStationsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where active equals to
        defaultStationFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllStationsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where active in
        defaultStationFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllStationsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where active is not null
        defaultStationFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    @Transactional
    void getAllStationsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where createdAt equals to
        defaultStationFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllStationsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where createdAt in
        defaultStationFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllStationsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where createdAt is not null
        defaultStationFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllStationsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where updatedAt equals to
        defaultStationFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllStationsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where updatedAt in
        defaultStationFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllStationsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where updatedAt is not null
        defaultStationFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllStationsByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where isDeleted equals to
        defaultStationFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllStationsByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where isDeleted in
        defaultStationFiltering("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED, "isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllStationsByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where isDeleted is not null
        defaultStationFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllStationsByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where deletedAt equals to
        defaultStationFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllStationsByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where deletedAt in
        defaultStationFiltering("deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT, "deletedAt.in=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllStationsByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where deletedAt is not null
        defaultStationFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllStationsByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where deletedBy equals to
        defaultStationFiltering("deletedBy.equals=" + DEFAULT_DELETED_BY, "deletedBy.equals=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllStationsByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where deletedBy in
        defaultStationFiltering("deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY, "deletedBy.in=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllStationsByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        // Get all the stationList where deletedBy is not null
        defaultStationFiltering("deletedBy.specified=true", "deletedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllStationsByAddressIsEqualToSomething() throws Exception {
        // Get already existing entity
        Address address = station.getAddress();
        stationRepository.saveAndFlush(station);
        Long addressId = address.getId();
        // Get all the stationList where address equals to addressId
        defaultStationShouldBeFound("addressId.equals=" + addressId);

        // Get all the stationList where address equals to (addressId + 1)
        defaultStationShouldNotBeFound("addressId.equals=" + (addressId + 1));
    }

    @Test
    @Transactional
    void getAllStationsByStationImgIsEqualToSomething() throws Exception {
        FileRoute stationImg;
        if (TestUtil.findAll(em, FileRoute.class).isEmpty()) {
            stationRepository.saveAndFlush(station);
            stationImg = FileRouteResourceIT.createEntity();
        } else {
            stationImg = TestUtil.findAll(em, FileRoute.class).get(0);
        }
        em.persist(stationImg);
        em.flush();
        station.setStationImg(stationImg);
        stationRepository.saveAndFlush(station);
        Long stationImgId = stationImg.getId();
        // Get all the stationList where stationImg equals to stationImgId
        defaultStationShouldBeFound("stationImgId.equals=" + stationImgId);

        // Get all the stationList where stationImg equals to (stationImgId + 1)
        defaultStationShouldNotBeFound("stationImgId.equals=" + (stationImgId + 1));
    }

    private void defaultStationFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultStationShouldBeFound(shouldBeFound);
        defaultStationShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStationShouldBeFound(String filter) throws Exception {
        restStationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(station.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));

        // Check, that the count call also returns 1
        restStationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStationShouldNotBeFound(String filter) throws Exception {
        restStationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStation() throws Exception {
        // Get the station
        restStationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStation() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        stationSearchRepository.save(station);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stationSearchRepository.findAll());

        // Update the station
        Station updatedStation = stationRepository.findById(station.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStation are not directly saved in db
        em.detach(updatedStation);
        updatedStation
            .name(UPDATED_NAME)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .description(UPDATED_DESCRIPTION)
            .active(UPDATED_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        StationDTO stationDTO = stationMapper.toDto(updatedStation);

        restStationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stationDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Station in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStationToMatchAllProperties(updatedStation);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(stationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Station> stationSearchList = Streamable.of(stationSearchRepository.findAll()).toList();
                Station testStationSearch = stationSearchList.get(searchDatabaseSizeAfter - 1);

                assertStationAllPropertiesEquals(testStationSearch, updatedStation);
            });
    }

    @Test
    @Transactional
    void putNonExistingStation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stationSearchRepository.findAll());
        station.setId(longCount.incrementAndGet());

        // Create the Station
        StationDTO stationDTO = stationMapper.toDto(station);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stationDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Station in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchStation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stationSearchRepository.findAll());
        station.setId(longCount.incrementAndGet());

        // Create the Station
        StationDTO stationDTO = stationMapper.toDto(station);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Station in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stationSearchRepository.findAll());
        station.setId(longCount.incrementAndGet());

        // Create the Station
        StationDTO stationDTO = stationMapper.toDto(station);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStationMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Station in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateStationWithPatch() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the station using partial update
        Station partialUpdatedStation = new Station();
        partialUpdatedStation.setId(station.getId());

        partialUpdatedStation
            .name(UPDATED_NAME)
            .active(UPDATED_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT);

        restStationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStation.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStation))
            )
            .andExpect(status().isOk());

        // Validate the Station in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStationUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedStation, station), getPersistedStation(station));
    }

    @Test
    @Transactional
    void fullUpdateStationWithPatch() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the station using partial update
        Station partialUpdatedStation = new Station();
        partialUpdatedStation.setId(station.getId());

        partialUpdatedStation
            .name(UPDATED_NAME)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .description(UPDATED_DESCRIPTION)
            .active(UPDATED_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restStationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStation.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStation))
            )
            .andExpect(status().isOk());

        // Validate the Station in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStationUpdatableFieldsEquals(partialUpdatedStation, getPersistedStation(partialUpdatedStation));
    }

    @Test
    @Transactional
    void patchNonExistingStation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stationSearchRepository.findAll());
        station.setId(longCount.incrementAndGet());

        // Create the Station
        StationDTO stationDTO = stationMapper.toDto(station);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stationDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Station in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stationSearchRepository.findAll());
        station.setId(longCount.incrementAndGet());

        // Create the Station
        StationDTO stationDTO = stationMapper.toDto(station);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Station in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stationSearchRepository.findAll());
        station.setId(longCount.incrementAndGet());

        // Create the Station
        StationDTO stationDTO = stationMapper.toDto(station);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStationMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(stationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Station in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteStation() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);
        stationRepository.save(station);
        stationSearchRepository.save(station);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(stationSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the station
        restStationMockMvc
            .perform(delete(ENTITY_API_URL_ID, station.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(stationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchStation() throws Exception {
        // Initialize the database
        insertedStation = stationRepository.saveAndFlush(station);
        stationSearchRepository.save(station);

        // Search the station
        restStationMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + station.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(station.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));
    }

    protected long getRepositoryCount() {
        return stationRepository.count();
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

    protected Station getPersistedStation(Station station) {
        return stationRepository.findById(station.getId()).orElseThrow();
    }

    protected void assertPersistedStationToMatchAllProperties(Station expectedStation) {
        assertStationAllPropertiesEquals(expectedStation, getPersistedStation(expectedStation));
    }

    protected void assertPersistedStationToMatchUpdatableProperties(Station expectedStation) {
        assertStationAllUpdatablePropertiesEquals(expectedStation, getPersistedStation(expectedStation));
    }
}
