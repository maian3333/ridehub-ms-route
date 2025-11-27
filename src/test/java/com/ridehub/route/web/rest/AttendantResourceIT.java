package com.ridehub.route.web.rest;

import static com.ridehub.route.domain.AttendantAsserts.*;
import static com.ridehub.route.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.route.IntegrationTest;
import com.ridehub.route.domain.Attendant;
import com.ridehub.route.domain.Staff;
import com.ridehub.route.repository.AttendantRepository;
import com.ridehub.route.service.dto.AttendantDTO;
import com.ridehub.route.service.mapper.AttendantMapper;
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
 * Integration tests for the {@link AttendantResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AttendantResourceIT {

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

    private static final String ENTITY_API_URL = "/api/attendants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AttendantRepository attendantRepository;

    @Autowired
    private AttendantMapper attendantMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAttendantMockMvc;

    private Attendant attendant;

    private Attendant insertedAttendant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Attendant createEntity(EntityManager em) {
        Attendant attendant = new Attendant()
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .isDeleted(DEFAULT_IS_DELETED)
            .deletedAt(DEFAULT_DELETED_AT)
            .deletedBy(DEFAULT_DELETED_BY);
        // Add required entity
        Staff staff;
        if (TestUtil.findAll(em, Staff.class).isEmpty()) {
            staff = StaffResourceIT.createEntity();
            em.persist(staff);
            em.flush();
        } else {
            staff = TestUtil.findAll(em, Staff.class).get(0);
        }
        attendant.setStaff(staff);
        return attendant;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Attendant createUpdatedEntity(EntityManager em) {
        Attendant updatedAttendant = new Attendant()
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        // Add required entity
        Staff staff;
        if (TestUtil.findAll(em, Staff.class).isEmpty()) {
            staff = StaffResourceIT.createUpdatedEntity();
            em.persist(staff);
            em.flush();
        } else {
            staff = TestUtil.findAll(em, Staff.class).get(0);
        }
        updatedAttendant.setStaff(staff);
        return updatedAttendant;
    }

    @BeforeEach
    void initTest() {
        attendant = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedAttendant != null) {
            attendantRepository.delete(insertedAttendant);
            insertedAttendant = null;
        }
    }

    @Test
    @Transactional
    void createAttendant() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Attendant
        AttendantDTO attendantDTO = attendantMapper.toDto(attendant);
        var returnedAttendantDTO = om.readValue(
            restAttendantMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attendantDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AttendantDTO.class
        );

        // Validate the Attendant in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAttendant = attendantMapper.toEntity(returnedAttendantDTO);
        assertAttendantUpdatableFieldsEquals(returnedAttendant, getPersistedAttendant(returnedAttendant));

        insertedAttendant = returnedAttendant;
    }

    @Test
    @Transactional
    void createAttendantWithExistingId() throws Exception {
        // Create the Attendant with an existing ID
        attendant.setId(1L);
        AttendantDTO attendantDTO = attendantMapper.toDto(attendant);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAttendantMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attendantDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Attendant in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        attendant.setCreatedAt(null);

        // Create the Attendant, which fails.
        AttendantDTO attendantDTO = attendantMapper.toDto(attendant);

        restAttendantMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attendantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAttendants() throws Exception {
        // Initialize the database
        insertedAttendant = attendantRepository.saveAndFlush(attendant);

        // Get all the attendantList
        restAttendantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(attendant.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));
    }

    @Test
    @Transactional
    void getAttendant() throws Exception {
        // Initialize the database
        insertedAttendant = attendantRepository.saveAndFlush(attendant);

        // Get the attendant
        restAttendantMockMvc
            .perform(get(ENTITY_API_URL_ID, attendant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(attendant.getId().intValue()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED))
            .andExpect(jsonPath("$.deletedAt").value(DEFAULT_DELETED_AT.toString()))
            .andExpect(jsonPath("$.deletedBy").value(DEFAULT_DELETED_BY.toString()));
    }

    @Test
    @Transactional
    void getAttendantsByIdFiltering() throws Exception {
        // Initialize the database
        insertedAttendant = attendantRepository.saveAndFlush(attendant);

        Long id = attendant.getId();

        defaultAttendantFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAttendantFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAttendantFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAttendantsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAttendant = attendantRepository.saveAndFlush(attendant);

        // Get all the attendantList where createdAt equals to
        defaultAttendantFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllAttendantsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAttendant = attendantRepository.saveAndFlush(attendant);

        // Get all the attendantList where createdAt in
        defaultAttendantFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllAttendantsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAttendant = attendantRepository.saveAndFlush(attendant);

        // Get all the attendantList where createdAt is not null
        defaultAttendantFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllAttendantsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAttendant = attendantRepository.saveAndFlush(attendant);

        // Get all the attendantList where updatedAt equals to
        defaultAttendantFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllAttendantsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAttendant = attendantRepository.saveAndFlush(attendant);

        // Get all the attendantList where updatedAt in
        defaultAttendantFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllAttendantsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAttendant = attendantRepository.saveAndFlush(attendant);

        // Get all the attendantList where updatedAt is not null
        defaultAttendantFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllAttendantsByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAttendant = attendantRepository.saveAndFlush(attendant);

        // Get all the attendantList where isDeleted equals to
        defaultAttendantFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllAttendantsByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAttendant = attendantRepository.saveAndFlush(attendant);

        // Get all the attendantList where isDeleted in
        defaultAttendantFiltering("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED, "isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllAttendantsByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAttendant = attendantRepository.saveAndFlush(attendant);

        // Get all the attendantList where isDeleted is not null
        defaultAttendantFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllAttendantsByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAttendant = attendantRepository.saveAndFlush(attendant);

        // Get all the attendantList where deletedAt equals to
        defaultAttendantFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllAttendantsByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAttendant = attendantRepository.saveAndFlush(attendant);

        // Get all the attendantList where deletedAt in
        defaultAttendantFiltering("deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT, "deletedAt.in=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllAttendantsByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAttendant = attendantRepository.saveAndFlush(attendant);

        // Get all the attendantList where deletedAt is not null
        defaultAttendantFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllAttendantsByDeletedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAttendant = attendantRepository.saveAndFlush(attendant);

        // Get all the attendantList where deletedBy equals to
        defaultAttendantFiltering("deletedBy.equals=" + DEFAULT_DELETED_BY, "deletedBy.equals=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllAttendantsByDeletedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAttendant = attendantRepository.saveAndFlush(attendant);

        // Get all the attendantList where deletedBy in
        defaultAttendantFiltering("deletedBy.in=" + DEFAULT_DELETED_BY + "," + UPDATED_DELETED_BY, "deletedBy.in=" + UPDATED_DELETED_BY);
    }

    @Test
    @Transactional
    void getAllAttendantsByDeletedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAttendant = attendantRepository.saveAndFlush(attendant);

        // Get all the attendantList where deletedBy is not null
        defaultAttendantFiltering("deletedBy.specified=true", "deletedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllAttendantsByStaffIsEqualToSomething() throws Exception {
        // Get already existing entity
        Staff staff = attendant.getStaff();
        attendantRepository.saveAndFlush(attendant);
        Long staffId = staff.getId();
        // Get all the attendantList where staff equals to staffId
        defaultAttendantShouldBeFound("staffId.equals=" + staffId);

        // Get all the attendantList where staff equals to (staffId + 1)
        defaultAttendantShouldNotBeFound("staffId.equals=" + (staffId + 1));
    }

    private void defaultAttendantFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAttendantShouldBeFound(shouldBeFound);
        defaultAttendantShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAttendantShouldBeFound(String filter) throws Exception {
        restAttendantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(attendant.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(DEFAULT_DELETED_AT.toString())))
            .andExpect(jsonPath("$.[*].deletedBy").value(hasItem(DEFAULT_DELETED_BY.toString())));

        // Check, that the count call also returns 1
        restAttendantMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAttendantShouldNotBeFound(String filter) throws Exception {
        restAttendantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAttendantMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAttendant() throws Exception {
        // Get the attendant
        restAttendantMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAttendant() throws Exception {
        // Initialize the database
        insertedAttendant = attendantRepository.saveAndFlush(attendant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the attendant
        Attendant updatedAttendant = attendantRepository.findById(attendant.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAttendant are not directly saved in db
        em.detach(updatedAttendant);
        updatedAttendant
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);
        AttendantDTO attendantDTO = attendantMapper.toDto(updatedAttendant);

        restAttendantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, attendantDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(attendantDTO))
            )
            .andExpect(status().isOk());

        // Validate the Attendant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAttendantToMatchAllProperties(updatedAttendant);
    }

    @Test
    @Transactional
    void putNonExistingAttendant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attendant.setId(longCount.incrementAndGet());

        // Create the Attendant
        AttendantDTO attendantDTO = attendantMapper.toDto(attendant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAttendantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, attendantDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(attendantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Attendant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAttendant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attendant.setId(longCount.incrementAndGet());

        // Create the Attendant
        AttendantDTO attendantDTO = attendantMapper.toDto(attendant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttendantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(attendantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Attendant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAttendant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attendant.setId(longCount.incrementAndGet());

        // Create the Attendant
        AttendantDTO attendantDTO = attendantMapper.toDto(attendant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttendantMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attendantDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Attendant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAttendantWithPatch() throws Exception {
        // Initialize the database
        insertedAttendant = attendantRepository.saveAndFlush(attendant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the attendant using partial update
        Attendant partialUpdatedAttendant = new Attendant();
        partialUpdatedAttendant.setId(attendant.getId());

        partialUpdatedAttendant
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restAttendantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAttendant.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAttendant))
            )
            .andExpect(status().isOk());

        // Validate the Attendant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAttendantUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAttendant, attendant),
            getPersistedAttendant(attendant)
        );
    }

    @Test
    @Transactional
    void fullUpdateAttendantWithPatch() throws Exception {
        // Initialize the database
        insertedAttendant = attendantRepository.saveAndFlush(attendant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the attendant using partial update
        Attendant partialUpdatedAttendant = new Attendant();
        partialUpdatedAttendant.setId(attendant.getId());

        partialUpdatedAttendant
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .isDeleted(UPDATED_IS_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .deletedBy(UPDATED_DELETED_BY);

        restAttendantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAttendant.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAttendant))
            )
            .andExpect(status().isOk());

        // Validate the Attendant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAttendantUpdatableFieldsEquals(partialUpdatedAttendant, getPersistedAttendant(partialUpdatedAttendant));
    }

    @Test
    @Transactional
    void patchNonExistingAttendant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attendant.setId(longCount.incrementAndGet());

        // Create the Attendant
        AttendantDTO attendantDTO = attendantMapper.toDto(attendant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAttendantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, attendantDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(attendantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Attendant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAttendant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attendant.setId(longCount.incrementAndGet());

        // Create the Attendant
        AttendantDTO attendantDTO = attendantMapper.toDto(attendant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttendantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(attendantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Attendant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAttendant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attendant.setId(longCount.incrementAndGet());

        // Create the Attendant
        AttendantDTO attendantDTO = attendantMapper.toDto(attendant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttendantMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(attendantDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Attendant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAttendant() throws Exception {
        // Initialize the database
        insertedAttendant = attendantRepository.saveAndFlush(attendant);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the attendant
        restAttendantMockMvc
            .perform(delete(ENTITY_API_URL_ID, attendant.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return attendantRepository.count();
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

    protected Attendant getPersistedAttendant(Attendant attendant) {
        return attendantRepository.findById(attendant.getId()).orElseThrow();
    }

    protected void assertPersistedAttendantToMatchAllProperties(Attendant expectedAttendant) {
        assertAttendantAllPropertiesEquals(expectedAttendant, getPersistedAttendant(expectedAttendant));
    }

    protected void assertPersistedAttendantToMatchUpdatableProperties(Attendant expectedAttendant) {
        assertAttendantAllUpdatablePropertiesEquals(expectedAttendant, getPersistedAttendant(expectedAttendant));
    }
}
