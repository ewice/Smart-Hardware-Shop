package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.CartItem;
import com.mycompany.myapp.domain.Product;
import com.mycompany.myapp.repository.CartItemRepository;
import com.mycompany.myapp.service.criteria.CartItemCriteria;
import com.mycompany.myapp.service.dto.CartItemDTO;
import com.mycompany.myapp.service.mapper.CartItemMapper;
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
 * Integration tests for the {@link CartItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CartItemResourceIT {

    private static final String ENTITY_API_URL = "/api/cart-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCartItemMockMvc;

    private CartItem cartItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CartItem createEntity(EntityManager em) {
        CartItem cartItem = new CartItem();

        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createEntity(em);
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        cartItem.setProduct(product);
        return cartItem;
    }

    @BeforeEach
    public void initTest() {
        cartItem = createEntity(em);
    }

    @Test
    @Transactional
    void createCartItem() throws Exception {
        int databaseSizeBeforeCreate = cartItemRepository.findAll().size();

        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);
        restCartItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cartItemDTO)))
            .andExpect(status().isCreated());

        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeCreate + 1);
        CartItem testCartItem = cartItemList.get(cartItemList.size() - 1);
    }

    @Test
    @Transactional
    void createCartItemWithExistingId() throws Exception {
        cartItem.setId(1L);
        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);

        int databaseSizeBeforeCreate = cartItemRepository.findAll().size();

        restCartItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cartItemDTO)))
            .andExpect(status().isBadRequest());

        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCartItems() throws Exception {
        cartItemRepository.saveAndFlush(cartItem);

        restCartItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cartItem.getId().intValue())));
    }

    @Test
    @Transactional
    void getCartItem() throws Exception {
        cartItemRepository.saveAndFlush(cartItem);

        restCartItemMockMvc
            .perform(get(ENTITY_API_URL_ID, cartItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cartItem.getId().intValue()));
    }

    @Test
    @Transactional
    void getCartItemsByIdFiltering() throws Exception {
        cartItemRepository.saveAndFlush(cartItem);

        Long id = cartItem.getId();

        defaultCartItemShouldBeFound("id.equals=" + id);
        defaultCartItemShouldNotBeFound("id.notEquals=" + id);

        defaultCartItemShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCartItemShouldNotBeFound("id.greaterThan=" + id);

        defaultCartItemShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCartItemShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCartItemsByProductIsEqualToSomething() throws Exception {
        cartItemRepository.saveAndFlush(cartItem);
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createEntity(em);
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        em.persist(product);
        em.flush();
        cartItem.setProduct(product);
        cartItemRepository.saveAndFlush(cartItem);
        Long productId = product.getId();

        defaultCartItemShouldBeFound("productId.equals=" + productId);
        defaultCartItemShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCartItemShouldBeFound(String filter) throws Exception {
        restCartItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cartItem.getId().intValue())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCartItemShouldNotBeFound(String filter) throws Exception {
        restCartItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    void getNonExistingCartItem() throws Exception {
        restCartItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCartItem() throws Exception {
        cartItemRepository.saveAndFlush(cartItem);

        int databaseSizeBeforeUpdate = cartItemRepository.findAll().size();

        CartItem updatedCartItem = cartItemRepository.findById(cartItem.getId()).get();
        em.detach(updatedCartItem);
        CartItemDTO cartItemDTO = cartItemMapper.toDto(updatedCartItem);

        restCartItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cartItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cartItemDTO))
            )
            .andExpect(status().isOk());

        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
        CartItem testCartItem = cartItemList.get(cartItemList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingCartItem() throws Exception {
        int databaseSizeBeforeUpdate = cartItemRepository.findAll().size();
        cartItem.setId(count.incrementAndGet());

        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);

        restCartItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cartItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cartItemDTO))
            )
            .andExpect(status().isBadRequest());

        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCartItem() throws Exception {
        int databaseSizeBeforeUpdate = cartItemRepository.findAll().size();
        cartItem.setId(count.incrementAndGet());

        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);

        restCartItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cartItemDTO))
            )
            .andExpect(status().isBadRequest());

        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCartItem() throws Exception {
        int databaseSizeBeforeUpdate = cartItemRepository.findAll().size();
        cartItem.setId(count.incrementAndGet());

        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);

        restCartItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cartItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCartItem() throws Exception {
        cartItemRepository.saveAndFlush(cartItem);

        int databaseSizeBeforeDelete = cartItemRepository.findAll().size();

        restCartItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, cartItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
