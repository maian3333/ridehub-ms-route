package com.ridehub.route.web.rest;

import static com.ridehub.route.domain.ProvinceAsserts.*;
import static com.ridehub.route.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.route.IntegrationTest;
import com.ridehub.route.domain.Province;
import com.ridehub.route.repository.ProvinceRepository;
import com.ridehub.route.service.dto.ProvinceDTO;
import com.ridehub.route.service.mapper.ProvinceMapper;
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
 * Integration tests for the {@link ProvinceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProvinceResourceIT {

    private static final String DEFAULT_PROVINCE_CODE = "AAAAAAAAAA";
    private static final String UPDATED_PROVINCE_CODE = "BBBBBBBBBB";

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

    private static final Integer DEFAULT_ADMINISTRATIVE_REGION_ID = 1;
    private static final Integer UPDATED_ADMINISTRATIVE_REGION_ID = 2;
    private static final Integer SMALLER_ADMINISTRATIVE_REGION_ID = 1 - 1;

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

    private static final String ENTITY_API_URL = "/api/provinces";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private ProvinceMapper provinceMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProvinceMockMvc;

    private Province province;

    private Province insertedProvince;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Province createEntity() {
        return new Province()
            .provinceCode(DEFAULT_PROVINCE_CODE)
            .name(DEFAULT_NAME)
            .nameEn(DEFAULT_NAME_EN)
            .fullName(DEFAULT_FULL_NAME)
            .fullNameEn(DEFAULT_FULL_NAME_EN)
            .codeName(DEFAULT_CODE_NAME)
            .administrativeUnitId(DEFAULT_ADMINISTRATIVE_UNIT_ID)
            .administrativeRegionId(DEFAULT_ADMINISTRATIVE_REGION_ID)
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
    public static Province createUpdatedEntity() {
        return new Province()
            .provinceCode(UPDATED_PROVINCE_CODE)
            .name(UPDATED_NAME)
            .nameEn(UPDATED_NAME_EN)
            .fullName(UPDATED_FULL_NAME)
            .fullNameEn(UPDATED_FULL_NAME_EN)
            .codeName(UPDATED_CODE_NAME)
            .administrativeUnitId(UPDATED_ADMINISTRATIVE_UNIT_ID)
            .administrativeRegionId(UPDATED_ADMINISTRATIVE_REGION_ID)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
    }

    @BeforeEach
    void initTest() {
        province = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProvince != null) {
            provinceRepository.delete(insertedProvince);
            insertedProvince = null;
        }
    }

    @Test
    @Transactional
    void createProvince() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Province
        ProvinceDTO provinceDTO = provinceMapper.toDto(province);
        var returnedProvinceDTO = om.readValue(
            restProvinceMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(provinceDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProvinceDTO.class
        );

        // Validate the Province in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProvince = provinceMapper.toEntity(returnedProvinceDTO);
        assertProvinceUpdatableFieldsEquals(returnedProvince, getPersistedProvince(returnedProvince));

        insertedProvince = returnedProvince;
    }

    @Test
    @Transactional
    void createProvinceWithExistingId() throws Exception {
        // Create the Province with an existing ID
        province.setId(1L);
        ProvinceDTO provinceDTO = provinceMapper.toDto(province);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProvinceMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(provinceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Province in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkProvinceCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        province.setProvinceCode(null);

        // Create the Province, which fails.
        ProvinceDTO provinceDTO = provinceMapper.toDto(province);

        restProvinceMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(provinceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        province.setName(null);

        // Create the Province, which fails.
        ProvinceDTO provinceDTO = provinceMapper.toDto(province);

        restProvinceMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(provinceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        province.setCreatedAt(null);

        // Create the Province, which fails.
        ProvinceDTO provinceDTO = provinceMapper.toDto(province);

        restProvinceMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(provinceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProvinces() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList
        restProvinceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(province.getId().intValue())))
            .andExpect(jsonPath("$.[*].provinceCode").value(hasItem(DEFAULT_PROVINCE_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].nameEn").value(hasItem(DEFAULT_NAME_EN)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].fullNameEn").value(hasItem(DEFAULT_FULL_NAME_EN)))
            .andExpect(jsonPath("$.[*].codeName").value(hasItem(DEFAULT_CODE_NAME)))
            .andExpect(jsonPath("$.[*].administrativeUnitId").value(hasItem(DEFAULT_ADMINISTRATIVE_UNIT_ID)))
            .andExpect(jsonPath("$.[*].administrativeRegionId").value(hasItem(DEFAULT_ADMINISTRATIVE_REGION_ID)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));
    }

    @Test
    @Transactional
    void getProvince() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get the province
        restProvinceMockMvc
            .perform(get(ENTITY_API_URL_ID, province.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(province.getId().intValue()))
            .andExpect(jsonPath("$.provinceCode").value(DEFAULT_PROVINCE_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.nameEn").value(DEFAULT_NAME_EN))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.fullNameEn").value(DEFAULT_FULL_NAME_EN))
            .andExpect(jsonPath("$.codeName").value(DEFAULT_CODE_NAME))
            .andExpect(jsonPath("$.administrativeUnitId").value(DEFAULT_ADMINISTRATIVE_UNIT_ID))
            .andExpect(jsonPath("$.administrativeRegionId").value(DEFAULT_ADMINISTRATIVE_REGION_ID))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()))
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY.toString()));
    }

    @Test
    @Transactional
    void getProvincesByIdFiltering() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        Long id = province.getId();

        defaultProvinceFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultProvinceFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultProvinceFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProvincesByProvinceCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where provinceCode equals to
        defaultProvinceFiltering("provinceCode.equals=" + DEFAULT_PROVINCE_CODE, "provinceCode.equals=" + UPDATED_PROVINCE_CODE);
    }

    @Test
    @Transactional
    void getAllProvincesByProvinceCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where provinceCode in
        defaultProvinceFiltering(
            "provinceCode.in=" + DEFAULT_PROVINCE_CODE + "," + UPDATED_PROVINCE_CODE,
            "provinceCode.in=" + UPDATED_PROVINCE_CODE
        );
    }

    @Test
    @Transactional
    void getAllProvincesByProvinceCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where provinceCode is not null
        defaultProvinceFiltering("provinceCode.specified=true", "provinceCode.specified=false");
    }

    @Test
    @Transactional
    void getAllProvincesByProvinceCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where provinceCode contains
        defaultProvinceFiltering("provinceCode.contains=" + DEFAULT_PROVINCE_CODE, "provinceCode.contains=" + UPDATED_PROVINCE_CODE);
    }

    @Test
    @Transactional
    void getAllProvincesByProvinceCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where provinceCode does not contain
        defaultProvinceFiltering(
            "provinceCode.doesNotContain=" + UPDATED_PROVINCE_CODE,
            "provinceCode.doesNotContain=" + DEFAULT_PROVINCE_CODE
        );
    }

    @Test
    @Transactional
    void getAllProvincesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where name equals to
        defaultProvinceFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProvincesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where name in
        defaultProvinceFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProvincesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where name is not null
        defaultProvinceFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllProvincesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where name contains
        defaultProvinceFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProvincesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where name does not contain
        defaultProvinceFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllProvincesByNameEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where nameEn equals to
        defaultProvinceFiltering("nameEn.equals=" + DEFAULT_NAME_EN, "nameEn.equals=" + UPDATED_NAME_EN);
    }

    @Test
    @Transactional
    void getAllProvincesByNameEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where nameEn in
        defaultProvinceFiltering("nameEn.in=" + DEFAULT_NAME_EN + "," + UPDATED_NAME_EN, "nameEn.in=" + UPDATED_NAME_EN);
    }

    @Test
    @Transactional
    void getAllProvincesByNameEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where nameEn is not null
        defaultProvinceFiltering("nameEn.specified=true", "nameEn.specified=false");
    }

    @Test
    @Transactional
    void getAllProvincesByNameEnContainsSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where nameEn contains
        defaultProvinceFiltering("nameEn.contains=" + DEFAULT_NAME_EN, "nameEn.contains=" + UPDATED_NAME_EN);
    }

    @Test
    @Transactional
    void getAllProvincesByNameEnNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where nameEn does not contain
        defaultProvinceFiltering("nameEn.doesNotContain=" + UPDATED_NAME_EN, "nameEn.doesNotContain=" + DEFAULT_NAME_EN);
    }

    @Test
    @Transactional
    void getAllProvincesByFullNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where fullName equals to
        defaultProvinceFiltering("fullName.equals=" + DEFAULT_FULL_NAME, "fullName.equals=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllProvincesByFullNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where fullName in
        defaultProvinceFiltering("fullName.in=" + DEFAULT_FULL_NAME + "," + UPDATED_FULL_NAME, "fullName.in=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllProvincesByFullNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where fullName is not null
        defaultProvinceFiltering("fullName.specified=true", "fullName.specified=false");
    }

    @Test
    @Transactional
    void getAllProvincesByFullNameContainsSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where fullName contains
        defaultProvinceFiltering("fullName.contains=" + DEFAULT_FULL_NAME, "fullName.contains=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllProvincesByFullNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where fullName does not contain
        defaultProvinceFiltering("fullName.doesNotContain=" + UPDATED_FULL_NAME, "fullName.doesNotContain=" + DEFAULT_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllProvincesByFullNameEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where fullNameEn equals to
        defaultProvinceFiltering("fullNameEn.equals=" + DEFAULT_FULL_NAME_EN, "fullNameEn.equals=" + UPDATED_FULL_NAME_EN);
    }

    @Test
    @Transactional
    void getAllProvincesByFullNameEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where fullNameEn in
        defaultProvinceFiltering(
            "fullNameEn.in=" + DEFAULT_FULL_NAME_EN + "," + UPDATED_FULL_NAME_EN,
            "fullNameEn.in=" + UPDATED_FULL_NAME_EN
        );
    }

    @Test
    @Transactional
    void getAllProvincesByFullNameEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where fullNameEn is not null
        defaultProvinceFiltering("fullNameEn.specified=true", "fullNameEn.specified=false");
    }

    @Test
    @Transactional
    void getAllProvincesByFullNameEnContainsSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where fullNameEn contains
        defaultProvinceFiltering("fullNameEn.contains=" + DEFAULT_FULL_NAME_EN, "fullNameEn.contains=" + UPDATED_FULL_NAME_EN);
    }

    @Test
    @Transactional
    void getAllProvincesByFullNameEnNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where fullNameEn does not contain
        defaultProvinceFiltering("fullNameEn.doesNotContain=" + UPDATED_FULL_NAME_EN, "fullNameEn.doesNotContain=" + DEFAULT_FULL_NAME_EN);
    }

    @Test
    @Transactional
    void getAllProvincesByCodeNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where codeName equals to
        defaultProvinceFiltering("codeName.equals=" + DEFAULT_CODE_NAME, "codeName.equals=" + UPDATED_CODE_NAME);
    }

    @Test
    @Transactional
    void getAllProvincesByCodeNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where codeName in
        defaultProvinceFiltering("codeName.in=" + DEFAULT_CODE_NAME + "," + UPDATED_CODE_NAME, "codeName.in=" + UPDATED_CODE_NAME);
    }

    @Test
    @Transactional
    void getAllProvincesByCodeNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where codeName is not null
        defaultProvinceFiltering("codeName.specified=true", "codeName.specified=false");
    }

    @Test
    @Transactional
    void getAllProvincesByCodeNameContainsSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where codeName contains
        defaultProvinceFiltering("codeName.contains=" + DEFAULT_CODE_NAME, "codeName.contains=" + UPDATED_CODE_NAME);
    }

    @Test
    @Transactional
    void getAllProvincesByCodeNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where codeName does not contain
        defaultProvinceFiltering("codeName.doesNotContain=" + UPDATED_CODE_NAME, "codeName.doesNotContain=" + DEFAULT_CODE_NAME);
    }

    @Test
    @Transactional
    void getAllProvincesByAdministrativeUnitIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where administrativeUnitId equals to
        defaultProvinceFiltering(
            "administrativeUnitId.equals=" + DEFAULT_ADMINISTRATIVE_UNIT_ID,
            "administrativeUnitId.equals=" + UPDATED_ADMINISTRATIVE_UNIT_ID
        );
    }

    @Test
    @Transactional
    void getAllProvincesByAdministrativeUnitIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where administrativeUnitId in
        defaultProvinceFiltering(
            "administrativeUnitId.in=" + DEFAULT_ADMINISTRATIVE_UNIT_ID + "," + UPDATED_ADMINISTRATIVE_UNIT_ID,
            "administrativeUnitId.in=" + UPDATED_ADMINISTRATIVE_UNIT_ID
        );
    }

    @Test
    @Transactional
    void getAllProvincesByAdministrativeUnitIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where administrativeUnitId is not null
        defaultProvinceFiltering("administrativeUnitId.specified=true", "administrativeUnitId.specified=false");
    }

    @Test
    @Transactional
    void getAllProvincesByAdministrativeUnitIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where administrativeUnitId is greater than or equal to
        defaultProvinceFiltering(
            "administrativeUnitId.greaterThanOrEqual=" + DEFAULT_ADMINISTRATIVE_UNIT_ID,
            "administrativeUnitId.greaterThanOrEqual=" + UPDATED_ADMINISTRATIVE_UNIT_ID
        );
    }

    @Test
    @Transactional
    void getAllProvincesByAdministrativeUnitIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where administrativeUnitId is less than or equal to
        defaultProvinceFiltering(
            "administrativeUnitId.lessThanOrEqual=" + DEFAULT_ADMINISTRATIVE_UNIT_ID,
            "administrativeUnitId.lessThanOrEqual=" + SMALLER_ADMINISTRATIVE_UNIT_ID
        );
    }

    @Test
    @Transactional
    void getAllProvincesByAdministrativeUnitIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where administrativeUnitId is less than
        defaultProvinceFiltering(
            "administrativeUnitId.lessThan=" + UPDATED_ADMINISTRATIVE_UNIT_ID,
            "administrativeUnitId.lessThan=" + DEFAULT_ADMINISTRATIVE_UNIT_ID
        );
    }

    @Test
    @Transactional
    void getAllProvincesByAdministrativeUnitIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where administrativeUnitId is greater than
        defaultProvinceFiltering(
            "administrativeUnitId.greaterThan=" + SMALLER_ADMINISTRATIVE_UNIT_ID,
            "administrativeUnitId.greaterThan=" + DEFAULT_ADMINISTRATIVE_UNIT_ID
        );
    }

    @Test
    @Transactional
    void getAllProvincesByAdministrativeRegionIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where administrativeRegionId equals to
        defaultProvinceFiltering(
            "administrativeRegionId.equals=" + DEFAULT_ADMINISTRATIVE_REGION_ID,
            "administrativeRegionId.equals=" + UPDATED_ADMINISTRATIVE_REGION_ID
        );
    }

    @Test
    @Transactional
    void getAllProvincesByAdministrativeRegionIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where administrativeRegionId in
        defaultProvinceFiltering(
            "administrativeRegionId.in=" + DEFAULT_ADMINISTRATIVE_REGION_ID + "," + UPDATED_ADMINISTRATIVE_REGION_ID,
            "administrativeRegionId.in=" + UPDATED_ADMINISTRATIVE_REGION_ID
        );
    }

    @Test
    @Transactional
    void getAllProvincesByAdministrativeRegionIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where administrativeRegionId is not null
        defaultProvinceFiltering("administrativeRegionId.specified=true", "administrativeRegionId.specified=false");
    }

    @Test
    @Transactional
    void getAllProvincesByAdministrativeRegionIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where administrativeRegionId is greater than or equal to
        defaultProvinceFiltering(
            "administrativeRegionId.greaterThanOrEqual=" + DEFAULT_ADMINISTRATIVE_REGION_ID,
            "administrativeRegionId.greaterThanOrEqual=" + UPDATED_ADMINISTRATIVE_REGION_ID
        );
    }

    @Test
    @Transactional
    void getAllProvincesByAdministrativeRegionIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where administrativeRegionId is less than or equal to
        defaultProvinceFiltering(
            "administrativeRegionId.lessThanOrEqual=" + DEFAULT_ADMINISTRATIVE_REGION_ID,
            "administrativeRegionId.lessThanOrEqual=" + SMALLER_ADMINISTRATIVE_REGION_ID
        );
    }

    @Test
    @Transactional
    void getAllProvincesByAdministrativeRegionIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where administrativeRegionId is less than
        defaultProvinceFiltering(
            "administrativeRegionId.lessThan=" + UPDATED_ADMINISTRATIVE_REGION_ID,
            "administrativeRegionId.lessThan=" + DEFAULT_ADMINISTRATIVE_REGION_ID
        );
    }

    @Test
    @Transactional
    void getAllProvincesByAdministrativeRegionIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where administrativeRegionId is greater than
        defaultProvinceFiltering(
            "administrativeRegionId.greaterThan=" + SMALLER_ADMINISTRATIVE_REGION_ID,
            "administrativeRegionId.greaterThan=" + DEFAULT_ADMINISTRATIVE_REGION_ID
        );
    }

    @Test
    @Transactional
    void getAllProvincesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where createdAt equals to
        defaultProvinceFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllProvincesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where createdAt in
        defaultProvinceFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllProvincesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where createdAt is not null
        defaultProvinceFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllProvincesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where updatedAt equals to
        defaultProvinceFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllProvincesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where updatedAt in
        defaultProvinceFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllProvincesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where updatedAt is not null
        defaultProvinceFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllProvincesByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where isDeleted equals to
        defaultProvinceFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllProvincesByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where isDeleted in
        defaultProvinceFiltering("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED, "isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllProvincesByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where isDeleted is not null
        defaultProvinceFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllProvincesByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where deletedAt equals to
        defaultProvinceFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllProvincesByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where deletedAt in
        defaultProvinceFiltering("deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT, "deletedAt.in=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllProvincesByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where deletedAt is not null
        defaultProvinceFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllProvincesByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where deletedBy equals to
        defaultProvinceFiltering("deletedBy.equals=" + DEFAULT_DELETED_BY, "deletedBy.equals=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllProvincesByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where deletedBy in
        defaultProvinceFiltering("deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY, "deletedBy.in=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllProvincesByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        // Get all the provinceList where deletedBy is not null
        defaultProvinceFiltering("deletedBy.specified=true", "deletedBy.specified=false");
    }

    private void defaultProvinceFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultProvinceShouldBeFound(shouldBeFound);
        defaultProvinceShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProvinceShouldBeFound(String filter) throws Exception {
        restProvinceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(province.getId().intValue())))
            .andExpect(jsonPath("$.[*].provinceCode").value(hasItem(DEFAULT_PROVINCE_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].nameEn").value(hasItem(DEFAULT_NAME_EN)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].fullNameEn").value(hasItem(DEFAULT_FULL_NAME_EN)))
            .andExpect(jsonPath("$.[*].codeName").value(hasItem(DEFAULT_CODE_NAME)))
            .andExpect(jsonPath("$.[*].administrativeUnitId").value(hasItem(DEFAULT_ADMINISTRATIVE_UNIT_ID)))
            .andExpect(jsonPath("$.[*].administrativeRegionId").value(hasItem(DEFAULT_ADMINISTRATIVE_REGION_ID)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));

        // Check, that the count call also returns 1
        restProvinceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProvinceShouldNotBeFound(String filter) throws Exception {
        restProvinceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProvinceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProvince() throws Exception {
        // Get the province
        restProvinceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProvince() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the province
        Province updatedProvince = provinceRepository.findById(province.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProvince are not directly saved in db
        em.detach(updatedProvince);
        updatedProvince
            .provinceCode(UPDATED_PROVINCE_CODE)
            .name(UPDATED_NAME)
            .nameEn(UPDATED_NAME_EN)
            .fullName(UPDATED_FULL_NAME)
            .fullNameEn(UPDATED_FULL_NAME_EN)
            .codeName(UPDATED_CODE_NAME)
            .administrativeUnitId(UPDATED_ADMINISTRATIVE_UNIT_ID)
            .administrativeRegionId(UPDATED_ADMINISTRATIVE_REGION_ID)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        ProvinceDTO provinceDTO = provinceMapper.toDto(updatedProvince);

        restProvinceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, provinceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(provinceDTO))
            )
            .andExpect(status().isOk());

        // Validate the Province in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProvinceToMatchAllProperties(updatedProvince);
    }

    @Test
    @Transactional
    void putNonExistingProvince() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        province.setId(longCount.incrementAndGet());

        // Create the Province
        ProvinceDTO provinceDTO = provinceMapper.toDto(province);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProvinceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, provinceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(provinceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Province in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProvince() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        province.setId(longCount.incrementAndGet());

        // Create the Province
        ProvinceDTO provinceDTO = provinceMapper.toDto(province);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProvinceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(provinceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Province in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProvince() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        province.setId(longCount.incrementAndGet());

        // Create the Province
        ProvinceDTO provinceDTO = provinceMapper.toDto(province);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProvinceMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(provinceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Province in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProvinceWithPatch() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the province using partial update
        Province partialUpdatedProvince = new Province();
        partialUpdatedProvince.setId(province.getId());

        partialUpdatedProvince
            .provinceCode(UPDATED_PROVINCE_CODE)
            .fullName(UPDATED_FULL_NAME)
            .codeName(UPDATED_CODE_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedBy(UPDATED_DELETED_BY);

        restProvinceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProvince.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProvince))
            )
            .andExpect(status().isOk());

        // Validate the Province in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProvinceUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProvince, province), getPersistedProvince(province));
    }

    @Test
    @Transactional
    void fullUpdateProvinceWithPatch() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the province using partial update
        Province partialUpdatedProvince = new Province();
        partialUpdatedProvince.setId(province.getId());

        partialUpdatedProvince
            .provinceCode(UPDATED_PROVINCE_CODE)
            .name(UPDATED_NAME)
            .nameEn(UPDATED_NAME_EN)
            .fullName(UPDATED_FULL_NAME)
            .fullNameEn(UPDATED_FULL_NAME_EN)
            .codeName(UPDATED_CODE_NAME)
            .administrativeUnitId(UPDATED_ADMINISTRATIVE_UNIT_ID)
            .administrativeRegionId(UPDATED_ADMINISTRATIVE_REGION_ID)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restProvinceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProvince.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProvince))
            )
            .andExpect(status().isOk());

        // Validate the Province in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProvinceUpdatableFieldsEquals(partialUpdatedProvince, getPersistedProvince(partialUpdatedProvince));
    }

    @Test
    @Transactional
    void patchNonExistingProvince() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        province.setId(longCount.incrementAndGet());

        // Create the Province
        ProvinceDTO provinceDTO = provinceMapper.toDto(province);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProvinceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, provinceDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(provinceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Province in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProvince() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        province.setId(longCount.incrementAndGet());

        // Create the Province
        ProvinceDTO provinceDTO = provinceMapper.toDto(province);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProvinceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(provinceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Province in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProvince() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        province.setId(longCount.incrementAndGet());

        // Create the Province
        ProvinceDTO provinceDTO = provinceMapper.toDto(province);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProvinceMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(provinceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Province in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProvince() throws Exception {
        // Initialize the database
        insertedProvince = provinceRepository.saveAndFlush(province);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the province
        restProvinceMockMvc
            .perform(delete(ENTITY_API_URL_ID, province.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return provinceRepository.count();
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

    protected Province getPersistedProvince(Province province) {
        return provinceRepository.findById(province.getId()).orElseThrow();
    }

    protected void assertPersistedProvinceToMatchAllProperties(Province expectedProvince) {
        assertProvinceAllPropertiesEquals(expectedProvince, getPersistedProvince(expectedProvince));
    }

    protected void assertPersistedProvinceToMatchUpdatableProperties(Province expectedProvince) {
        assertProvinceAllUpdatablePropertiesEquals(expectedProvince, getPersistedProvince(expectedProvince));
    }
}
