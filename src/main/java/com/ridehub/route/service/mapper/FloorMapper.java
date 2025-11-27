package com.ridehub.route.service.mapper;

import com.ridehub.route.domain.Floor;
import com.ridehub.route.domain.SeatMap;
import com.ridehub.route.service.dto.FloorDTO;
import com.ridehub.route.service.dto.SeatMapDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Floor} and its DTO {@link FloorDTO}.
 */
@Mapper(componentModel = "spring")
public interface FloorMapper extends EntityMapper<FloorDTO, Floor> {
    @Mapping(target = "seatMap", source = "seatMap", qualifiedByName = "seatMapId")
    FloorDTO toDto(Floor s);

    @Named("seatMapId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SeatMapDTO toDtoSeatMapId(SeatMap seatMap);
}
