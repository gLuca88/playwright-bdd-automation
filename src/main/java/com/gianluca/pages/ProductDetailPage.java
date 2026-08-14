package com.gianluca.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ProductDetailPage {

    private final Page page;

    public ProductDetailPage(Page page) {
        this.page = page;
    }


    public boolean verificaUrlProdotto(String urlProdotto) {
        return page.url().endsWith(urlProdotto);
    }
}
