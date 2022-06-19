import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { NewsApi } from 'src/app/_shared/apis/news.api';
import { INews } from 'src/app/_shared/types/news.interface';

@Component({
    selector: 'app-product-banner',
    templateUrl: './product-banner.component.html',
    styleUrls: ['./product-banner.component.scss']
})
export class ProductBannerComponent implements OnInit {
    news$: Observable<INews[]> | undefined;

    constructor(private newsApi: NewsApi) {
        this.news$ = this.newsApi.getAllNews();
    }

    ngOnInit(): void {}
}
