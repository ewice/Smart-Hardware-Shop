package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.News;
import com.mycompany.myapp.repository.NewsRepository;
import com.mycompany.myapp.service.criteria.NewsCriteria;
import com.mycompany.myapp.service.dto.NewsDTO;
import com.mycompany.myapp.service.mapper.NewsMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link NewsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NewsResourceIT {

    private static final String DEFAULT_IMAGE = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/news";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNewsMockMvc;

    private News news;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static News createEntity(EntityManager em) {
        return new News().image(DEFAULT_IMAGE);
    }

    @BeforeEach
    public void initTest() {
        news = createEntity(em);
    }

    @Test
    @Transactional
    void createNews() throws Exception {
        int databaseSizeBeforeCreate = newsRepository.findAll().size();

        NewsDTO newsDTO = newsMapper.toDto(news);
        restNewsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(newsDTO)))
            .andExpect(status().isCreated());

        List<News> newsList = newsRepository.findAll();
        assertThat(newsList).hasSize(databaseSizeBeforeCreate + 1);
        News testNews = newsList.get(newsList.size() - 1);
        assertThat(testNews.getImage()).isEqualTo(DEFAULT_IMAGE);
    }

    @Test
    @Transactional
    void createNewsWithExistingId() throws Exception {
        news.setId(1L);
        NewsDTO newsDTO = newsMapper.toDto(news);

        int databaseSizeBeforeCreate = newsRepository.findAll().size();

        restNewsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(newsDTO)))
            .andExpect(status().isBadRequest());

        List<News> newsList = newsRepository.findAll();
        assertThat(newsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkImageIsRequired() throws Exception {
        int databaseSizeBeforeTest = newsRepository.findAll().size();
        news.setImage(null);

        NewsDTO newsDTO = newsMapper.toDto(news);

        restNewsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(newsDTO)))
            .andExpect(status().isBadRequest());

        List<News> newsList = newsRepository.findAll();
        assertThat(newsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllNews() throws Exception {
        newsRepository.saveAndFlush(news);

        restNewsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(news.getId().intValue())))
            .andExpect(jsonPath("$.[*].image").value(hasItem(DEFAULT_IMAGE)));
    }

    @Test
    @Transactional
    void getNews() throws Exception {
        newsRepository.saveAndFlush(news);

        restNewsMockMvc
            .perform(get(ENTITY_API_URL_ID, news.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(news.getId().intValue()))
            .andExpect(jsonPath("$.image").value(DEFAULT_IMAGE));
    }

    @Test
    @Transactional
    void getNewsByIdFiltering() throws Exception {
        newsRepository.saveAndFlush(news);

        Long id = news.getId();

        defaultNewsShouldBeFound("id.equals=" + id);
        defaultNewsShouldNotBeFound("id.notEquals=" + id);

        defaultNewsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultNewsShouldNotBeFound("id.greaterThan=" + id);

        defaultNewsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultNewsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllNewsByImageIsEqualToSomething() throws Exception {
        newsRepository.saveAndFlush(news);

        defaultNewsShouldBeFound("image.equals=" + DEFAULT_IMAGE);
        defaultNewsShouldNotBeFound("image.equals=" + UPDATED_IMAGE);
    }

    @Test
    @Transactional
    void getAllNewsByImageIsNotEqualToSomething() throws Exception {
        newsRepository.saveAndFlush(news);

        defaultNewsShouldNotBeFound("image.notEquals=" + DEFAULT_IMAGE);
        defaultNewsShouldBeFound("image.notEquals=" + UPDATED_IMAGE);
    }

    @Test
    @Transactional
    void getAllNewsByImageIsInShouldWork() throws Exception {
        newsRepository.saveAndFlush(news);

        defaultNewsShouldBeFound("image.in=" + DEFAULT_IMAGE + "," + UPDATED_IMAGE);
        defaultNewsShouldNotBeFound("image.in=" + UPDATED_IMAGE);
    }

    @Test
    @Transactional
    void getAllNewsByImageIsNullOrNotNull() throws Exception {
        newsRepository.saveAndFlush(news);

        defaultNewsShouldBeFound("image.specified=true");
        defaultNewsShouldNotBeFound("image.specified=false");
    }

    @Test
    @Transactional
    void getAllNewsByImageContainsSomething() throws Exception {
        newsRepository.saveAndFlush(news);

        defaultNewsShouldBeFound("image.contains=" + DEFAULT_IMAGE);
        defaultNewsShouldNotBeFound("image.contains=" + UPDATED_IMAGE);
    }

    @Test
    @Transactional
    void getAllNewsByImageNotContainsSomething() throws Exception {
        newsRepository.saveAndFlush(news);

        defaultNewsShouldNotBeFound("image.doesNotContain=" + DEFAULT_IMAGE);
        defaultNewsShouldBeFound("image.doesNotContain=" + UPDATED_IMAGE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultNewsShouldBeFound(String filter) throws Exception {
        restNewsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(news.getId().intValue())))
            .andExpect(jsonPath("$.[*].image").value(hasItem(DEFAULT_IMAGE)));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultNewsShouldNotBeFound(String filter) throws Exception {
        restNewsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    void getNonExistingNews() throws Exception {
        restNewsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewNews() throws Exception {
        newsRepository.saveAndFlush(news);

        int databaseSizeBeforeUpdate = newsRepository.findAll().size();

        News updatedNews = newsRepository.findById(news.getId()).get();
        em.detach(updatedNews);
        updatedNews.image(UPDATED_IMAGE);
        NewsDTO newsDTO = newsMapper.toDto(updatedNews);

        restNewsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, newsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(newsDTO))
            )
            .andExpect(status().isOk());

        List<News> newsList = newsRepository.findAll();
        assertThat(newsList).hasSize(databaseSizeBeforeUpdate);
        News testNews = newsList.get(newsList.size() - 1);
        assertThat(testNews.getImage()).isEqualTo(UPDATED_IMAGE);
    }

    @Test
    @Transactional
    void putNonExistingNews() throws Exception {
        int databaseSizeBeforeUpdate = newsRepository.findAll().size();
        news.setId(count.incrementAndGet());

        NewsDTO newsDTO = newsMapper.toDto(news);

        restNewsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, newsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(newsDTO))
            )
            .andExpect(status().isBadRequest());

        List<News> newsList = newsRepository.findAll();
        assertThat(newsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNews() throws Exception {
        int databaseSizeBeforeUpdate = newsRepository.findAll().size();
        news.setId(count.incrementAndGet());

        NewsDTO newsDTO = newsMapper.toDto(news);

        restNewsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(newsDTO))
            )
            .andExpect(status().isBadRequest());

        List<News> newsList = newsRepository.findAll();
        assertThat(newsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNews() throws Exception {
        int databaseSizeBeforeUpdate = newsRepository.findAll().size();
        news.setId(count.incrementAndGet());

        NewsDTO newsDTO = newsMapper.toDto(news);

        restNewsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(newsDTO)))
            .andExpect(status().isMethodNotAllowed());

        List<News> newsList = newsRepository.findAll();
        assertThat(newsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNews() throws Exception {
        newsRepository.saveAndFlush(news);

        int databaseSizeBeforeDelete = newsRepository.findAll().size();

        restNewsMockMvc
            .perform(delete(ENTITY_API_URL_ID, news.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        List<News> newsList = newsRepository.findAll();
        assertThat(newsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
