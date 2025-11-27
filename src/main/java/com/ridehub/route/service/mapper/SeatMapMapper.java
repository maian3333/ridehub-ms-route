package com.ridehub.route.service.mapper;

import com.ridehub.route.domain.FileRoute;
import com.ridehub.route.domain.SeatMap;
import com.ridehub.route.service.dto.FileRouteDTO;
import com.ridehub.route.service.dto.SeatMapDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SeatMap} and its DTO {@link SeatMapDTO}.
 */
@Mapper(componentModel = "spring")
public interface SeatMapMapper extends EntityMapper<SeatMapDTO, SeatMap> {
    @Mapping(target = "seatMapImg", source = "seatMapImg", qualifiedByName = "fileRouteId")
    SeatMapDTO toDto(SeatMap s);

    @Named("fileRouteId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FileRouteDTO toDtoFileRouteId(FileRoute fileRoute);
}
