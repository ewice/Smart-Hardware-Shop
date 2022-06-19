package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.News;
import com.mycompany.myapp.service.dto.NewsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link News} and its DTO {@link NewsDTO}.
 */
@Mapper(componentModel = "spring")
public interface NewsMapper extends EntityMapper<NewsDTO, News> {}
