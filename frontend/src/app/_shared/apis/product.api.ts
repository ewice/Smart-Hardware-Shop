import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { IProduct } from '../types/product.interface';

@Injectable({
    providedIn: 'root'
})
export class ProductApi {
    constructor(private http: HttpClient) {}

    getAllProductsByTitle(title: string): Observable<IProduct[]> {
        return this.http.get<IProduct[]>('/api/products?title.contains=' + title);
    }
}
