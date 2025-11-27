package com.ridehub.route.web.rest;

import static com.ridehub.route.domain.DistrictAsserts.*;
import static com.ridehub.route.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.route.IntegrationTest;
import com.ridehub.route.domain.District;
import com.ridehub.route.domain.Province;
import com.ridehub.route.repository.DistrictRepository;
import com.ridehub.route.service.dto.DistrictDTO;
import com.ridehub.route.service.mapper.DistrictMapper;
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
 * Integration tests for the {@link DistrictResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DistrictResourceIT {

    private static final String DEFAULT_DISTRICT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_DISTRICT_CODE = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL = "/api/districts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private DistrictMapper districtMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDistrictMockMvc;

    private District district;

    private District insertedDistrict;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static District createEntity(EntityManager em) {
        District district = new District()
            .districtCode(DEFAULT_DISTRICT_CODE)
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
        Province province;
        if (TestUtil.findAll(em, Province.class).isEmpty()) {
            province = ProvinceResourceIT.createEntity();
            em.persist(province);
            em.flush();
        } else {
            province = TestUtil.findAll(em, Province.class).get(0);
        }
        district.setProvince(province);
        return district;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static District createUpdatedEntity(EntityManager em) {
        District updatedDistrict = new District()
            .districtCode(UPDATED_DISTRICT_CODE)
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
        Province province;
        if (TestUtil.findAll(em, Province.class).isEmpty()) {
            province = ProvinceResourceIT.createUpdatedEntity();
            em.persist(province);
            em.flush();
        } else {
            province = TestUtil.findAll(em, Province.class).get(0);
        }
        updatedDistrict.setProvince(province);
        return updatedDistrict;
    }

    @BeforeEach
    void initTest() {
        district = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedDistrict != null) {
            districtRepository.delete(insertedDistrict);
            insertedDistrict = null;
        }
    }

    @Test
    @Transactional
    void createDistrict() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the District
        DistrictDTO districtDTO = districtMapper.toDto(district);
        var returnedDistrictDTO = om.readValue(
            restDistrictMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(districtDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DistrictDTO.class
        );

        // Validate the District in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDistrict = districtMapper.toEntity(returnedDistrictDTO);
        assertDistrictUpdatableFieldsEquals(returnedDistrict, getPersistedDistrict(returnedDistrict));

        insertedDistrict = returnedDistrict;
    }

    @Test
    @Transactional
    void createDistrictWithExistingId() throws Exception {
        // Create the District with an existing ID
        district.setId(1L);
        DistrictDTO districtDTO = districtMapper.toDto(district);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDistrictMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(districtDTO)))
            .andExpect(status().isBadRequest());

        // Validate the District in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDistrictCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        district.setDistrictCode(null);

        // Create the District, which fails.
        DistrictDTO districtDTO = districtMapper.toDto(district);

        restDistrictMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(districtDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        district.setName(null);

        // Create the District, which fails.
        DistrictDTO districtDTO = districtMapper.toDto(district);

        restDistrictMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(districtDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        district.setCreatedAt(null);

        // Create the District, which fails.
        DistrictDTO districtDTO = districtMapper.toDto(district);

        restDistrictMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(districtDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDistricts() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList
        restDistrictMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(district.getId().intValue())))
            .andExpect(jsonPath("$.[*].districtCode").value(hasItem(DEFAULT_DISTRICT_CODE)))
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
    void getDistrict() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get the district
        restDistrictMockMvc
            .perform(get(ENTITY_API_URL_ID, district.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(district.getId().intValue()))
            .andExpect(jsonPath("$.districtCode").value(DEFAULT_DISTRICT_CODE))
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
    void getDistrictsByIdFiltering() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        Long id = district.getId();

        defaultDistrictFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDistrictFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDistrictFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDistrictsByDistrictCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where districtCode equals to
        defaultDistrictFiltering("districtCode.equals=" + DEFAULT_DISTRICT_CODE, "districtCode.equals=" + UPDATED_DISTRICT_CODE);
    }

    @Test
    @Transactional
    void getAllDistrictsByDistrictCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where districtCode in
        defaultDistrictFiltering(
            "districtCode.in=" + DEFAULT_DISTRICT_CODE + "," + UPDATED_DISTRICT_CODE,
            "districtCode.in=" + UPDATED_DISTRICT_CODE
        );
    }

    @Test
    @Transactional
    void getAllDistrictsByDistrictCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where districtCode is not null
        defaultDistrictFiltering("districtCode.specified=true", "districtCode.specified=false");
    }

    @Test
    @Transactional
    void getAllDistrictsByDistrictCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where districtCode contains
        defaultDistrictFiltering("districtCode.contains=" + DEFAULT_DISTRICT_CODE, "districtCode.contains=" + UPDATED_DISTRICT_CODE);
    }

    @Test
    @Transactional
    void getAllDistrictsByDistrictCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where districtCode does not contain
        defaultDistrictFiltering(
            "districtCode.doesNotContain=" + UPDATED_DISTRICT_CODE,
            "districtCode.doesNotContain=" + DEFAULT_DISTRICT_CODE
        );
    }

    @Test
    @Transactional
    void getAllDistrictsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where name equals to
        defaultDistrictFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDistrictsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where name in
        defaultDistrictFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDistrictsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where name is not null
        defaultDistrictFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllDistrictsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where name contains
        defaultDistrictFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDistrictsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where name does not contain
        defaultDistrictFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllDistrictsByNameEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where nameEn equals to
        defaultDistrictFiltering("nameEn.equals=" + DEFAULT_NAME_EN, "nameEn.equals=" + UPDATED_NAME_EN);
    }

    @Test
    @Transactional
    void getAllDistrictsByNameEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where nameEn in
        defaultDistrictFiltering("nameEn.in=" + DEFAULT_NAME_EN + "," + UPDATED_NAME_EN, "nameEn.in=" + UPDATED_NAME_EN);
    }

    @Test
    @Transactional
    void getAllDistrictsByNameEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where nameEn is not null
        defaultDistrictFiltering("nameEn.specified=true", "nameEn.specified=false");
    }

    @Test
    @Transactional
    void getAllDistrictsByNameEnContainsSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where nameEn contains
        defaultDistrictFiltering("nameEn.contains=" + DEFAULT_NAME_EN, "nameEn.contains=" + UPDATED_NAME_EN);
    }

    @Test
    @Transactional
    void getAllDistrictsByNameEnNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where nameEn does not contain
        defaultDistrictFiltering("nameEn.doesNotContain=" + UPDATED_NAME_EN, "nameEn.doesNotContain=" + DEFAULT_NAME_EN);
    }

    @Test
    @Transactional
    void getAllDistrictsByFullNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where fullName equals to
        defaultDistrictFiltering("fullName.equals=" + DEFAULT_FULL_NAME, "fullName.equals=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllDistrictsByFullNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where fullName in
        defaultDistrictFiltering("fullName.in=" + DEFAULT_FULL_NAME + "," + UPDATED_FULL_NAME, "fullName.in=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllDistrictsByFullNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where fullName is not null
        defaultDistrictFiltering("fullName.specified=true", "fullName.specified=false");
    }

    @Test
    @Transactional
    void getAllDistrictsByFullNameContainsSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where fullName contains
        defaultDistrictFiltering("fullName.contains=" + DEFAULT_FULL_NAME, "fullName.contains=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllDistrictsByFullNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where fullName does not contain
        defaultDistrictFiltering("fullName.doesNotContain=" + UPDATED_FULL_NAME, "fullName.doesNotContain=" + DEFAULT_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllDistrictsByFullNameEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where fullNameEn equals to
        defaultDistrictFiltering("fullNameEn.equals=" + DEFAULT_FULL_NAME_EN, "fullNameEn.equals=" + UPDATED_FULL_NAME_EN);
    }

    @Test
    @Transactional
    void getAllDistrictsByFullNameEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where fullNameEn in
        defaultDistrictFiltering(
            "fullNameEn.in=" + DEFAULT_FULL_NAME_EN + "," + UPDATED_FULL_NAME_EN,
            "fullNameEn.in=" + UPDATED_FULL_NAME_EN
        );
    }

    @Test
    @Transactional
    void getAllDistrictsByFullNameEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where fullNameEn is not null
        defaultDistrictFiltering("fullNameEn.specified=true", "fullNameEn.specified=false");
    }

    @Test
    @Transactional
    void getAllDistrictsByFullNameEnContainsSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where fullNameEn contains
        defaultDistrictFiltering("fullNameEn.contains=" + DEFAULT_FULL_NAME_EN, "fullNameEn.contains=" + UPDATED_FULL_NAME_EN);
    }

    @Test
    @Transactional
    void getAllDistrictsByFullNameEnNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where fullNameEn does not contain
        defaultDistrictFiltering("fullNameEn.doesNotContain=" + UPDATED_FULL_NAME_EN, "fullNameEn.doesNotContain=" + DEFAULT_FULL_NAME_EN);
    }

    @Test
    @Transactional
    void getAllDistrictsByCodeNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where codeName equals to
        defaultDistrictFiltering("codeName.equals=" + DEFAULT_CODE_NAME, "codeName.equals=" + UPDATED_CODE_NAME);
    }

    @Test
    @Transactional
    void getAllDistrictsByCodeNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where codeName in
        defaultDistrictFiltering("codeName.in=" + DEFAULT_CODE_NAME + "," + UPDATED_CODE_NAME, "codeName.in=" + UPDATED_CODE_NAME);
    }

    @Test
    @Transactional
    void getAllDistrictsByCodeNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where codeName is not null
        defaultDistrictFiltering("codeName.specified=true", "codeName.specified=false");
    }

    @Test
    @Transactional
    void getAllDistrictsByCodeNameContainsSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where codeName contains
        defaultDistrictFiltering("codeName.contains=" + DEFAULT_CODE_NAME, "codeName.contains=" + UPDATED_CODE_NAME);
    }

    @Test
    @Transactional
    void getAllDistrictsByCodeNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where codeName does not contain
        defaultDistrictFiltering("codeName.doesNotContain=" + UPDATED_CODE_NAME, "codeName.doesNotContain=" + DEFAULT_CODE_NAME);
    }

    @Test
    @Transactional
    void getAllDistrictsByAdministrativeUnitIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where administrativeUnitId equals to
        defaultDistrictFiltering(
            "administrativeUnitId.equals=" + DEFAULT_ADMINISTRATIVE_UNIT_ID,
            "administrativeUnitId.equals=" + UPDATED_ADMINISTRATIVE_UNIT_ID
        );
    }

    @Test
    @Transactional
    void getAllDistrictsByAdministrativeUnitIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where administrativeUnitId in
        defaultDistrictFiltering(
            "administrativeUnitId.in=" + DEFAULT_ADMINISTRATIVE_UNIT_ID + "," + UPDATED_ADMINISTRATIVE_UNIT_ID,
            "administrativeUnitId.in=" + UPDATED_ADMINISTRATIVE_UNIT_ID
        );
    }

    @Test
    @Transactional
    void getAllDistrictsByAdministrativeUnitIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where administrativeUnitId is not null
        defaultDistrictFiltering("administrativeUnitId.specified=true", "administrativeUnitId.specified=false");
    }

    @Test
    @Transactional
    void getAllDistrictsByAdministrativeUnitIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where administrativeUnitId is greater than or equal to
        defaultDistrictFiltering(
            "administrativeUnitId.greaterThanOrEqual=" + DEFAULT_ADMINISTRATIVE_UNIT_ID,
            "administrativeUnitId.greaterThanOrEqual=" + UPDATED_ADMINISTRATIVE_UNIT_ID
        );
    }

    @Test
    @Transactional
    void getAllDistrictsByAdministrativeUnitIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where administrativeUnitId is less than or equal to
        defaultDistrictFiltering(
            "administrativeUnitId.lessThanOrEqual=" + DEFAULT_ADMINISTRATIVE_UNIT_ID,
            "administrativeUnitId.lessThanOrEqual=" + SMALLER_ADMINISTRATIVE_UNIT_ID
        );
    }

    @Test
    @Transactional
    void getAllDistrictsByAdministrativeUnitIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where administrativeUnitId is less than
        defaultDistrictFiltering(
            "administrativeUnitId.lessThan=" + UPDATED_ADMINISTRATIVE_UNIT_ID,
            "administrativeUnitId.lessThan=" + DEFAULT_ADMINISTRATIVE_UNIT_ID
        );
    }

    @Test
    @Transactional
    void getAllDistrictsByAdministrativeUnitIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where administrativeUnitId is greater than
        defaultDistrictFiltering(
            "administrativeUnitId.greaterThan=" + SMALLER_ADMINISTRATIVE_UNIT_ID,
            "administrativeUnitId.greaterThan=" + DEFAULT_ADMINISTRATIVE_UNIT_ID
        );
    }

    @Test
    @Transactional
    void getAllDistrictsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where createdAt equals to
        defaultDistrictFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllDistrictsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where createdAt in
        defaultDistrictFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllDistrictsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where createdAt is not null
        defaultDistrictFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllDistrictsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where updatedAt equals to
        defaultDistrictFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllDistrictsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where updatedAt in
        defaultDistrictFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllDistrictsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where updatedAt is not null
        defaultDistrictFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllDistrictsByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where isDeleted equals to
        defaultDistrictFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllDistrictsByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where isDeleted in
        defaultDistrictFiltering("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED, "isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllDistrictsByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where isDeleted is not null
        defaultDistrictFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllDistrictsByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where deletedAt equals to
        defaultDistrictFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllDistrictsByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where deletedAt in
        defaultDistrictFiltering("deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT, "deletedAt.in=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllDistrictsByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where deletedAt is not null
        defaultDistrictFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllDistrictsByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where deletedBy equals to
        defaultDistrictFiltering("deletedBy.equals=" + DEFAULT_DELETED_BY, "deletedBy.equals=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllDistrictsByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where deletedBy in
        defaultDistrictFiltering("deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY, "deletedBy.in=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllDistrictsByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        // Get all the districtList where deletedBy is not null
        defaultDistrictFiltering("deletedBy.specified=true", "deletedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllDistrictsByProvinceIsEqualToSomething() throws Exception {
        Province province;
        if (TestUtil.findAll(em, Province.class).isEmpty()) {
            districtRepository.saveAndFlush(district);
            province = ProvinceResourceIT.createEntity();
        } else {
            province = TestUtil.findAll(em, Province.class).get(0);
        }
        em.persist(province);
        em.flush();
        district.setProvince(province);
        districtRepository.saveAndFlush(district);
        Long provinceId = province.getId();
        // Get all the districtList where province equals to provinceId
        defaultDistrictShouldBeFound("provinceId.equals=" + provinceId);

        // Get all the districtList where province equals to (provinceId + 1)
        defaultDistrictShouldNotBeFound("provinceId.equals=" + (provinceId + 1));
    }

    private void defaultDistrictFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDistrictShouldBeFound(shouldBeFound);
        defaultDistrictShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDistrictShouldBeFound(String filter) throws Exception {
        restDistrictMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(district.getId().intValue())))
            .andExpect(jsonPath("$.[*].districtCode").value(hasItem(DEFAULT_DISTRICT_CODE)))
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
        restDistrictMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDistrictShouldNotBeFound(String filter) throws Exception {
        restDistrictMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDistrictMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDistrict() throws Exception {
        // Get the district
        restDistrictMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDistrict() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the district
        District updatedDistrict = districtRepository.findById(district.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDistrict are not directly saved in db
        em.detach(updatedDistrict);
        updatedDistrict
            .districtCode(UPDATED_DISTRICT_CODE)
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
        DistrictDTO districtDTO = districtMapper.toDto(updatedDistrict);

        restDistrictMockMvc
            .perform(
                put(ENTITY_API_URL_ID, districtDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(districtDTO))
            )
            .andExpect(status().isOk());

        // Validate the District in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDistrictToMatchAllProperties(updatedDistrict);
    }

    @Test
    @Transactional
    void putNonExistingDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        district.setId(longCount.incrementAndGet());

        // Create the District
        DistrictDTO districtDTO = districtMapper.toDto(district);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDistrictMockMvc
            .perform(
                put(ENTITY_API_URL_ID, districtDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(districtDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the District in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        district.setId(longCount.incrementAndGet());

        // Create the District
        DistrictDTO districtDTO = districtMapper.toDto(district);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDistrictMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(districtDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the District in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        district.setId(longCount.incrementAndGet());

        // Create the District
        DistrictDTO districtDTO = districtMapper.toDto(district);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDistrictMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(districtDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the District in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDistrictWithPatch() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the district using partial update
        District partialUpdatedDistrict = new District();
        partialUpdatedDistrict.setId(district.getId());

        partialUpdatedDistrict.name(UPDATED_NAME).fullNameEn(UPDATED_FULL_NAME_EN).updatedAt(UPDATED_UPDATED_AT);

        restDistrictMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDistrict.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDistrict))
            )
            .andExpect(status().isOk());

        // Validate the District in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDistrictUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDistrict, district), getPersistedDistrict(district));
    }

    @Test
    @Transactional
    void fullUpdateDistrictWithPatch() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the district using partial update
        District partialUpdatedDistrict = new District();
        partialUpdatedDistrict.setId(district.getId());

        partialUpdatedDistrict
            .districtCode(UPDATED_DISTRICT_CODE)
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

        restDistrictMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDistrict.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDistrict))
            )
            .andExpect(status().isOk());

        // Validate the District in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDistrictUpdatableFieldsEquals(partialUpdatedDistrict, getPersistedDistrict(partialUpdatedDistrict));
    }

    @Test
    @Transactional
    void patchNonExistingDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        district.setId(longCount.incrementAndGet());

        // Create the District
        DistrictDTO districtDTO = districtMapper.toDto(district);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDistrictMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, districtDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(districtDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the District in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        district.setId(longCount.incrementAndGet());

        // Create the District
        DistrictDTO districtDTO = districtMapper.toDto(district);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDistrictMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(districtDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the District in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDistrict() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        district.setId(longCount.incrementAndGet());

        // Create the District
        DistrictDTO districtDTO = districtMapper.toDto(district);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDistrictMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(districtDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the District in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDistrict() throws Exception {
        // Initialize the database
        insertedDistrict = districtRepository.saveAndFlush(district);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the district
        restDistrictMockMvc
            .perform(delete(ENTITY_API_URL_ID, district.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return districtRepository.count();
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

    protected District getPersistedDistrict(District district) {
        return districtRepository.findById(district.getId()).orElseThrow();
    }

    protected void assertPersistedDistrictToMatchAllProperties(District expectedDistrict) {
        assertDistrictAllPropertiesEquals(expectedDistrict, getPersistedDistrict(expectedDistrict));
    }

    protected void assertPersistedDistrictToMatchUpdatableProperties(District expectedDistrict) {
        assertDistrictAllUpdatablePropertiesEquals(expectedDistrict, getPersistedDistrict(expectedDistrict));
    }
}
