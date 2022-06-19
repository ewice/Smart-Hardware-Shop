package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.CartItem;
import com.mycompany.myapp.domain.Product;
import com.mycompany.myapp.service.dto.CartItemDTO;
import com.mycompany.myapp.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CartItem} and its DTO {@link CartItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface CartItemMapper extends EntityMapper<CartItemDTO, CartItem> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productId")
    CartItemDTO toDto(CartItem s);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "price", source = "price")
    ProductDTO toDtoProductId(Product product);
}
