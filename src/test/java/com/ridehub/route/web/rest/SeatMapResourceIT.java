package com.ridehub.route.web.rest;

import static com.ridehub.route.domain.SeatMapAsserts.*;
import static com.ridehub.route.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.route.IntegrationTest;
import com.ridehub.route.domain.FileRoute;
import com.ridehub.route.domain.SeatMap;
import com.ridehub.route.repository.SeatMapRepository;
import com.ridehub.route.service.dto.SeatMapDTO;
import com.ridehub.route.service.mapper.SeatMapMapper;
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
 * Integration tests for the {@link SeatMapResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SeatMapResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL = "/api/seat-maps";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SeatMapRepository seatMapRepository;

    @Autowired
    private SeatMapMapper seatMapMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSeatMapMockMvc;

    private SeatMap seatMap;

    private SeatMap insertedSeatMap;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SeatMap createEntity() {
        return new SeatMap()
            .name(DEFAULT_NAME)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .isDeleted(DEFAULT_IS_DELETED)
            .deletedAt(DEFAULT_DELETED_AT)
            .deletedBy(DEFAULT_DELETED_BY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SeatMap createUpdatedEntity() {
        return new SeatMap()
            .name(UPDATED_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
    }

    @BeforeEach
    void initTest() {
        seatMap = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSeatMap != null) {
            seatMapRepository.delete(insertedSeatMap);
            insertedSeatMap = null;
        }
    }

    @Test
    @Transactional
    void createSeatMap() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SeatMap
        SeatMapDTO seatMapDTO = seatMapMapper.toDto(seatMap);
        var returnedSeatMapDTO = om.readValue(
            restSeatMapMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(seatMapDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SeatMapDTO.class
        );

        // Validate the SeatMap in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSeatMap = seatMapMapper.toEntity(returnedSeatMapDTO);
        assertSeatMapUpdatableFieldsEquals(returnedSeatMap, getPersistedSeatMap(returnedSeatMap));

        insertedSeatMap = returnedSeatMap;
    }

    @Test
    @Transactional
    void createSeatMapWithExistingId() throws Exception {
        // Create the SeatMap with an existing ID
        seatMap.setId(1L);
        SeatMapDTO seatMapDTO = seatMapMapper.toDto(seatMap);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSeatMapMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(seatMapDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SeatMap in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        seatMap.setName(null);

        // Create the SeatMap, which fails.
        SeatMapDTO seatMapDTO = seatMapMapper.toDto(seatMap);

        restSeatMapMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(seatMapDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        seatMap.setCreatedAt(null);

        // Create the SeatMap, which fails.
        SeatMapDTO seatMapDTO = seatMapMapper.toDto(seatMap);

        restSeatMapMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(seatMapDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSeatMaps() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        // Get all the seatMapList
        restSeatMapMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(seatMap.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));
    }

    @Test
    @Transactional
    void getSeatMap() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        // Get the seatMap
        restSeatMapMockMvc
            .perform(get(ENTITY_API_URL_ID, seatMap.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(seatMap.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()))
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY.toString()));
    }

    @Test
    @Transactional
    void getSeatMapsByIdFiltering() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        Long id = seatMap.getId();

        defaultSeatMapFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSeatMapFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSeatMapFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSeatMapsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        // Get all the seatMapList where name equals to
        defaultSeatMapFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSeatMapsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        // Get all the seatMapList where name in
        defaultSeatMapFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSeatMapsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        // Get all the seatMapList where name is not null
        defaultSeatMapFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllSeatMapsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        // Get all the seatMapList where name contains
        defaultSeatMapFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSeatMapsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        // Get all the seatMapList where name does not contain
        defaultSeatMapFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllSeatMapsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        // Get all the seatMapList where createdAt equals to
        defaultSeatMapFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSeatMapsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        // Get all the seatMapList where createdAt in
        defaultSeatMapFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSeatMapsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        // Get all the seatMapList where createdAt is not null
        defaultSeatMapFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSeatMapsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        // Get all the seatMapList where updatedAt equals to
        defaultSeatMapFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllSeatMapsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        // Get all the seatMapList where updatedAt in
        defaultSeatMapFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllSeatMapsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        // Get all the seatMapList where updatedAt is not null
        defaultSeatMapFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSeatMapsByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        // Get all the seatMapList where isDeleted equals to
        defaultSeatMapFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllSeatMapsByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        // Get all the seatMapList where isDeleted in
        defaultSeatMapFiltering("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED, "isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllSeatMapsByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        // Get all the seatMapList where isDeleted is not null
        defaultSeatMapFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllSeatMapsByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        // Get all the seatMapList where deletedAt equals to
        defaultSeatMapFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllSeatMapsByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        // Get all the seatMapList where deletedAt in
        defaultSeatMapFiltering("deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT, "deletedAt.in=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllSeatMapsByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        // Get all the seatMapList where deletedAt is not null
        defaultSeatMapFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSeatMapsByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        // Get all the seatMapList where deletedBy equals to
        defaultSeatMapFiltering("deletedBy.equals=" + DEFAULT_DELETED_BY, "deletedBy.equals=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllSeatMapsByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        // Get all the seatMapList where deletedBy in
        defaultSeatMapFiltering("deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY, "deletedBy.in=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllSeatMapsByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        // Get all the seatMapList where deletedBy is not null
        defaultSeatMapFiltering("deletedBy.specified=true", "deletedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllSeatMapsBySeatMapImgIsEqualToSomething() throws Exception {
        FileRoute seatMapImg;
        if (TestUtil.findAll(em, FileRoute.class).isEmpty()) {
            seatMapRepository.saveAndFlush(seatMap);
            seatMapImg = FileRouteResourceIT.createEntity();
        } else {
            seatMapImg = TestUtil.findAll(em, FileRoute.class).get(0);
        }
        em.persist(seatMapImg);
        em.flush();
        seatMap.setSeatMapImg(seatMapImg);
        seatMapRepository.saveAndFlush(seatMap);
        Long seatMapImgId = seatMapImg.getId();
        // Get all the seatMapList where seatMapImg equals to seatMapImgId
        defaultSeatMapShouldBeFound("seatMapImgId.equals=" + seatMapImgId);

        // Get all the seatMapList where seatMapImg equals to (seatMapImgId + 1)
        defaultSeatMapShouldNotBeFound("seatMapImgId.equals=" + (seatMapImgId + 1));
    }

    private void defaultSeatMapFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSeatMapShouldBeFound(shouldBeFound);
        defaultSeatMapShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSeatMapShouldBeFound(String filter) throws Exception {
        restSeatMapMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(seatMap.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));

        // Check, that the count call also returns 1
        restSeatMapMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSeatMapShouldNotBeFound(String filter) throws Exception {
        restSeatMapMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSeatMapMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSeatMap() throws Exception {
        // Get the seatMap
        restSeatMapMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSeatMap() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the seatMap
        SeatMap updatedSeatMap = seatMapRepository.findById(seatMap.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSeatMap are not directly saved in db
        em.detach(updatedSeatMap);
        updatedSeatMap
            .name(UPDATED_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        SeatMapDTO seatMapDTO = seatMapMapper.toDto(updatedSeatMap);

        restSeatMapMockMvc
            .perform(
                put(ENTITY_API_URL_ID, seatMapDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(seatMapDTO))
            )
            .andExpect(status().isOk());

        // Validate the SeatMap in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSeatMapToMatchAllProperties(updatedSeatMap);
    }

    @Test
    @Transactional
    void putNonExistingSeatMap() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seatMap.setId(longCount.incrementAndGet());

        // Create the SeatMap
        SeatMapDTO seatMapDTO = seatMapMapper.toDto(seatMap);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSeatMapMockMvc
            .perform(
                put(ENTITY_API_URL_ID, seatMapDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(seatMapDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SeatMap in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSeatMap() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seatMap.setId(longCount.incrementAndGet());

        // Create the SeatMap
        SeatMapDTO seatMapDTO = seatMapMapper.toDto(seatMap);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSeatMapMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(seatMapDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SeatMap in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSeatMap() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seatMap.setId(longCount.incrementAndGet());

        // Create the SeatMap
        SeatMapDTO seatMapDTO = seatMapMapper.toDto(seatMap);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSeatMapMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(seatMapDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SeatMap in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSeatMapWithPatch() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the seatMap using partial update
        SeatMap partialUpdatedSeatMap = new SeatMap();
        partialUpdatedSeatMap.setId(seatMap.getId());

        partialUpdatedSeatMap.updatedAt(UPDATED_UPDATED_AT).isDeleted(UPDATED_IS_DELETED).deletedBy(UPDATED_DELETED_BY);

        restSeatMapMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSeatMap.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSeatMap))
            )
            .andExpect(status().isOk());

        // Validate the SeatMap in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSeatMapUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSeatMap, seatMap), getPersistedSeatMap(seatMap));
    }

    @Test
    @Transactional
    void fullUpdateSeatMapWithPatch() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the seatMap using partial update
        SeatMap partialUpdatedSeatMap = new SeatMap();
        partialUpdatedSeatMap.setId(seatMap.getId());

        partialUpdatedSeatMap
            .name(UPDATED_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restSeatMapMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSeatMap.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSeatMap))
            )
            .andExpect(status().isOk());

        // Validate the SeatMap in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSeatMapUpdatableFieldsEquals(partialUpdatedSeatMap, getPersistedSeatMap(partialUpdatedSeatMap));
    }

    @Test
    @Transactional
    void patchNonExistingSeatMap() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seatMap.setId(longCount.incrementAndGet());

        // Create the SeatMap
        SeatMapDTO seatMapDTO = seatMapMapper.toDto(seatMap);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSeatMapMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, seatMapDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(seatMapDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SeatMap in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSeatMap() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seatMap.setId(longCount.incrementAndGet());

        // Create the SeatMap
        SeatMapDTO seatMapDTO = seatMapMapper.toDto(seatMap);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSeatMapMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(seatMapDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SeatMap in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSeatMap() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seatMap.setId(longCount.incrementAndGet());

        // Create the SeatMap
        SeatMapDTO seatMapDTO = seatMapMapper.toDto(seatMap);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSeatMapMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(seatMapDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SeatMap in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSeatMap() throws Exception {
        // Initialize the database
        insertedSeatMap = seatMapRepository.saveAndFlush(seatMap);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the seatMap
        restSeatMapMockMvc
            .perform(delete(ENTITY_API_URL_ID, seatMap.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return seatMapRepository.count();
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

    protected SeatMap getPersistedSeatMap(SeatMap seatMap) {
        return seatMapRepository.findById(seatMap.getId()).orElseThrow();
    }

    protected void assertPersistedSeatMapToMatchAllProperties(SeatMap expectedSeatMap) {
        assertSeatMapAllPropertiesEquals(expectedSeatMap, getPersistedSeatMap(expectedSeatMap));
    }

    protected void assertPersistedSeatMapToMatchUpdatableProperties(SeatMap expectedSeatMap) {
        assertSeatMapAllUpdatablePropertiesEquals(expectedSeatMap, getPersistedSeatMap(expectedSeatMap));
    }
}
