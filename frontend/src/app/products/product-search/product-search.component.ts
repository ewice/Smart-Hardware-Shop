import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ProductService } from 'src/app/_shared/services/product.service';

@Component({
    selector: 'app-product-search',
    templateUrl: './product-search.component.html',
    styleUrls: ['./product-search.component.scss']
})
export class ProductSearchComponent implements OnInit {
    form = new FormGroup({
        title: new FormControl('')
    });

    get title(): FormControl {
        return this.form.get('title') as FormControl;
    }

    constructor(private productService: ProductService) {}

    ngOnInit(): void {
        this.searchProduct();
    }

    searchProduct() {
        this.productService.searchProduct(this.title.value);
    }
}
