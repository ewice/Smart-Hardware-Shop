import { Component, OnInit } from '@angular/core';
import { ProductService } from 'src/app/_shared/services/product.service';

@Component({
    selector: 'app-product-list',
    templateUrl: './product-list.component.html',
    styleUrls: ['./product-list.component.scss']
})
export class ProductListComponent implements OnInit {
    constructor(public productService: ProductService) {}

    ngOnInit(): void {}
}
