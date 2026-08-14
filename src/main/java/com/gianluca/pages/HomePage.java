package com.gianluca.pages;

import com.gianluca.config.ConfigReader;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class HomePage {

    private final Page page;

    public HomePage(Page page) {
        this.page = page;
    }


    // =========================
    // LOCATORS
    // =========================

    private Locator grigliaProdotti() {
        return page.locator(".col-md-9 > .container");
    }

    private Locator cards() {
        return page.locator(".card");
    }


    // =========================
    // NAVIGATION
    // =========================

    public void openHomePage() {

        String url = ConfigReader.get("base.url");

        page.waitForResponse(
                currentResponse ->
                        currentResponse.url().equals(
                                "https://api.practicesoftwaretesting.com/products"
                        )
                                && currentResponse.status() == 200,
                () -> page.navigate(url)
        );
    }


    // =========================
    // PRODUCT OVERVIEW
    // =========================

    public boolean verificaPresenzaGriglia() {
        grigliaProdotti().waitFor();
        return grigliaProdotti().isVisible();
    }

    public int getNumeroProdotti() {
        return cards().count();
    }

    public boolean verificaNumeroMinimoProdotti(int numeroMinimo) {

        cards().first().waitFor();

        int numeroProdotti = cards().count();

        return numeroProdotti >= numeroMinimo;
    }


    // =========================
    // PRODUCT CARD
    // =========================

    public boolean verificaImmaginePerOgniProdotto() {

        for (Locator card : cards().all()) {

            Locator image =
                    card.locator("img.card-img-top");

            page.waitForCondition(
                    () -> ((Number) image.evaluate(
                            "img => img.naturalWidth"
                    )).intValue() > 0
            );

            String src =
                    image.getAttribute("src");

            if (src == null || src.trim().isEmpty()) {
                return false;
            }

            int naturalWidth = ((Number) image.evaluate(
                    "img => img.naturalWidth"
            )).intValue();

            if (naturalWidth <= 0) {
                return false;
            }
        }

        return true;
    }


    public boolean verificaNomePerOgniProdotto() {

        for (Locator card : cards().all()) {

            Locator nome = card.getByTestId("product-name");

            if (!nome.isVisible()) {
                return false;
            }

            String testoNome = nome.innerText().trim();

            if (testoNome.isEmpty()) {
                return false;
            }
        }

        return true;
    }


    public boolean verificaPrezzoPerOgniProdotto() {

        for (Locator card : cards().all()) {

            Locator prezzo = card.getByTestId("product-price");

            if (!prezzo.isVisible()) {
                return false;
            }

            String testoPrezzo = prezzo.innerText().trim();

            if (testoPrezzo.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public String selezionaProdotto() {

        Locator prodotto = cards().first();

        prodotto.waitFor();

        page.waitForCondition(
                () -> prodotto.getAttribute("href") != null
        );

        String urlProdotto =
                prodotto.getAttribute("href");

        prodotto.click();

        page.waitForURL(
                "**" + urlProdotto
        );

        return urlProdotto;
    }
}
