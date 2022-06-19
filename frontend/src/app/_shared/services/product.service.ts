import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { IProduct } from '../types/product.interface';
import { ProductApi } from '../apis/product.api';

@Injectable({
    providedIn: 'root'
})
export class ProductService {
    private products$ = new BehaviorSubject<IProduct[]>([]);

    constructor(private productApi: ProductApi) {}

    getProducts$(): Observable<IProduct[]> {
        return this.products$.asObservable();
    }

    searchProduct(title: string): void {
        this.productApi.getAllProductsByTitle(title).subscribe(products => this.products$.next(products));
    }
}
