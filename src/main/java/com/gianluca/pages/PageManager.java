package com.gianluca.pages;

import com.microsoft.playwright.Page;

public class PageManager {


    private final Page page;

    private HomePage homePage;
    private ProductDetailPage productDetailPage;

    public PageManager(Page page) {
        this.page = page;
    }

    public HomePage homePage() {

        if (homePage == null) {
            homePage = new HomePage(page);
        }

        return homePage;
    }

    public ProductDetailPage productDetailPage() {

        if (productDetailPage == null) {
            productDetailPage = new ProductDetailPage(page);
        }

        return productDetailPage;
    }
}
