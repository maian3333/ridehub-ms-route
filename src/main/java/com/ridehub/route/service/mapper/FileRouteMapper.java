package com.ridehub.route.service.mapper;

import com.ridehub.route.domain.FileRoute;
import com.ridehub.route.service.dto.FileRouteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FileRoute} and its DTO {@link FileRouteDTO}.
 */
@Mapper(componentModel = "spring")
public interface FileRouteMapper extends EntityMapper<FileRouteDTO, FileRoute> {}
