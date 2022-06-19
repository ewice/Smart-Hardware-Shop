package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.News;
import com.mycompany.myapp.repository.NewsRepository;
import com.mycompany.myapp.service.criteria.NewsCriteria;
import com.mycompany.myapp.service.dto.NewsDTO;
import com.mycompany.myapp.service.mapper.NewsMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link News} entities in the database.
 * The main input is a {@link NewsCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link NewsDTO} or a {@link Page} of {@link NewsDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class NewsQueryService extends QueryService<News> {

    private final Logger log = LoggerFactory.getLogger(NewsQueryService.class);

    private final NewsRepository newsRepository;

    private final NewsMapper newsMapper;

    public NewsQueryService(NewsRepository newsRepository, NewsMapper newsMapper) {
        this.newsRepository = newsRepository;
        this.newsMapper = newsMapper;
    }

    /**
     * Return a {@link Page} of {@link NewsDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<NewsDTO> findByCriteria(NewsCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<News> specification = createSpecification(criteria);
        return newsRepository.findAll(specification, page).map(newsMapper::toDto);
    }

    /**
     * Function to convert {@link NewsCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<News> createSpecification(NewsCriteria criteria) {
        Specification<News> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), News_.id));
            }
            if (criteria.getImage() != null) {
                specification = specification.and(buildStringSpecification(criteria.getImage(), News_.image));
            }
        }
        return specification;
    }
}
