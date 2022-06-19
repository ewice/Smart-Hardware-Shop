package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.News;
import com.mycompany.myapp.repository.NewsRepository;
import com.mycompany.myapp.service.dto.NewsDTO;
import com.mycompany.myapp.service.mapper.NewsMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link News}.
 */
@Service
@Transactional
public class NewsService {

    private final Logger log = LoggerFactory.getLogger(NewsService.class);

    private final NewsRepository newsRepository;

    private final NewsMapper newsMapper;

    public NewsService(NewsRepository newsRepository, NewsMapper newsMapper) {
        this.newsRepository = newsRepository;
        this.newsMapper = newsMapper;
    }

    /**
     * Save a news.
     *
     * @param newsDTO the entity to save.
     * @return the persisted entity.
     */
    public NewsDTO save(NewsDTO newsDTO) {
        log.debug("Request to save News : {}", newsDTO);
        News news = newsMapper.toEntity(newsDTO);
        news = newsRepository.save(news);
        return newsMapper.toDto(news);
    }

    /**
     * Update a news.
     *
     * @param newsDTO the entity to save.
     * @return the persisted entity.
     */
    public NewsDTO update(NewsDTO newsDTO) {
        log.debug("Request to save News : {}", newsDTO);
        News news = newsMapper.toEntity(newsDTO);
        news = newsRepository.save(news);
        return newsMapper.toDto(news);
    }

    /**
     * Get all the news.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<NewsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all News");
        return newsRepository.findAll(pageable).map(newsMapper::toDto);
    }

    /**
     * Get one news by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<NewsDTO> findOne(Long id) {
        log.debug("Request to get News : {}", id);
        return newsRepository.findById(id).map(newsMapper::toDto);
    }

    /**
     * Delete the news by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete News : {}", id);
        newsRepository.deleteById(id);
    }
}
