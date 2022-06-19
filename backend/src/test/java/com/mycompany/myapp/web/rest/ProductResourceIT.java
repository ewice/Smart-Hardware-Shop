package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Product;
import com.mycompany.myapp.repository.ProductRepository;
import com.mycompany.myapp.service.criteria.ProductCriteria;
import com.mycompany.myapp.service.dto.ProductDTO;
import com.mycompany.myapp.service.mapper.ProductMapper;
import java.math.BigDecimal;
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
 * Integration tests for the {@link ProductResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(100);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(98);
    private static final BigDecimal SMALLER_PRICE = new BigDecimal(99);

    private static final String DEFAULT_IMAGE = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductMockMvc;

    private Product product;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createEntity(EntityManager em) {
        return new Product().title(DEFAULT_TITLE).price(DEFAULT_PRICE).image(DEFAULT_IMAGE);
    }

    @BeforeEach
    public void initTest() {
        product = createEntity(em);
    }

    @Test
    @Transactional
    void createProduct() throws Exception {
        int databaseSizeBeforeCreate = productRepository.findAll().size();

        ProductDTO productDTO = productMapper.toDto(product);
        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isCreated());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate + 1);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testProduct.getPrice()).isEqualByComparingTo(DEFAULT_PRICE);
        assertThat(testProduct.getImage()).isEqualTo(DEFAULT_IMAGE);
    }

    @Test
    @Transactional
    void createProductWithExistingId() throws Exception {
        product.setId(1L);
        ProductDTO productDTO = productMapper.toDto(product);

        int databaseSizeBeforeCreate = productRepository.findAll().size();

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        product.setTitle(null);

        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        product.setPrice(null);

        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkImageIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        product.setImage(null);

        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProducts() throws Exception {
        productRepository.saveAndFlush(product);

        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].image").value(hasItem(DEFAULT_IMAGE)));
    }

    @Test
    @Transactional
    void getProduct() throws Exception {
        productRepository.saveAndFlush(product);

        restProductMockMvc
            .perform(get(ENTITY_API_URL_ID, product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(product.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.price").value(sameNumber(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.image").value(DEFAULT_IMAGE));
    }

    @Test
    @Transactional
    void getProductsByIdFiltering() throws Exception {
        productRepository.saveAndFlush(product);

        Long id = product.getId();

        defaultProductShouldBeFound("id.equals=" + id);
        defaultProductShouldNotBeFound("id.notEquals=" + id);

        defaultProductShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProductShouldNotBeFound("id.greaterThan=" + id);

        defaultProductShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProductShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProductsByTitleIsEqualToSomething() throws Exception {
        productRepository.saveAndFlush(product);

        defaultProductShouldBeFound("title.equals=" + DEFAULT_TITLE);
        defaultProductShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllProductsByTitleIsNotEqualToSomething() throws Exception {
        productRepository.saveAndFlush(product);

        defaultProductShouldNotBeFound("title.notEquals=" + DEFAULT_TITLE);
        defaultProductShouldBeFound("title.notEquals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllProductsByTitleIsInShouldWork() throws Exception {
        productRepository.saveAndFlush(product);

        defaultProductShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);
        defaultProductShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllProductsByTitleIsNullOrNotNull() throws Exception {
        productRepository.saveAndFlush(product);

        defaultProductShouldBeFound("title.specified=true");
        defaultProductShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByTitleContainsSomething() throws Exception {
        productRepository.saveAndFlush(product);

        defaultProductShouldBeFound("title.contains=" + DEFAULT_TITLE);
        defaultProductShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllProductsByTitleNotContainsSomething() throws Exception {
        productRepository.saveAndFlush(product);

        defaultProductShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);
        defaultProductShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsEqualToSomething() throws Exception {
        productRepository.saveAndFlush(product);

        defaultProductShouldBeFound("price.equals=" + DEFAULT_PRICE);
        defaultProductShouldNotBeFound("price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsNotEqualToSomething() throws Exception {
        productRepository.saveAndFlush(product);

        defaultProductShouldNotBeFound("price.notEquals=" + DEFAULT_PRICE);
        defaultProductShouldBeFound("price.notEquals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsInShouldWork() throws Exception {
        productRepository.saveAndFlush(product);

        defaultProductShouldBeFound("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE);
        defaultProductShouldNotBeFound("price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsNullOrNotNull() throws Exception {
        productRepository.saveAndFlush(product);

        defaultProductShouldBeFound("price.specified=true");
        defaultProductShouldNotBeFound("price.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsGreaterThanOrEqualToSomething() throws Exception {
        productRepository.saveAndFlush(product);

        defaultProductShouldBeFound("price.greaterThanOrEqual=" + DEFAULT_PRICE);
        defaultProductShouldNotBeFound("price.greaterThanOrEqual=" + (DEFAULT_PRICE.add(BigDecimal.ONE)));
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsLessThanOrEqualToSomething() throws Exception {
        productRepository.saveAndFlush(product);

        defaultProductShouldBeFound("price.lessThanOrEqual=" + DEFAULT_PRICE);
        defaultProductShouldNotBeFound("price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsLessThanSomething() throws Exception {
        productRepository.saveAndFlush(product);

        defaultProductShouldNotBeFound("price.lessThan=" + DEFAULT_PRICE);
        defaultProductShouldBeFound("price.lessThan=" + (DEFAULT_PRICE.add(BigDecimal.ONE)));
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsGreaterThanSomething() throws Exception {
        productRepository.saveAndFlush(product);

        defaultProductShouldNotBeFound("price.greaterThan=" + DEFAULT_PRICE);
        defaultProductShouldBeFound("price.greaterThan=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByImageIsEqualToSomething() throws Exception {
        productRepository.saveAndFlush(product);

        defaultProductShouldBeFound("image.equals=" + DEFAULT_IMAGE);
        defaultProductShouldNotBeFound("image.equals=" + UPDATED_IMAGE);
    }

    @Test
    @Transactional
    void getAllProductsByImageIsNotEqualToSomething() throws Exception {
        productRepository.saveAndFlush(product);

        defaultProductShouldNotBeFound("image.notEquals=" + DEFAULT_IMAGE);
        defaultProductShouldBeFound("image.notEquals=" + UPDATED_IMAGE);
    }

    @Test
    @Transactional
    void getAllProductsByImageIsInShouldWork() throws Exception {
        productRepository.saveAndFlush(product);

        defaultProductShouldBeFound("image.in=" + DEFAULT_IMAGE + "," + UPDATED_IMAGE);
        defaultProductShouldNotBeFound("image.in=" + UPDATED_IMAGE);
    }

    @Test
    @Transactional
    void getAllProductsByImageIsNullOrNotNull() throws Exception {
        productRepository.saveAndFlush(product);

        defaultProductShouldBeFound("image.specified=true");
        defaultProductShouldNotBeFound("image.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByImageContainsSomething() throws Exception {
        productRepository.saveAndFlush(product);

        defaultProductShouldBeFound("image.contains=" + DEFAULT_IMAGE);
        defaultProductShouldNotBeFound("image.contains=" + UPDATED_IMAGE);
    }

    @Test
    @Transactional
    void getAllProductsByImageNotContainsSomething() throws Exception {
        productRepository.saveAndFlush(product);

        defaultProductShouldNotBeFound("image.doesNotContain=" + DEFAULT_IMAGE);
        defaultProductShouldBeFound("image.doesNotContain=" + UPDATED_IMAGE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductShouldBeFound(String filter) throws Exception {
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].image").value(hasItem(DEFAULT_IMAGE)));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductShouldNotBeFound(String filter) throws Exception {
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    void getNonExistingProduct() throws Exception {
        restProductMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProduct() throws Exception {
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        Product updatedProduct = productRepository.findById(product.getId()).get();
        em.detach(updatedProduct);
        updatedProduct.title(UPDATED_TITLE).price(UPDATED_PRICE).image(UPDATED_IMAGE);
        ProductDTO productDTO = productMapper.toDto(updatedProduct);

        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isOk());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testProduct.getPrice()).isEqualByComparingTo(UPDATED_PRICE);
        assertThat(testProduct.getImage()).isEqualTo(UPDATED_IMAGE);
    }

    @Test
    @Transactional
    void putNonExistingProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isMethodNotAllowed());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProduct() throws Exception {
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeDelete = productRepository.findAll().size();

        restProductMockMvc
            .perform(delete(ENTITY_API_URL_ID, product.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
