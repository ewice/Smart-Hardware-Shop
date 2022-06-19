import { Component, OnInit } from '@angular/core';
import { CartItemService } from '../_shared/services/cart-item.service';
import { ICartItem } from '../_shared/types/cart-item.interface';

@Component({
    selector: 'app-cart',
    templateUrl: './cart.component.html',
    styleUrls: ['./cart.component.scss']
})
export class CartComponent implements OnInit {
    totalAmount = 0;

    cartItems: ICartItem[] | undefined;

    constructor(private cartItemService: CartItemService) {}

    ngOnInit(): void {
        this.cartItemService.getCartItems$().subscribe(cartItems => {
            this.cartItems = cartItems;
            this.calculateTotalAmount(cartItems);
        });
    }

    private calculateTotalAmount(cartItems: ICartItem[]): void {
        this.totalAmount = 0;
        cartItems.forEach(cartItem => {
            this.totalAmount += cartItem.product.price;
        });
    }

    deleteCartItem(cartItemId: number): void {
        this.cartItemService.deleteCartItem(cartItemId);
    }
}
