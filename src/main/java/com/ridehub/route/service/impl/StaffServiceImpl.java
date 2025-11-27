package com.ridehub.route.service.impl;

import com.ridehub.route.domain.Staff;
import com.ridehub.route.repository.StaffRepository;
import com.ridehub.route.service.StaffService;
import com.ridehub.route.service.dto.StaffDTO;
import com.ridehub.route.service.mapper.StaffMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ridehub.route.domain.Staff}.
 */
@Service
@Transactional
public class StaffServiceImpl implements StaffService {

    private static final Logger LOG = LoggerFactory.getLogger(StaffServiceImpl.class);

    private final StaffRepository staffRepository;

    private final StaffMapper staffMapper;

    public StaffServiceImpl(StaffRepository staffRepository, StaffMapper staffMapper) {
        this.staffRepository = staffRepository;
        this.staffMapper = staffMapper;
    }

    @Override
    public StaffDTO save(StaffDTO staffDTO) {
        LOG.debug("Request to save Staff : {}", staffDTO);
        Staff staff = staffMapper.toEntity(staffDTO);
        staff = staffRepository.save(staff);
        return staffMapper.toDto(staff);
    }

    @Override
    public StaffDTO update(StaffDTO staffDTO) {
        LOG.debug("Request to update Staff : {}", staffDTO);
        Staff staff = staffMapper.toEntity(staffDTO);
        staff = staffRepository.save(staff);
        return staffMapper.toDto(staff);
    }

    @Override
    public Optional<StaffDTO> partialUpdate(StaffDTO staffDTO) {
        LOG.debug("Request to partially update Staff : {}", staffDTO);

        return staffRepository
            .findById(staffDTO.getId())
            .map(existingStaff -> {
                staffMapper.partialUpdate(existingStaff, staffDTO);

                return existingStaff;
            })
            .map(staffRepository::save)
            .map(staffMapper::toDto);
    }

    /**
     *  Get all the staff where Driver is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<StaffDTO> findAllWhereDriverIsNull() {
        LOG.debug("Request to get all staff where Driver is null");
        return StreamSupport.stream(staffRepository.findAll().spliterator(), false)
            .filter(staff -> staff.getDriver() == null)
            .map(staffMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get all the staff where Attendant is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<StaffDTO> findAllWhereAttendantIsNull() {
        LOG.debug("Request to get all staff where Attendant is null");
        return StreamSupport.stream(staffRepository.findAll().spliterator(), false)
            .filter(staff -> staff.getAttendant() == null)
            .map(staffMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StaffDTO> findOne(Long id) {
        LOG.debug("Request to get Staff : {}", id);
        return staffRepository.findById(id).map(staffMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Staff : {}", id);
        staffRepository.deleteById(id);
    }
}
