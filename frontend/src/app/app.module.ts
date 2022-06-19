import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ProductListComponent } from './products/product-list/product-list.component';
import { CartComponent } from './cart/cart.component';
import { ProductComponent } from './products/product-list/components/product/product.component';
import { ProductBannerComponent } from './products/product-banner/product-banner.component';
import { ProductSearchComponent } from './products/product-search/product-search.component';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
    declarations: [
        AppComponent,
        ProductListComponent,
        CartComponent,
        ProductComponent,
        ProductBannerComponent,
        ProductSearchComponent
    ],
    imports: [BrowserModule, AppRoutingModule, HttpClientModule, ReactiveFormsModule],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule {}
