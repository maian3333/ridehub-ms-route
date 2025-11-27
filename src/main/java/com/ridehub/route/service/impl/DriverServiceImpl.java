package com.ridehub.route.service.impl;

import com.ridehub.route.domain.Driver;
import com.ridehub.route.domain.Staff;
import com.ridehub.route.domain.enumeration.StaffStatus;
import com.ridehub.route.repository.DriverRepository;
import com.ridehub.route.repository.StaffRepository;
import com.ridehub.route.service.DriverService;
import com.ridehub.route.service.dto.DriverDTO;
import com.ridehub.route.service.dto.request.SimpleDriverRequestDTO;
import com.ridehub.route.service.dto.response.SimpleDriverResponseDTO;
import com.ridehub.route.service.mapper.DriverMapper;
import com.ridehub.route.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ridehub.route.domain.Driver}.
 */
@Service
@Transactional
public class DriverServiceImpl implements DriverService {

    private static final Logger LOG = LoggerFactory.getLogger(DriverServiceImpl.class);

    private final DriverRepository driverRepository;

    private final StaffRepository staffRepository;

    private final DriverMapper driverMapper;

    public DriverServiceImpl(DriverRepository driverRepository, StaffRepository staffRepository,
            DriverMapper driverMapper) {
        this.driverRepository = driverRepository;
        this.staffRepository = staffRepository;
        this.driverMapper = driverMapper;
    }

    @Override
    public DriverDTO save(DriverDTO driverDTO) {
        LOG.debug("Request to save Driver : {}", driverDTO);
        Driver driver = driverMapper.toEntity(driverDTO);
        driver = driverRepository.save(driver);
        return driverMapper.toDto(driver);
    }

    @Override
    public DriverDTO update(DriverDTO driverDTO) {
        LOG.debug("Request to update Driver : {}", driverDTO);
        Driver driver = driverMapper.toEntity(driverDTO);
        driver = driverRepository.save(driver);
        return driverMapper.toDto(driver);
    }

    @Override
    public Optional<DriverDTO> partialUpdate(DriverDTO driverDTO) {
        LOG.debug("Request to partially update Driver : {}", driverDTO);

        return driverRepository
                .findById(driverDTO.getId())
                .map(existingDriver -> {
                    driverMapper.partialUpdate(existingDriver, driverDTO);

                    return existingDriver;
                })
                .map(driverRepository::save)
                .map(driverMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DriverDTO> findOne(Long id) {
        LOG.debug("Request to get Driver : {}", id);
        return driverRepository.findById(id).map(driverMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Driver : {}", id);
        driverRepository.deleteById(id);
    }

    @Override
    public SimpleDriverResponseDTO createSimpleDriver(SimpleDriverRequestDTO requestDTO) {
        LOG.debug("Request to create simple Driver : {}", requestDTO);

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

        // Create Driver
        Driver driver = new Driver();
        driver.setLicenseClass(requestDTO.getLicenseClass());
        driver.setYearsExperience(requestDTO.getYearsExperience());
        driver.setStaff(staff);
        driver.setCreatedAt(Instant.now());
        driver.setIsDeleted(false);

        driver = driverRepository.save(driver);

        return mapToSimpleResponse(driver);
    }

    @Override
    public SimpleDriverResponseDTO updateSimpleDriver(Long id, SimpleDriverRequestDTO requestDTO) {
        LOG.debug("Request to update simple Driver : {}, {}", id, requestDTO);

        Driver existingDriver = driverRepository
                .findById(id)
                .orElseThrow(() -> new BadRequestAlertException("Driver not found", "driver", "idnotfound"));
        Staff existingStaff = existingDriver.getStaff();

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

        // Update Driver
        existingDriver.setLicenseClass(requestDTO.getLicenseClass());
        existingDriver.setYearsExperience(requestDTO.getYearsExperience());
        existingDriver.setUpdatedAt(Instant.now());

        existingDriver = driverRepository.save(existingDriver);

        return mapToSimpleResponse(existingDriver);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SimpleDriverResponseDTO> findSimpleDriverById(Long id) {
        LOG.debug("Request to get simple Driver : {}", id);
        return driverRepository.findById(id)
                .map(this::mapToSimpleResponse);
    }

    private SimpleDriverResponseDTO mapToSimpleResponse(Driver driver) {
        SimpleDriverResponseDTO response = new SimpleDriverResponseDTO();
        response.setId(driver.getId());
        response.setLicenseClass(driver.getLicenseClass());
        response.setYearsExperience(driver.getYearsExperience());
        response.setCreatedAt(driver.getCreatedAt());
        response.setUpdatedAt(driver.getUpdatedAt());

        if (driver.getStaff() != null) {
            Staff staff = driver.getStaff();
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
