package com.ridehub.route.service.mapper;

import com.ridehub.route.domain.FileRoute;
import com.ridehub.route.domain.SeatMap;
import com.ridehub.route.domain.Vehicle;
import com.ridehub.route.service.dto.FileRouteDTO;
import com.ridehub.route.service.dto.SeatMapDTO;
import com.ridehub.route.service.dto.VehicleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Vehicle} and its DTO {@link VehicleDTO}.
 */
@Mapper(componentModel = "spring")
public interface VehicleMapper extends EntityMapper<VehicleDTO, Vehicle> {
    @Mapping(target = "seatMap", source = "seatMap", qualifiedByName = "seatMapId")
    @Mapping(target = "vehicleImg", source = "vehicleImg", qualifiedByName = "fileRouteId")
    VehicleDTO toDto(Vehicle s);

    @Named("seatMapId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SeatMapDTO toDtoSeatMapId(SeatMap seatMap);

    @Named("fileRouteId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FileRouteDTO toDtoFileRouteId(FileRoute fileRoute);
}
