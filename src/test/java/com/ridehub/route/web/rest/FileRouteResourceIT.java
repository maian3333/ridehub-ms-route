package com.ridehub.route.web.rest;

import static com.ridehub.route.domain.FileRouteAsserts.*;
import static com.ridehub.route.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.route.IntegrationTest;
import com.ridehub.route.domain.FileRoute;
import com.ridehub.route.repository.FileRouteRepository;
import com.ridehub.route.service.dto.FileRouteDTO;
import com.ridehub.route.service.mapper.FileRouteMapper;
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
 * Integration tests for the {@link FileRouteResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FileRouteResourceIT {

    private static final String DEFAULT_BUCKET = "AAAAAAAAAA";
    private static final String UPDATED_BUCKET = "BBBBBBBBBB";

    private static final String DEFAULT_OBJECT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_OBJECT_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT_TYPE = "BBBBBBBBBB";

    private static final Long DEFAULT_SIZE = 1L;
    private static final Long UPDATED_SIZE = 2L;
    private static final Long SMALLER_SIZE = 1L - 1L;

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

    private static final String ENTITY_API_URL = "/api/file-routes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FileRouteRepository fileRouteRepository;

    @Autowired
    private FileRouteMapper fileRouteMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFileRouteMockMvc;

    private FileRoute fileRoute;

    private FileRoute insertedFileRoute;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FileRoute createEntity() {
        return new FileRoute()
            .bucket(DEFAULT_BUCKET)
            .objectKey(DEFAULT_OBJECT_KEY)
            .contentType(DEFAULT_CONTENT_TYPE)
            .size(DEFAULT_SIZE)
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
    public static FileRoute createUpdatedEntity() {
        return new FileRoute()
            .bucket(UPDATED_BUCKET)
            .objectKey(UPDATED_OBJECT_KEY)
            .contentType(UPDATED_CONTENT_TYPE)
            .size(UPDATED_SIZE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
    }

    @BeforeEach
    void initTest() {
        fileRoute = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFileRoute != null) {
            fileRouteRepository.delete(insertedFileRoute);
            insertedFileRoute = null;
        }
    }

    @Test
    @Transactional
    void createFileRoute() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FileRoute
        FileRouteDTO fileRouteDTO = fileRouteMapper.toDto(fileRoute);
        var returnedFileRouteDTO = om.readValue(
            restFileRouteMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fileRouteDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FileRouteDTO.class
        );

        // Validate the FileRoute in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFileRoute = fileRouteMapper.toEntity(returnedFileRouteDTO);
        assertFileRouteUpdatableFieldsEquals(returnedFileRoute, getPersistedFileRoute(returnedFileRoute));

        insertedFileRoute = returnedFileRoute;
    }

    @Test
    @Transactional
    void createFileRouteWithExistingId() throws Exception {
        // Create the FileRoute with an existing ID
        fileRoute.setId(1L);
        FileRouteDTO fileRouteDTO = fileRouteMapper.toDto(fileRoute);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFileRouteMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fileRouteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FileRoute in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkBucketIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        fileRoute.setBucket(null);

        // Create the FileRoute, which fails.
        FileRouteDTO fileRouteDTO = fileRouteMapper.toDto(fileRoute);

        restFileRouteMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fileRouteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkObjectKeyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        fileRoute.setObjectKey(null);

        // Create the FileRoute, which fails.
        FileRouteDTO fileRouteDTO = fileRouteMapper.toDto(fileRoute);

        restFileRouteMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fileRouteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        fileRoute.setCreatedAt(null);

        // Create the FileRoute, which fails.
        FileRouteDTO fileRouteDTO = fileRouteMapper.toDto(fileRoute);

        restFileRouteMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fileRouteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFileRoutes() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList
        restFileRouteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fileRoute.getId().intValue())))
            .andExpect(jsonPath("$.[*].bucket").value(hasItem(DEFAULT_BUCKET)))
            .andExpect(jsonPath("$.[*].objectKey").value(hasItem(DEFAULT_OBJECT_KEY)))
            .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].size").value(hasItem(DEFAULT_SIZE.intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));
    }

    @Test
    @Transactional
    void getFileRoute() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get the fileRoute
        restFileRouteMockMvc
            .perform(get(ENTITY_API_URL_ID, fileRoute.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(fileRoute.getId().intValue()))
            .andExpect(jsonPath("$.bucket").value(DEFAULT_BUCKET))
            .andExpect(jsonPath("$.objectKey").value(DEFAULT_OBJECT_KEY))
            .andExpect(jsonPath("$.contentType").value(DEFAULT_CONTENT_TYPE))
            .andExpect(jsonPath("$.size").value(DEFAULT_SIZE.intValue()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()))
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY.toString()));
    }

    @Test
    @Transactional
    void getFileRoutesByIdFiltering() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        Long id = fileRoute.getId();

        defaultFileRouteFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultFileRouteFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultFileRouteFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFileRoutesByBucketIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where bucket equals to
        defaultFileRouteFiltering("bucket.equals=" + DEFAULT_BUCKET, "bucket.equals=" + UPDATED_BUCKET);
    }

    @Test
    @Transactional
    void getAllFileRoutesByBucketIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where bucket in
        defaultFileRouteFiltering("bucket.in=" + DEFAULT_BUCKET + "," + UPDATED_BUCKET, "bucket.in=" + UPDATED_BUCKET);
    }

    @Test
    @Transactional
    void getAllFileRoutesByBucketIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where bucket is not null
        defaultFileRouteFiltering("bucket.specified=true", "bucket.specified=false");
    }

    @Test
    @Transactional
    void getAllFileRoutesByBucketContainsSomething() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where bucket contains
        defaultFileRouteFiltering("bucket.contains=" + DEFAULT_BUCKET, "bucket.contains=" + UPDATED_BUCKET);
    }

    @Test
    @Transactional
    void getAllFileRoutesByBucketNotContainsSomething() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where bucket does not contain
        defaultFileRouteFiltering("bucket.doesNotContain=" + UPDATED_BUCKET, "bucket.doesNotContain=" + DEFAULT_BUCKET);
    }

    @Test
    @Transactional
    void getAllFileRoutesByObjectKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where objectKey equals to
        defaultFileRouteFiltering("objectKey.equals=" + DEFAULT_OBJECT_KEY, "objectKey.equals=" + UPDATED_OBJECT_KEY);
    }

    @Test
    @Transactional
    void getAllFileRoutesByObjectKeyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where objectKey in
        defaultFileRouteFiltering("objectKey.in=" + DEFAULT_OBJECT_KEY + "," + UPDATED_OBJECT_KEY, "objectKey.in=" + UPDATED_OBJECT_KEY);
    }

    @Test
    @Transactional
    void getAllFileRoutesByObjectKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where objectKey is not null
        defaultFileRouteFiltering("objectKey.specified=true", "objectKey.specified=false");
    }

    @Test
    @Transactional
    void getAllFileRoutesByObjectKeyContainsSomething() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where objectKey contains
        defaultFileRouteFiltering("objectKey.contains=" + DEFAULT_OBJECT_KEY, "objectKey.contains=" + UPDATED_OBJECT_KEY);
    }

    @Test
    @Transactional
    void getAllFileRoutesByObjectKeyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where objectKey does not contain
        defaultFileRouteFiltering("objectKey.doesNotContain=" + UPDATED_OBJECT_KEY, "objectKey.doesNotContain=" + DEFAULT_OBJECT_KEY);
    }

    @Test
    @Transactional
    void getAllFileRoutesByContentTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where contentType equals to
        defaultFileRouteFiltering("contentType.equals=" + DEFAULT_CONTENT_TYPE, "contentType.equals=" + UPDATED_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void getAllFileRoutesByContentTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where contentType in
        defaultFileRouteFiltering(
            "contentType.in=" + DEFAULT_CONTENT_TYPE + "," + UPDATED_CONTENT_TYPE,
            "contentType.in=" + UPDATED_CONTENT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllFileRoutesByContentTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where contentType is not null
        defaultFileRouteFiltering("contentType.specified=true", "contentType.specified=false");
    }

    @Test
    @Transactional
    void getAllFileRoutesByContentTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where contentType contains
        defaultFileRouteFiltering("contentType.contains=" + DEFAULT_CONTENT_TYPE, "contentType.contains=" + UPDATED_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void getAllFileRoutesByContentTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where contentType does not contain
        defaultFileRouteFiltering(
            "contentType.doesNotContain=" + UPDATED_CONTENT_TYPE,
            "contentType.doesNotContain=" + DEFAULT_CONTENT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllFileRoutesBySizeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where size equals to
        defaultFileRouteFiltering("size.equals=" + DEFAULT_SIZE, "size.equals=" + UPDATED_SIZE);
    }

    @Test
    @Transactional
    void getAllFileRoutesBySizeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where size in
        defaultFileRouteFiltering("size.in=" + DEFAULT_SIZE + "," + UPDATED_SIZE, "size.in=" + UPDATED_SIZE);
    }

    @Test
    @Transactional
    void getAllFileRoutesBySizeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where size is not null
        defaultFileRouteFiltering("size.specified=true", "size.specified=false");
    }

    @Test
    @Transactional
    void getAllFileRoutesBySizeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where size is greater than or equal to
        defaultFileRouteFiltering("size.greaterThanOrEqual=" + DEFAULT_SIZE, "size.greaterThanOrEqual=" + UPDATED_SIZE);
    }

    @Test
    @Transactional
    void getAllFileRoutesBySizeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where size is less than or equal to
        defaultFileRouteFiltering("size.lessThanOrEqual=" + DEFAULT_SIZE, "size.lessThanOrEqual=" + SMALLER_SIZE);
    }

    @Test
    @Transactional
    void getAllFileRoutesBySizeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where size is less than
        defaultFileRouteFiltering("size.lessThan=" + UPDATED_SIZE, "size.lessThan=" + DEFAULT_SIZE);
    }

    @Test
    @Transactional
    void getAllFileRoutesBySizeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where size is greater than
        defaultFileRouteFiltering("size.greaterThan=" + SMALLER_SIZE, "size.greaterThan=" + DEFAULT_SIZE);
    }

    @Test
    @Transactional
    void getAllFileRoutesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where createdAt equals to
        defaultFileRouteFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllFileRoutesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where createdAt in
        defaultFileRouteFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllFileRoutesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where createdAt is not null
        defaultFileRouteFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllFileRoutesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where updatedAt equals to
        defaultFileRouteFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllFileRoutesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where updatedAt in
        defaultFileRouteFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllFileRoutesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where updatedAt is not null
        defaultFileRouteFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllFileRoutesByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where isDeleted equals to
        defaultFileRouteFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllFileRoutesByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where isDeleted in
        defaultFileRouteFiltering("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED, "isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllFileRoutesByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where isDeleted is not null
        defaultFileRouteFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllFileRoutesByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where deletedAt equals to
        defaultFileRouteFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllFileRoutesByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where deletedAt in
        defaultFileRouteFiltering("deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT, "deletedAt.in=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllFileRoutesByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where deletedAt is not null
        defaultFileRouteFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllFileRoutesByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where deletedBy equals to
        defaultFileRouteFiltering("deletedBy.equals=" + DEFAULT_DELETED_BY, "deletedBy.equals=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllFileRoutesByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where deletedBy in
        defaultFileRouteFiltering("deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY, "deletedBy.in=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllFileRoutesByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        // Get all the fileRouteList where deletedBy is not null
        defaultFileRouteFiltering("deletedBy.specified=true", "deletedBy.specified=false");
    }

    private void defaultFileRouteFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultFileRouteShouldBeFound(shouldBeFound);
        defaultFileRouteShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFileRouteShouldBeFound(String filter) throws Exception {
        restFileRouteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fileRoute.getId().intValue())))
            .andExpect(jsonPath("$.[*].bucket").value(hasItem(DEFAULT_BUCKET)))
            .andExpect(jsonPath("$.[*].objectKey").value(hasItem(DEFAULT_OBJECT_KEY)))
            .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].size").value(hasItem(DEFAULT_SIZE.intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));

        // Check, that the count call also returns 1
        restFileRouteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFileRouteShouldNotBeFound(String filter) throws Exception {
        restFileRouteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFileRouteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingFileRoute() throws Exception {
        // Get the fileRoute
        restFileRouteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFileRoute() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fileRoute
        FileRoute updatedFileRoute = fileRouteRepository.findById(fileRoute.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFileRoute are not directly saved in db
        em.detach(updatedFileRoute);
        updatedFileRoute
            .bucket(UPDATED_BUCKET)
            .objectKey(UPDATED_OBJECT_KEY)
            .contentType(UPDATED_CONTENT_TYPE)
            .size(UPDATED_SIZE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        FileRouteDTO fileRouteDTO = fileRouteMapper.toDto(updatedFileRoute);

        restFileRouteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, fileRouteDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(fileRouteDTO))
            )
            .andExpect(status().isOk());

        // Validate the FileRoute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFileRouteToMatchAllProperties(updatedFileRoute);
    }

    @Test
    @Transactional
    void putNonExistingFileRoute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileRoute.setId(longCount.incrementAndGet());

        // Create the FileRoute
        FileRouteDTO fileRouteDTO = fileRouteMapper.toDto(fileRoute);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFileRouteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, fileRouteDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(fileRouteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FileRoute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFileRoute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileRoute.setId(longCount.incrementAndGet());

        // Create the FileRoute
        FileRouteDTO fileRouteDTO = fileRouteMapper.toDto(fileRoute);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFileRouteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(fileRouteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FileRoute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFileRoute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileRoute.setId(longCount.incrementAndGet());

        // Create the FileRoute
        FileRouteDTO fileRouteDTO = fileRouteMapper.toDto(fileRoute);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFileRouteMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fileRouteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FileRoute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFileRouteWithPatch() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fileRoute using partial update
        FileRoute partialUpdatedFileRoute = new FileRoute();
        partialUpdatedFileRoute.setId(fileRoute.getId());

        partialUpdatedFileRoute.createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        restFileRouteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFileRoute.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFileRoute))
            )
            .andExpect(status().isOk());

        // Validate the FileRoute in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFileRouteUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFileRoute, fileRoute),
            getPersistedFileRoute(fileRoute)
        );
    }

    @Test
    @Transactional
    void fullUpdateFileRouteWithPatch() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fileRoute using partial update
        FileRoute partialUpdatedFileRoute = new FileRoute();
        partialUpdatedFileRoute.setId(fileRoute.getId());

        partialUpdatedFileRoute
            .bucket(UPDATED_BUCKET)
            .objectKey(UPDATED_OBJECT_KEY)
            .contentType(UPDATED_CONTENT_TYPE)
            .size(UPDATED_SIZE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restFileRouteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFileRoute.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFileRoute))
            )
            .andExpect(status().isOk());

        // Validate the FileRoute in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFileRouteUpdatableFieldsEquals(partialUpdatedFileRoute, getPersistedFileRoute(partialUpdatedFileRoute));
    }

    @Test
    @Transactional
    void patchNonExistingFileRoute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileRoute.setId(longCount.incrementAndGet());

        // Create the FileRoute
        FileRouteDTO fileRouteDTO = fileRouteMapper.toDto(fileRoute);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFileRouteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, fileRouteDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(fileRouteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FileRoute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFileRoute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileRoute.setId(longCount.incrementAndGet());

        // Create the FileRoute
        FileRouteDTO fileRouteDTO = fileRouteMapper.toDto(fileRoute);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFileRouteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(fileRouteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FileRoute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFileRoute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileRoute.setId(longCount.incrementAndGet());

        // Create the FileRoute
        FileRouteDTO fileRouteDTO = fileRouteMapper.toDto(fileRoute);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFileRouteMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(fileRouteDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FileRoute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFileRoute() throws Exception {
        // Initialize the database
        insertedFileRoute = fileRouteRepository.saveAndFlush(fileRoute);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the fileRoute
        restFileRouteMockMvc
            .perform(delete(ENTITY_API_URL_ID, fileRoute.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return fileRouteRepository.count();
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

    protected FileRoute getPersistedFileRoute(FileRoute fileRoute) {
        return fileRouteRepository.findById(fileRoute.getId()).orElseThrow();
    }

    protected void assertPersistedFileRouteToMatchAllProperties(FileRoute expectedFileRoute) {
        assertFileRouteAllPropertiesEquals(expectedFileRoute, getPersistedFileRoute(expectedFileRoute));
    }

    protected void assertPersistedFileRouteToMatchUpdatableProperties(FileRoute expectedFileRoute) {
        assertFileRouteAllUpdatablePropertiesEquals(expectedFileRoute, getPersistedFileRoute(expectedFileRoute));
    }
}
