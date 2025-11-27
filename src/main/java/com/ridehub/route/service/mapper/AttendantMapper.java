package com.ridehub.route.service.mapper;

import com.ridehub.route.domain.Attendant;
import com.ridehub.route.domain.Staff;
import com.ridehub.route.service.dto.AttendantDTO;
import com.ridehub.route.service.dto.StaffDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Attendant} and its DTO {@link AttendantDTO}.
 */
@Mapper(componentModel = "spring")
public interface AttendantMapper extends EntityMapper<AttendantDTO, Attendant> {
    @Mapping(target = "staff", source = "staff", qualifiedByName = "staffId")
    AttendantDTO toDto(Attendant s);

    @Named("staffId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StaffDTO toDtoStaffId(Staff staff);
}
