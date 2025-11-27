package com.ridehub.route.web.rest;

import static com.ridehub.route.domain.StaffAsserts.*;
import static com.ridehub.route.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.route.IntegrationTest;
import com.ridehub.route.domain.Staff;
import com.ridehub.route.domain.enumeration.Gender;
import com.ridehub.route.domain.enumeration.StaffStatus;
import com.ridehub.route.repository.StaffRepository;
import com.ridehub.route.service.dto.StaffDTO;
import com.ridehub.route.service.mapper.StaffMapper;
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
 * Integration tests for the {@link StaffResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StaffResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_AGE = 1;
    private static final Integer UPDATED_AGE = 2;
    private static final Integer SMALLER_AGE = 1 - 1;

    private static final Gender DEFAULT_GENDER = Gender.MALE;
    private static final Gender UPDATED_GENDER = Gender.FEMALE;

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final StaffStatus DEFAULT_STATUS = StaffStatus.ACTIVE;
    private static final StaffStatus UPDATED_STATUS = StaffStatus.INACTIVE;

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

    private static final String ENTITY_API_URL = "/api/staff";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StaffMapper staffMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStaffMockMvc;

    private Staff staff;

    private Staff insertedStaff;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Staff createEntity() {
        return new Staff()
            .name(DEFAULT_NAME)
            .age(DEFAULT_AGE)
            .gender(DEFAULT_GENDER)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .status(DEFAULT_STATUS)
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
    public static Staff createUpdatedEntity() {
        return new Staff()
            .name(UPDATED_NAME)
            .age(UPDATED_AGE)
            .gender(UPDATED_GENDER)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
    }

    @BeforeEach
    void initTest() {
        staff = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedStaff != null) {
            staffRepository.delete(insertedStaff);
            insertedStaff = null;
        }
    }

    @Test
    @Transactional
    void createStaff() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);
        var returnedStaffDTO = om.readValue(
            restStaffMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(staffDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StaffDTO.class
        );

        // Validate the Staff in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStaff = staffMapper.toEntity(returnedStaffDTO);
        assertStaffUpdatableFieldsEquals(returnedStaff, getPersistedStaff(returnedStaff));

        insertedStaff = returnedStaff;
    }

    @Test
    @Transactional
    void createStaffWithExistingId() throws Exception {
        // Create the Staff with an existing ID
        staff.setId(1L);
        StaffDTO staffDTO = staffMapper.toDto(staff);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStaffMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(staffDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Staff in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        staff.setName(null);

        // Create the Staff, which fails.
        StaffDTO staffDTO = staffMapper.toDto(staff);

        restStaffMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(staffDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        staff.setCreatedAt(null);

        // Create the Staff, which fails.
        StaffDTO staffDTO = staffMapper.toDto(staff);

        restStaffMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(staffDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStaff() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList
        restStaffMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(staff.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].age").value(hasItem(DEFAULT_AGE)))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));
    }

    @Test
    @Transactional
    void getStaff() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get the staff
        restStaffMockMvc
            .perform(get(ENTITY_API_URL_ID, staff.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(staff.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.age").value(DEFAULT_AGE))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER.toString()))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()))
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY.toString()));
    }

    @Test
    @Transactional
    void getStaffByIdFiltering() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        Long id = staff.getId();

        defaultStaffFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultStaffFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultStaffFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStaffByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where name equals to
        defaultStaffFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStaffByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where name in
        defaultStaffFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStaffByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where name is not null
        defaultStaffFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllStaffByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where name contains
        defaultStaffFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStaffByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where name does not contain
        defaultStaffFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllStaffByAgeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where age equals to
        defaultStaffFiltering("age.equals=" + DEFAULT_AGE, "age.equals=" + UPDATED_AGE);
    }

    @Test
    @Transactional
    void getAllStaffByAgeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where age in
        defaultStaffFiltering("age.in=" + DEFAULT_AGE + "," + UPDATED_AGE, "age.in=" + UPDATED_AGE);
    }

    @Test
    @Transactional
    void getAllStaffByAgeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where age is not null
        defaultStaffFiltering("age.specified=true", "age.specified=false");
    }

    @Test
    @Transactional
    void getAllStaffByAgeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where age is greater than or equal to
        defaultStaffFiltering("age.greaterThanOrEqual=" + DEFAULT_AGE, "age.greaterThanOrEqual=" + UPDATED_AGE);
    }

    @Test
    @Transactional
    void getAllStaffByAgeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where age is less than or equal to
        defaultStaffFiltering("age.lessThanOrEqual=" + DEFAULT_AGE, "age.lessThanOrEqual=" + SMALLER_AGE);
    }

    @Test
    @Transactional
    void getAllStaffByAgeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where age is less than
        defaultStaffFiltering("age.lessThan=" + UPDATED_AGE, "age.lessThan=" + DEFAULT_AGE);
    }

    @Test
    @Transactional
    void getAllStaffByAgeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where age is greater than
        defaultStaffFiltering("age.greaterThan=" + SMALLER_AGE, "age.greaterThan=" + DEFAULT_AGE);
    }

    @Test
    @Transactional
    void getAllStaffByGenderIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where gender equals to
        defaultStaffFiltering("gender.equals=" + DEFAULT_GENDER, "gender.equals=" + UPDATED_GENDER);
    }

    @Test
    @Transactional
    void getAllStaffByGenderIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where gender in
        defaultStaffFiltering("gender.in=" + DEFAULT_GENDER + "," + UPDATED_GENDER, "gender.in=" + UPDATED_GENDER);
    }

    @Test
    @Transactional
    void getAllStaffByGenderIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where gender is not null
        defaultStaffFiltering("gender.specified=true", "gender.specified=false");
    }

    @Test
    @Transactional
    void getAllStaffByPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where phoneNumber equals to
        defaultStaffFiltering("phoneNumber.equals=" + DEFAULT_PHONE_NUMBER, "phoneNumber.equals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllStaffByPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where phoneNumber in
        defaultStaffFiltering(
            "phoneNumber.in=" + DEFAULT_PHONE_NUMBER + "," + UPDATED_PHONE_NUMBER,
            "phoneNumber.in=" + UPDATED_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllStaffByPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where phoneNumber is not null
        defaultStaffFiltering("phoneNumber.specified=true", "phoneNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllStaffByPhoneNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where phoneNumber contains
        defaultStaffFiltering("phoneNumber.contains=" + DEFAULT_PHONE_NUMBER, "phoneNumber.contains=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllStaffByPhoneNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where phoneNumber does not contain
        defaultStaffFiltering("phoneNumber.doesNotContain=" + UPDATED_PHONE_NUMBER, "phoneNumber.doesNotContain=" + DEFAULT_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllStaffByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where status equals to
        defaultStaffFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllStaffByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where status in
        defaultStaffFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllStaffByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where status is not null
        defaultStaffFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllStaffByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where createdAt equals to
        defaultStaffFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllStaffByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where createdAt in
        defaultStaffFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllStaffByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where createdAt is not null
        defaultStaffFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllStaffByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where updatedAt equals to
        defaultStaffFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllStaffByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where updatedAt in
        defaultStaffFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllStaffByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where updatedAt is not null
        defaultStaffFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllStaffByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where isDeleted equals to
        defaultStaffFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllStaffByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where isDeleted in
        defaultStaffFiltering("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED, "isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllStaffByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where isDeleted is not null
        defaultStaffFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllStaffByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where deletedAt equals to
        defaultStaffFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllStaffByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where deletedAt in
        defaultStaffFiltering("deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT, "deletedAt.in=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllStaffByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where deletedAt is not null
        defaultStaffFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllStaffByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where deletedBy equals to
        defaultStaffFiltering("deletedBy.equals=" + DEFAULT_DELETED_BY, "deletedBy.equals=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllStaffByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where deletedBy in
        defaultStaffFiltering("deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY, "deletedBy.in=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllStaffByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where deletedBy is not null
        defaultStaffFiltering("deletedBy.specified=true", "deletedBy.specified=false");
    }

    private void defaultStaffFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultStaffShouldBeFound(shouldBeFound);
        defaultStaffShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStaffShouldBeFound(String filter) throws Exception {
        restStaffMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(staff.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].age").value(hasItem(DEFAULT_AGE)))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));

        // Check, that the count call also returns 1
        restStaffMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStaffShouldNotBeFound(String filter) throws Exception {
        restStaffMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStaffMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStaff() throws Exception {
        // Get the staff
        restStaffMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStaff() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the staff
        Staff updatedStaff = staffRepository.findById(staff.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStaff are not directly saved in db
        em.detach(updatedStaff);
        updatedStaff
            .name(UPDATED_NAME)
            .age(UPDATED_AGE)
            .gender(UPDATED_GENDER)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        StaffDTO staffDTO = staffMapper.toDto(updatedStaff);

        restStaffMockMvc
            .perform(
                put(ENTITY_API_URL_ID, staffDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(staffDTO))
            )
            .andExpect(status().isOk());

        // Validate the Staff in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStaffToMatchAllProperties(updatedStaff);
    }

    @Test
    @Transactional
    void putNonExistingStaff() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        staff.setId(longCount.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStaffMockMvc
            .perform(
                put(ENTITY_API_URL_ID, staffDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(staffDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Staff in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStaff() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        staff.setId(longCount.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaffMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(staffDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Staff in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStaff() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        staff.setId(longCount.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaffMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(staffDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Staff in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStaffWithPatch() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the staff using partial update
        Staff partialUpdatedStaff = new Staff();
        partialUpdatedStaff.setId(staff.getId());

        partialUpdatedStaff
            .gender(UPDATED_GENDER)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restStaffMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStaff.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStaff))
            )
            .andExpect(status().isOk());

        // Validate the Staff in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStaffUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedStaff, staff), getPersistedStaff(staff));
    }

    @Test
    @Transactional
    void fullUpdateStaffWithPatch() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the staff using partial update
        Staff partialUpdatedStaff = new Staff();
        partialUpdatedStaff.setId(staff.getId());

        partialUpdatedStaff
            .name(UPDATED_NAME)
            .age(UPDATED_AGE)
            .gender(UPDATED_GENDER)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restStaffMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStaff.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStaff))
            )
            .andExpect(status().isOk());

        // Validate the Staff in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStaffUpdatableFieldsEquals(partialUpdatedStaff, getPersistedStaff(partialUpdatedStaff));
    }

    @Test
    @Transactional
    void patchNonExistingStaff() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        staff.setId(longCount.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStaffMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, staffDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(staffDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Staff in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStaff() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        staff.setId(longCount.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaffMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(staffDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Staff in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStaff() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        staff.setId(longCount.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaffMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(staffDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Staff in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStaff() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the staff
        restStaffMockMvc
            .perform(delete(ENTITY_API_URL_ID, staff.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return staffRepository.count();
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

    protected Staff getPersistedStaff(Staff staff) {
        return staffRepository.findById(staff.getId()).orElseThrow();
    }

    protected void assertPersistedStaffToMatchAllProperties(Staff expectedStaff) {
        assertStaffAllPropertiesEquals(expectedStaff, getPersistedStaff(expectedStaff));
    }

    protected void assertPersistedStaffToMatchUpdatableProperties(Staff expectedStaff) {
        assertStaffAllUpdatablePropertiesEquals(expectedStaff, getPersistedStaff(expectedStaff));
    }
}
