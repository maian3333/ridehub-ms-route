package com.ridehub.route.service.mapper;

import com.ridehub.route.domain.Route;
import com.ridehub.route.domain.Station;
import com.ridehub.route.service.dto.RouteDTO;
import com.ridehub.route.service.dto.StationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Route} and its DTO {@link RouteDTO}.
 */
@Mapper(componentModel = "spring", uses = {StationMapper.class})
public interface RouteMapper extends EntityMapper<RouteDTO, Route> {
    @Mapping(target = "origin", source = "origin", qualifiedByName = "stationFull")
    @Mapping(target = "destination", source = "destination", qualifiedByName = "stationFull")
    RouteDTO toDto(Route s);

    @Named("stationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StationDTO toDtoStationId(Station station);

    @Named("stationFull")
    @BeanMapping(ignoreByDefault = false)
    StationDTO toDtoStationFull(Station station);
}
