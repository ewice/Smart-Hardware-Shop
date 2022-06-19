import { Component, Input, OnInit } from '@angular/core';
import { CartItemService } from 'src/app/_shared/services/cart-item.service';
import { IProduct } from '../../../../_shared/types/product.interface';

@Component({
    selector: 'app-product',
    templateUrl: './product.component.html',
    styleUrls: ['./product.component.scss']
})
export class ProductComponent implements OnInit {
    @Input() product: IProduct | undefined;

    constructor(private cartItemService: CartItemService) {}

    ngOnInit(): void {}

    addProductToCart(): void {
        if (this.product) this.cartItemService.setCartItem(this.product);
    }
}
