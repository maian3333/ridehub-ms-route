package com.ridehub.route.service.mapper;

import com.ridehub.route.domain.Driver;
import com.ridehub.route.domain.Staff;
import com.ridehub.route.service.dto.DriverDTO;
import com.ridehub.route.service.dto.StaffDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Driver} and its DTO {@link DriverDTO}.
 */
@Mapper(componentModel = "spring")
public interface DriverMapper extends EntityMapper<DriverDTO, Driver> {
    @Mapping(target = "staff", source = "staff", qualifiedByName = "staffId")
    DriverDTO toDto(Driver s);

    @Named("staffId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StaffDTO toDtoStaffId(Staff staff);
}
