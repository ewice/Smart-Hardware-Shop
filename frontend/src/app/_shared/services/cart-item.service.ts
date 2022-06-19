import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { ICartItem } from '../types/cart-item.interface';
import { CartItemApi } from '../apis/cart-item.api';
import { IProduct } from '../types/product.interface';

@Injectable({
    providedIn: 'root'
})
export class CartItemService {
    private cartItems$ = new BehaviorSubject<ICartItem[]>([]);

    constructor(private cartItemApi: CartItemApi) {
        this.getCartItems();
    }

    // Observable
    getCartItems$(): Observable<ICartItem[]> {
        return this.cartItems$.asObservable();
    }

    addCartItem$(cardItem: ICartItem): void {
        const newCartItems = this.cartItems$.getValue();
        this.cartItems$.next([...newCartItems, cardItem]);
    }

    removeCartItem$(cartItemId: number): void {
        const newCartItems = this.cartItems$.getValue().filter(cartItem => cartItem.id !== cartItemId);
        this.cartItems$.next(newCartItems);
    }

    // API
    getCartItems(): void {
        this.cartItemApi.getAllCartItems().subscribe(cartItems => this.cartItems$.next(cartItems));
    }

    deleteCartItem(cartItemId: number): void {
        this.cartItemApi.deleteCartItem(cartItemId).subscribe(_ => this.removeCartItem$(cartItemId));
    }

    setCartItem(product: IProduct): void {
        this.cartItemApi.addCartItem(product).subscribe((cardItem: ICartItem) => this.addCartItem$(cardItem));
    }
}
