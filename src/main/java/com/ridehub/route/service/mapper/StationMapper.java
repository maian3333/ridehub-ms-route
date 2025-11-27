package com.ridehub.route.service.mapper;

import com.ridehub.route.domain.Address;
import com.ridehub.route.domain.FileRoute;
import com.ridehub.route.domain.Station;
import com.ridehub.route.service.dto.AddressDTO;
import com.ridehub.route.service.dto.FileRouteDTO;
import com.ridehub.route.service.dto.StationDTO;
import com.ridehub.route.service.vm.StationWithRoutesVM;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Station} and its DTO {@link StationDTO}.
 */
@Mapper(componentModel = "spring")
public interface StationMapper extends EntityMapper<StationDTO, Station> {
    @Mapping(target = "address", source = "address", qualifiedByName = "addressId")
    @Mapping(target = "stationImg", source = "stationImg", qualifiedByName = "fileRouteId")
    StationDTO toDto(Station s);

    @Named("addressId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AddressDTO toDtoAddressId(Address address);

    @Named("fileRouteId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FileRouteDTO toDtoFileRouteId(FileRoute fileRoute);

    /**
     * Convert Station entity to StationWithRoutesVM
     * Note: routes will be populated separately in the service layer
     */
    @Mapping(target = "routes", ignore = true)
    @Mapping(target = "routresCount", ignore = true)
    StationWithRoutesVM toStationWithRoutesDto(Station station);
}
