package com.ridehub.route.service.impl;

import com.ridehub.route.domain.Attendant;
import com.ridehub.route.domain.Staff;
import com.ridehub.route.domain.enumeration.StaffStatus;
import com.ridehub.route.repository.AttendantRepository;
import com.ridehub.route.repository.StaffRepository;
import com.ridehub.route.service.AttendantService;
import com.ridehub.route.service.dto.AttendantDTO;
import com.ridehub.route.service.dto.request.SimpleAttendantRequestDTO;
import com.ridehub.route.service.dto.response.SimpleAttendantResponseDTO;
import com.ridehub.route.service.mapper.AttendantMapper;
import com.ridehub.route.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing
 * {@link com.ridehub.route.domain.Attendant}.
 */
@Service
@Transactional
public class AttendantServiceImpl implements AttendantService {

    private static final Logger LOG = LoggerFactory.getLogger(AttendantServiceImpl.class);

    private final AttendantRepository attendantRepository;

    private final StaffRepository staffRepository;

    private final AttendantMapper attendantMapper;

    public AttendantServiceImpl(AttendantRepository attendantRepository, StaffRepository staffRepository,
            AttendantMapper attendantMapper) {
        this.attendantRepository = attendantRepository;
        this.staffRepository = staffRepository;
        this.attendantMapper = attendantMapper;
    }

    @Override
    public AttendantDTO save(AttendantDTO attendantDTO) {
        LOG.debug("Request to save Attendant : {}", attendantDTO);
        Attendant attendant = attendantMapper.toEntity(attendantDTO);
        attendant = attendantRepository.save(attendant);
        return attendantMapper.toDto(attendant);
    }

    @Override
    public AttendantDTO update(AttendantDTO attendantDTO) {
        LOG.debug("Request to update Attendant : {}", attendantDTO);
        Attendant attendant = attendantMapper.toEntity(attendantDTO);
        attendant = attendantRepository.save(attendant);
        return attendantMapper.toDto(attendant);
    }

    @Override
    public Optional<AttendantDTO> partialUpdate(AttendantDTO attendantDTO) {
        LOG.debug("Request to partially update Attendant : {}", attendantDTO);

        return attendantRepository
                .findById(attendantDTO.getId())
                .map(existingAttendant -> {
                    attendantMapper.partialUpdate(existingAttendant, attendantDTO);

                    return existingAttendant;
                })
                .map(attendantRepository::save)
                .map(attendantMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AttendantDTO> findOne(Long id) {
        LOG.debug("Request to get Attendant : {}", id);
        return attendantRepository.findById(id).map(attendantMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Attendant : {}", id);
        attendantRepository.deleteById(id);
    }

    @Override
    public SimpleAttendantResponseDTO createSimpleAttendant(SimpleAttendantRequestDTO requestDTO) {
        LOG.debug("Request to create simple Attendant : {}", requestDTO);

        // Create Staff first
        Staff staff = new Staff();
        staff.setName(requestDTO.getName());
        staff.setAge(requestDTO.getAge());
        staff.setGender(requestDTO.getGender());
        staff.setPhoneNumber(requestDTO.getPhoneNumber());
        staff.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : StaffStatus.ACTIVE);
        staff.setCreatedAt(Instant.now());
        staff.setIsDeleted(false);

        staff = staffRepository.save(staff);

        // Create Attendant
        Attendant attendant = new Attendant();
        attendant.setStaff(staff);
        attendant.setCreatedAt(Instant.now());
        attendant.setIsDeleted(false);

        attendant = attendantRepository.save(attendant);

        return mapToSimpleResponse(attendant);
    }

    @Override
    public SimpleAttendantResponseDTO updateSimpleAttendant(Long id, SimpleAttendantRequestDTO requestDTO) {
        LOG.debug("Request to update simple Attendant : {}, {}", id, requestDTO);

        Attendant existingAttendant = attendantRepository
                .findById(id)
                .orElseThrow(() -> new BadRequestAlertException("Attendant not found", "attendant", "idnotfound"));
        Staff existingStaff = existingAttendant.getStaff();

        // Update Staff
        existingStaff.setName(requestDTO.getName());
        existingStaff.setAge(requestDTO.getAge());
        existingStaff.setGender(requestDTO.getGender());
        existingStaff.setPhoneNumber(requestDTO.getPhoneNumber());
        if (requestDTO.getStatus() != null) {
            existingStaff.setStatus(requestDTO.getStatus());
        }
        existingStaff.setUpdatedAt(Instant.now());

        staffRepository.save(existingStaff);

        // Update Attendant
        existingAttendant.setUpdatedAt(Instant.now());

        existingAttendant = attendantRepository.save(existingAttendant);

        return mapToSimpleResponse(existingAttendant);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SimpleAttendantResponseDTO> findSimpleAttendantById(Long id) {
        LOG.debug("Request to get simple Attendant : {}", id);
        return attendantRepository.findById(id)
                .map(this::mapToSimpleResponse);
    }

    private SimpleAttendantResponseDTO mapToSimpleResponse(Attendant attendant) {
        SimpleAttendantResponseDTO response = new SimpleAttendantResponseDTO();
        response.setId(attendant.getId());
        response.setCreatedAt(attendant.getCreatedAt());
        response.setUpdatedAt(attendant.getUpdatedAt());

        if (attendant.getStaff() != null) {
            Staff staff = attendant.getStaff();
            response.setStaffId(staff.getId());
            response.setName(staff.getName());
            response.setAge(staff.getAge());
            response.setGender(staff.getGender());
            response.setPhoneNumber(staff.getPhoneNumber());
            response.setStatus(staff.getStatus());
        }

        return response;
    }
}
