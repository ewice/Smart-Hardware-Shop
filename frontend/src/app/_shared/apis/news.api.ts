import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { INews } from '../types/news.interface';

@Injectable({
    providedIn: 'root'
})
export class NewsApi {
    constructor(private http: HttpClient) {}

    getAllNews(): Observable<INews[]> {
        return this.http.get<INews[]>('/api/news');
    }
}
