import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ICartItem } from '../types/cart-item.interface';
import { IProduct } from '../types/product.interface';

@Injectable({
    providedIn: 'root'
})
export class CartItemApi {
    constructor(private http: HttpClient) {}

    getAllCartItems(): Observable<ICartItem[]> {
        return this.http.get<ICartItem[]>('/api/cart-items');
    }

    deleteCartItem(cartItemId: number): Observable<ICartItem> {
        return this.http.delete<ICartItem>('/api/cart-items/' + cartItemId);
    }

    addCartItem(product: IProduct): Observable<ICartItem> {
        return this.http.post<ICartItem>('/api/cart-items', { product });
    }
}
