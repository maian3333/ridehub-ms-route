package com.ridehub.route.web.rest;

import static com.ridehub.route.domain.WardAsserts.*;
import static com.ridehub.route.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.route.IntegrationTest;
import com.ridehub.route.domain.District;
import com.ridehub.route.domain.Ward;
import com.ridehub.route.repository.WardRepository;
import com.ridehub.route.service.dto.WardDTO;
import com.ridehub.route.service.mapper.WardMapper;
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
 * Integration tests for the {@link WardResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WardResourceIT {

    private static final String DEFAULT_WARD_CODE = "AAAAAAAAAA";
    private static final String UPDATED_WARD_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_NAME_EN = "AAAAAAAAAA";
    private static final String UPDATED_NAME_EN = "BBBBBBBBBB";

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FULL_NAME_EN = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME_EN = "BBBBBBBBBB";

    private static final String DEFAULT_CODE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CODE_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_ADMINISTRATIVE_UNIT_ID = 1;
    private static final Integer UPDATED_ADMINISTRATIVE_UNIT_ID = 2;
    private static final Integer SMALLER_ADMINISTRATIVE_UNIT_ID = 1 - 1;

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

    private static final String ENTITY_API_URL = "/api/wards";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WardRepository wardRepository;

    @Autowired
    private WardMapper wardMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWardMockMvc;

    private Ward ward;

    private Ward insertedWard;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ward createEntity(EntityManager em) {
        Ward ward = new Ward()
            .wardCode(DEFAULT_WARD_CODE)
            .name(DEFAULT_NAME)
            .nameEn(DEFAULT_NAME_EN)
            .fullName(DEFAULT_FULL_NAME)
            .fullNameEn(DEFAULT_FULL_NAME_EN)
            .codeName(DEFAULT_CODE_NAME)
            .administrativeUnitId(DEFAULT_ADMINISTRATIVE_UNIT_ID)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .isDeleted(DEFAULT_IS_DELETED)
            .deletedAt(DEFAULT_DELETED_AT)
            .deletedBy(DEFAULT_DELETED_BY);
        // Add required entity
        District district;
        if (TestUtil.findAll(em, District.class).isEmpty()) {
            district = DistrictResourceIT.createEntity(em);
            em.persist(district);
            em.flush();
        } else {
            district = TestUtil.findAll(em, District.class).get(0);
        }
        ward.setDistrict(district);
        return ward;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ward createUpdatedEntity(EntityManager em) {
        Ward updatedWard = new Ward()
            .wardCode(UPDATED_WARD_CODE)
            .name(UPDATED_NAME)
            .nameEn(UPDATED_NAME_EN)
            .fullName(UPDATED_FULL_NAME)
            .fullNameEn(UPDATED_FULL_NAME_EN)
            .codeName(UPDATED_CODE_NAME)
            .administrativeUnitId(UPDATED_ADMINISTRATIVE_UNIT_ID)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        // Add required entity
        District district;
        if (TestUtil.findAll(em, District.class).isEmpty()) {
            district = DistrictResourceIT.createUpdatedEntity(em);
            em.persist(district);
            em.flush();
        } else {
            district = TestUtil.findAll(em, District.class).get(0);
        }
        updatedWard.setDistrict(district);
        return updatedWard;
    }

    @BeforeEach
    void initTest() {
        ward = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedWard != null) {
            wardRepository.delete(insertedWard);
            insertedWard = null;
        }
    }

    @Test
    @Transactional
    void createWard() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Ward
        WardDTO wardDTO = wardMapper.toDto(ward);
        var returnedWardDTO = om.readValue(
            restWardMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wardDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            WardDTO.class
        );

        // Validate the Ward in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWard = wardMapper.toEntity(returnedWardDTO);
        assertWardUpdatableFieldsEquals(returnedWard, getPersistedWard(returnedWard));

        insertedWard = returnedWard;
    }

    @Test
    @Transactional
    void createWardWithExistingId() throws Exception {
        // Create the Ward with an existing ID
        ward.setId(1L);
        WardDTO wardDTO = wardMapper.toDto(ward);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWardMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wardDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Ward in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkWardCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ward.setWardCode(null);

        // Create the Ward, which fails.
        WardDTO wardDTO = wardMapper.toDto(ward);

        restWardMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wardDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ward.setName(null);

        // Create the Ward, which fails.
        WardDTO wardDTO = wardMapper.toDto(ward);

        restWardMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wardDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ward.setCreatedAt(null);

        // Create the Ward, which fails.
        WardDTO wardDTO = wardMapper.toDto(ward);

        restWardMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wardDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWards() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList
        restWardMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ward.getId().intValue())))
            .andExpect(jsonPath("$.[*].wardCode").value(hasItem(DEFAULT_WARD_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].nameEn").value(hasItem(DEFAULT_NAME_EN)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].fullNameEn").value(hasItem(DEFAULT_FULL_NAME_EN)))
            .andExpect(jsonPath("$.[*].codeName").value(hasItem(DEFAULT_CODE_NAME)))
            .andExpect(jsonPath("$.[*].administrativeUnitId").value(hasItem(DEFAULT_ADMINISTRATIVE_UNIT_ID)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));
    }

    @Test
    @Transactional
    void getWard() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get the ward
        restWardMockMvc
            .perform(get(ENTITY_API_URL_ID, ward.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ward.getId().intValue()))
            .andExpect(jsonPath("$.wardCode").value(DEFAULT_WARD_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.nameEn").value(DEFAULT_NAME_EN))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.fullNameEn").value(DEFAULT_FULL_NAME_EN))
            .andExpect(jsonPath("$.codeName").value(DEFAULT_CODE_NAME))
            .andExpect(jsonPath("$.administrativeUnitId").value(DEFAULT_ADMINISTRATIVE_UNIT_ID))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()))
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY.toString()));
    }

    @Test
    @Transactional
    void getWardsByIdFiltering() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        Long id = ward.getId();

        defaultWardFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultWardFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultWardFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWardsByWardCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where wardCode equals to
        defaultWardFiltering("wardCode.equals=" + DEFAULT_WARD_CODE, "wardCode.equals=" + UPDATED_WARD_CODE);
    }

    @Test
    @Transactional
    void getAllWardsByWardCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where wardCode in
        defaultWardFiltering("wardCode.in=" + DEFAULT_WARD_CODE + "," + UPDATED_WARD_CODE, "wardCode.in=" + UPDATED_WARD_CODE);
    }

    @Test
    @Transactional
    void getAllWardsByWardCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where wardCode is not null
        defaultWardFiltering("wardCode.specified=true", "wardCode.specified=false");
    }

    @Test
    @Transactional
    void getAllWardsByWardCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where wardCode contains
        defaultWardFiltering("wardCode.contains=" + DEFAULT_WARD_CODE, "wardCode.contains=" + UPDATED_WARD_CODE);
    }

    @Test
    @Transactional
    void getAllWardsByWardCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where wardCode does not contain
        defaultWardFiltering("wardCode.doesNotContain=" + UPDATED_WARD_CODE, "wardCode.doesNotContain=" + DEFAULT_WARD_CODE);
    }

    @Test
    @Transactional
    void getAllWardsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where name equals to
        defaultWardFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWardsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where name in
        defaultWardFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWardsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where name is not null
        defaultWardFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllWardsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where name contains
        defaultWardFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWardsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where name does not contain
        defaultWardFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllWardsByNameEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where nameEn equals to
        defaultWardFiltering("nameEn.equals=" + DEFAULT_NAME_EN, "nameEn.equals=" + UPDATED_NAME_EN);
    }

    @Test
    @Transactional
    void getAllWardsByNameEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where nameEn in
        defaultWardFiltering("nameEn.in=" + DEFAULT_NAME_EN + "," + UPDATED_NAME_EN, "nameEn.in=" + UPDATED_NAME_EN);
    }

    @Test
    @Transactional
    void getAllWardsByNameEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where nameEn is not null
        defaultWardFiltering("nameEn.specified=true", "nameEn.specified=false");
    }

    @Test
    @Transactional
    void getAllWardsByNameEnContainsSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where nameEn contains
        defaultWardFiltering("nameEn.contains=" + DEFAULT_NAME_EN, "nameEn.contains=" + UPDATED_NAME_EN);
    }

    @Test
    @Transactional
    void getAllWardsByNameEnNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where nameEn does not contain
        defaultWardFiltering("nameEn.doesNotContain=" + UPDATED_NAME_EN, "nameEn.doesNotContain=" + DEFAULT_NAME_EN);
    }

    @Test
    @Transactional
    void getAllWardsByFullNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where fullName equals to
        defaultWardFiltering("fullName.equals=" + DEFAULT_FULL_NAME, "fullName.equals=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllWardsByFullNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where fullName in
        defaultWardFiltering("fullName.in=" + DEFAULT_FULL_NAME + "," + UPDATED_FULL_NAME, "fullName.in=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllWardsByFullNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where fullName is not null
        defaultWardFiltering("fullName.specified=true", "fullName.specified=false");
    }

    @Test
    @Transactional
    void getAllWardsByFullNameContainsSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where fullName contains
        defaultWardFiltering("fullName.contains=" + DEFAULT_FULL_NAME, "fullName.contains=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllWardsByFullNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where fullName does not contain
        defaultWardFiltering("fullName.doesNotContain=" + UPDATED_FULL_NAME, "fullName.doesNotContain=" + DEFAULT_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllWardsByFullNameEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where fullNameEn equals to
        defaultWardFiltering("fullNameEn.equals=" + DEFAULT_FULL_NAME_EN, "fullNameEn.equals=" + UPDATED_FULL_NAME_EN);
    }

    @Test
    @Transactional
    void getAllWardsByFullNameEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where fullNameEn in
        defaultWardFiltering("fullNameEn.in=" + DEFAULT_FULL_NAME_EN + "," + UPDATED_FULL_NAME_EN, "fullNameEn.in=" + UPDATED_FULL_NAME_EN);
    }

    @Test
    @Transactional
    void getAllWardsByFullNameEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where fullNameEn is not null
        defaultWardFiltering("fullNameEn.specified=true", "fullNameEn.specified=false");
    }

    @Test
    @Transactional
    void getAllWardsByFullNameEnContainsSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where fullNameEn contains
        defaultWardFiltering("fullNameEn.contains=" + DEFAULT_FULL_NAME_EN, "fullNameEn.contains=" + UPDATED_FULL_NAME_EN);
    }

    @Test
    @Transactional
    void getAllWardsByFullNameEnNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where fullNameEn does not contain
        defaultWardFiltering("fullNameEn.doesNotContain=" + UPDATED_FULL_NAME_EN, "fullNameEn.doesNotContain=" + DEFAULT_FULL_NAME_EN);
    }

    @Test
    @Transactional
    void getAllWardsByCodeNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where codeName equals to
        defaultWardFiltering("codeName.equals=" + DEFAULT_CODE_NAME, "codeName.equals=" + UPDATED_CODE_NAME);
    }

    @Test
    @Transactional
    void getAllWardsByCodeNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where codeName in
        defaultWardFiltering("codeName.in=" + DEFAULT_CODE_NAME + "," + UPDATED_CODE_NAME, "codeName.in=" + UPDATED_CODE_NAME);
    }

    @Test
    @Transactional
    void getAllWardsByCodeNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where codeName is not null
        defaultWardFiltering("codeName.specified=true", "codeName.specified=false");
    }

    @Test
    @Transactional
    void getAllWardsByCodeNameContainsSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where codeName contains
        defaultWardFiltering("codeName.contains=" + DEFAULT_CODE_NAME, "codeName.contains=" + UPDATED_CODE_NAME);
    }

    @Test
    @Transactional
    void getAllWardsByCodeNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where codeName does not contain
        defaultWardFiltering("codeName.doesNotContain=" + UPDATED_CODE_NAME, "codeName.doesNotContain=" + DEFAULT_CODE_NAME);
    }

    @Test
    @Transactional
    void getAllWardsByAdministrativeUnitIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where administrativeUnitId equals to
        defaultWardFiltering(
            "administrativeUnitId.equals=" + DEFAULT_ADMINISTRATIVE_UNIT_ID,
            "administrativeUnitId.equals=" + UPDATED_ADMINISTRATIVE_UNIT_ID
        );
    }

    @Test
    @Transactional
    void getAllWardsByAdministrativeUnitIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where administrativeUnitId in
        defaultWardFiltering(
            "administrativeUnitId.in=" + DEFAULT_ADMINISTRATIVE_UNIT_ID + "," + UPDATED_ADMINISTRATIVE_UNIT_ID,
            "administrativeUnitId.in=" + UPDATED_ADMINISTRATIVE_UNIT_ID
        );
    }

    @Test
    @Transactional
    void getAllWardsByAdministrativeUnitIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where administrativeUnitId is not null
        defaultWardFiltering("administrativeUnitId.specified=true", "administrativeUnitId.specified=false");
    }

    @Test
    @Transactional
    void getAllWardsByAdministrativeUnitIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where administrativeUnitId is greater than or equal to
        defaultWardFiltering(
            "administrativeUnitId.greaterThanOrEqual=" + DEFAULT_ADMINISTRATIVE_UNIT_ID,
            "administrativeUnitId.greaterThanOrEqual=" + UPDATED_ADMINISTRATIVE_UNIT_ID
        );
    }

    @Test
    @Transactional
    void getAllWardsByAdministrativeUnitIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where administrativeUnitId is less than or equal to
        defaultWardFiltering(
            "administrativeUnitId.lessThanOrEqual=" + DEFAULT_ADMINISTRATIVE_UNIT_ID,
            "administrativeUnitId.lessThanOrEqual=" + SMALLER_ADMINISTRATIVE_UNIT_ID
        );
    }

    @Test
    @Transactional
    void getAllWardsByAdministrativeUnitIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where administrativeUnitId is less than
        defaultWardFiltering(
            "administrativeUnitId.lessThan=" + UPDATED_ADMINISTRATIVE_UNIT_ID,
            "administrativeUnitId.lessThan=" + DEFAULT_ADMINISTRATIVE_UNIT_ID
        );
    }

    @Test
    @Transactional
    void getAllWardsByAdministrativeUnitIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where administrativeUnitId is greater than
        defaultWardFiltering(
            "administrativeUnitId.greaterThan=" + SMALLER_ADMINISTRATIVE_UNIT_ID,
            "administrativeUnitId.greaterThan=" + DEFAULT_ADMINISTRATIVE_UNIT_ID
        );
    }

    @Test
    @Transactional
    void getAllWardsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where createdAt equals to
        defaultWardFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllWardsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where createdAt in
        defaultWardFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllWardsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where createdAt is not null
        defaultWardFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllWardsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where updatedAt equals to
        defaultWardFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllWardsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where updatedAt in
        defaultWardFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllWardsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where updatedAt is not null
        defaultWardFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllWardsByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where isDeleted equals to
        defaultWardFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllWardsByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where isDeleted in
        defaultWardFiltering("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED, "isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllWardsByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where isDeleted is not null
        defaultWardFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllWardsByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where deletedAt equals to
        defaultWardFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllWardsByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where deletedAt in
        defaultWardFiltering("deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT, "deletedAt.in=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllWardsByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where deletedAt is not null
        defaultWardFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllWardsByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where deletedBy equals to
        defaultWardFiltering("deletedBy.equals=" + DEFAULT_DELETED_BY, "deletedBy.equals=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllWardsByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where deletedBy in
        defaultWardFiltering("deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY, "deletedBy.in=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllWardsByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        // Get all the wardList where deletedBy is not null
        defaultWardFiltering("deletedBy.specified=true", "deletedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllWardsByDistrictIsEqualToSomething() throws Exception {
        District district;
        if (TestUtil.findAll(em, District.class).isEmpty()) {
            wardRepository.saveAndFlush(ward);
            district = DistrictResourceIT.createEntity(em);
        } else {
            district = TestUtil.findAll(em, District.class).get(0);
        }
        em.persist(district);
        em.flush();
        ward.setDistrict(district);
        wardRepository.saveAndFlush(ward);
        Long districtId = district.getId();
        // Get all the wardList where district equals to districtId
        defaultWardShouldBeFound("districtId.equals=" + districtId);

        // Get all the wardList where district equals to (districtId + 1)
        defaultWardShouldNotBeFound("districtId.equals=" + (districtId + 1));
    }

    private void defaultWardFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultWardShouldBeFound(shouldBeFound);
        defaultWardShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWardShouldBeFound(String filter) throws Exception {
        restWardMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ward.getId().intValue())))
            .andExpect(jsonPath("$.[*].wardCode").value(hasItem(DEFAULT_WARD_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].nameEn").value(hasItem(DEFAULT_NAME_EN)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].fullNameEn").value(hasItem(DEFAULT_FULL_NAME_EN)))
            .andExpect(jsonPath("$.[*].codeName").value(hasItem(DEFAULT_CODE_NAME)))
            .andExpect(jsonPath("$.[*].administrativeUnitId").value(hasItem(DEFAULT_ADMINISTRATIVE_UNIT_ID)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));

        // Check, that the count call also returns 1
        restWardMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWardShouldNotBeFound(String filter) throws Exception {
        restWardMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWardMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWard() throws Exception {
        // Get the ward
        restWardMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWard() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ward
        Ward updatedWard = wardRepository.findById(ward.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedWard are not directly saved in db
        em.detach(updatedWard);
        updatedWard
            .wardCode(UPDATED_WARD_CODE)
            .name(UPDATED_NAME)
            .nameEn(UPDATED_NAME_EN)
            .fullName(UPDATED_FULL_NAME)
            .fullNameEn(UPDATED_FULL_NAME_EN)
            .codeName(UPDATED_CODE_NAME)
            .administrativeUnitId(UPDATED_ADMINISTRATIVE_UNIT_ID)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        WardDTO wardDTO = wardMapper.toDto(updatedWard);

        restWardMockMvc
            .perform(
                put(ENTITY_API_URL_ID, wardDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(wardDTO))
            )
            .andExpect(status().isOk());

        // Validate the Ward in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWardToMatchAllProperties(updatedWard);
    }

    @Test
    @Transactional
    void putNonExistingWard() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ward.setId(longCount.incrementAndGet());

        // Create the Ward
        WardDTO wardDTO = wardMapper.toDto(ward);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWardMockMvc
            .perform(
                put(ENTITY_API_URL_ID, wardDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(wardDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ward in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWard() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ward.setId(longCount.incrementAndGet());

        // Create the Ward
        WardDTO wardDTO = wardMapper.toDto(ward);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWardMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(wardDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ward in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWard() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ward.setId(longCount.incrementAndGet());

        // Create the Ward
        WardDTO wardDTO = wardMapper.toDto(ward);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWardMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(wardDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ward in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWardWithPatch() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ward using partial update
        Ward partialUpdatedWard = new Ward();
        partialUpdatedWard.setId(ward.getId());

        partialUpdatedWard
            .wardCode(UPDATED_WARD_CODE)
            .name(UPDATED_NAME)
            .fullName(UPDATED_FULL_NAME)
            .fullNameEn(UPDATED_FULL_NAME_EN)
            .codeName(UPDATED_CODE_NAME)
            .administrativeUnitId(UPDATED_ADMINISTRATIVE_UNIT_ID)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT);

        restWardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWard.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWard))
            )
            .andExpect(status().isOk());

        // Validate the Ward in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWardUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedWard, ward), getPersistedWard(ward));
    }

    @Test
    @Transactional
    void fullUpdateWardWithPatch() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ward using partial update
        Ward partialUpdatedWard = new Ward();
        partialUpdatedWard.setId(ward.getId());

        partialUpdatedWard
            .wardCode(UPDATED_WARD_CODE)
            .name(UPDATED_NAME)
            .nameEn(UPDATED_NAME_EN)
            .fullName(UPDATED_FULL_NAME)
            .fullNameEn(UPDATED_FULL_NAME_EN)
            .codeName(UPDATED_CODE_NAME)
            .administrativeUnitId(UPDATED_ADMINISTRATIVE_UNIT_ID)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restWardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWard.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWard))
            )
            .andExpect(status().isOk());

        // Validate the Ward in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWardUpdatableFieldsEquals(partialUpdatedWard, getPersistedWard(partialUpdatedWard));
    }

    @Test
    @Transactional
    void patchNonExistingWard() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ward.setId(longCount.incrementAndGet());

        // Create the Ward
        WardDTO wardDTO = wardMapper.toDto(ward);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, wardDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(wardDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ward in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWard() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ward.setId(longCount.incrementAndGet());

        // Create the Ward
        WardDTO wardDTO = wardMapper.toDto(ward);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(wardDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ward in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWard() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ward.setId(longCount.incrementAndGet());

        // Create the Ward
        WardDTO wardDTO = wardMapper.toDto(ward);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWardMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(wardDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ward in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWard() throws Exception {
        // Initialize the database
        insertedWard = wardRepository.saveAndFlush(ward);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the ward
        restWardMockMvc
            .perform(delete(ENTITY_API_URL_ID, ward.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return wardRepository.count();
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

    protected Ward getPersistedWard(Ward ward) {
        return wardRepository.findById(ward.getId()).orElseThrow();
    }

    protected void assertPersistedWardToMatchAllProperties(Ward expectedWard) {
        assertWardAllPropertiesEquals(expectedWard, getPersistedWard(expectedWard));
    }

    protected void assertPersistedWardToMatchUpdatableProperties(Ward expectedWard) {
        assertWardAllUpdatablePropertiesEquals(expectedWard, getPersistedWard(expectedWard));
    }
}
