import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductBannerComponent } from './product-banner.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('ProductBannerComponent', () => {
    let component: ProductBannerComponent;
    let fixture: ComponentFixture<ProductBannerComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            declarations: [ProductBannerComponent]
        }).compileComponents();

        fixture = TestBed.createComponent(ProductBannerComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
