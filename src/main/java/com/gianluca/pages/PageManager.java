package com.gianluca.pages;

import com.microsoft.playwright.Page;

public class PageManager {


    private final Page page;

    private HomePage homePage;
    private ProductDetailPage productDetailPage;
    private CategoryPage categoryPage;

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

    public CategoryPage categoryPage() {

        if (categoryPage == null) {
            categoryPage = new CategoryPage(page);
        }

        return categoryPage;
    }
}
