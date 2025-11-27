package com.ridehub.route.service.mapper;

import com.ridehub.route.domain.Staff;
import com.ridehub.route.service.dto.StaffDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Staff} and its DTO {@link StaffDTO}.
 */
@Mapper(componentModel = "spring")
public interface StaffMapper extends EntityMapper<StaffDTO, Staff> {}
