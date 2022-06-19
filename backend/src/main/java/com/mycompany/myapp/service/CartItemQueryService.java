package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.CartItem;
import com.mycompany.myapp.repository.CartItemRepository;
import com.mycompany.myapp.service.criteria.CartItemCriteria;
import com.mycompany.myapp.service.dto.CartItemDTO;
import com.mycompany.myapp.service.mapper.CartItemMapper;

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
 * Service for executing complex queries for {@link CartItem} entities in the database.
 * The main input is a {@link CartItemCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CartItemDTO} or a {@link Page} of {@link CartItemDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CartItemQueryService extends QueryService<CartItem> {

    private final Logger log = LoggerFactory.getLogger(CartItemQueryService.class);

    private final CartItemRepository cartItemRepository;

    private final CartItemMapper cartItemMapper;

    public CartItemQueryService(CartItemRepository cartItemRepository, CartItemMapper cartItemMapper) {
        this.cartItemRepository = cartItemRepository;
        this.cartItemMapper = cartItemMapper;
    }

    /**
     * Return a {@link Page} of {@link CartItemDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CartItemDTO> findByCriteria(CartItemCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CartItem> specification = createSpecification(criteria);
        return cartItemRepository.findAll(specification, page).map(cartItemMapper::toDto);
    }

    /**
     * Function to convert {@link CartItemCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CartItem> createSpecification(CartItemCriteria criteria) {
        Specification<CartItem> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), CartItem_.id));
            }
            if (criteria.getProductId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getProductId(), root -> root.join(CartItem_.product, JoinType.LEFT).get(Product_.id))
                    );
            }
        }
        return specification;
    }
}
