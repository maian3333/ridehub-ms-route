package com.ridehub.route.service.mapper;

import com.ridehub.route.domain.SeatLock;
import com.ridehub.route.domain.Trip;
import com.ridehub.route.service.dto.SeatLockDTO;
import com.ridehub.route.service.dto.TripDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SeatLock} and its DTO {@link SeatLockDTO}.
 */
@Mapper(componentModel = "spring")
public interface SeatLockMapper extends EntityMapper<SeatLockDTO, SeatLock> {
    @Mapping(target = "trip", source = "trip", qualifiedByName = "tripId")
    SeatLockDTO toDto(SeatLock s);

    @Named("tripId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TripDTO toDtoTripId(Trip trip);
}
