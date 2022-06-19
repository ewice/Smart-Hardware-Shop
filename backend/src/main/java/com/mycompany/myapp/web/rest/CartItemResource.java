package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.CartItemRepository;
import com.mycompany.myapp.service.CartItemQueryService;
import com.mycompany.myapp.service.CartItemService;
import com.mycompany.myapp.service.criteria.CartItemCriteria;
import com.mycompany.myapp.service.dto.CartItemDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.CartItem}.
 */
@RestController
@RequestMapping("/api")
public class CartItemResource {

    private final Logger log = LoggerFactory.getLogger(CartItemResource.class);

    private static final String ENTITY_NAME = "backendCartItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CartItemService cartItemService;

    private final CartItemRepository cartItemRepository;

    private final CartItemQueryService cartItemQueryService;

    public CartItemResource(
        CartItemService cartItemService,
        CartItemRepository cartItemRepository,
        CartItemQueryService cartItemQueryService
    ) {
        this.cartItemService = cartItemService;
        this.cartItemRepository = cartItemRepository;
        this.cartItemQueryService = cartItemQueryService;
    }

    /**
     * {@code POST  /cart-items} : Create a new cartItem.
     *
     * @param cartItemDTO the cartItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cartItemDTO, or with status {@code 400 (Bad Request)} if the cartItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/cart-items")
    public ResponseEntity<CartItemDTO> createCartItem(@Valid @RequestBody CartItemDTO cartItemDTO) throws URISyntaxException {
        log.debug("REST request to save CartItem : {}", cartItemDTO);
        if (cartItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new cartItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CartItemDTO result = cartItemService.save(cartItemDTO);
        return ResponseEntity
            .created(new URI("/api/cart-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /cart-items/:id} : Updates an existing cartItem.
     *
     * @param id          the id of the cartItemDTO to save.
     * @param cartItemDTO the cartItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cartItemDTO,
     * or with status {@code 400 (Bad Request)} if the cartItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cartItemDTO couldn't be updated.
     */
    @PutMapping("/cart-items/{id}")
    public ResponseEntity<CartItemDTO> updateCartItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CartItemDTO cartItemDTO
    ) {
        log.debug("REST request to update CartItem : {}, {}", id, cartItemDTO);
        if (cartItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cartItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cartItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CartItemDTO result = cartItemService.update(cartItemDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cartItemDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /cart-items} : get all the cartItems.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cartItems in body.
     */
    @GetMapping("/cart-items")
    public ResponseEntity<List<CartItemDTO>> getAllCartItems(
        CartItemCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get CartItems by criteria: {}", criteria);
        Page<CartItemDTO> page = cartItemQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cart-items/:id} : get the "id" cartItem.
     *
     * @param id the id of the cartItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cartItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/cart-items/{id}")
    public ResponseEntity<CartItemDTO> getCartItem(@PathVariable Long id) {
        log.debug("REST request to get CartItem : {}", id);
        Optional<CartItemDTO> cartItemDTO = cartItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cartItemDTO);
    }

    /**
     * {@code DELETE  /cart-items/:id} : delete the "id" cartItem.
     *
     * @param id the id of the cartItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/cart-items/{id}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long id) {
        log.debug("REST request to delete CartItem : {}", id);
        cartItemService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
