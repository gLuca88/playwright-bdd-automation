package com.gianluca.pages;

import com.gianluca.config.ConfigReader;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.AriaRole;

public class HomePage {

    private final Page page;

    public HomePage(Page page) {
        this.page = page;
    }


    // =========================
    // LOCATORS
    // =========================

    private static final String SELETTORE_GRIGLIA_PRODOTTI =
            ".col-md-9 > .container";

    private static final String SELETTORE_CARD_PRODOTTO =
            ".card";

    private static final String TEST_ID_NOME_PRODOTTO =
            "product-name";

    private static final String TEST_ID_PREZZO_PRODOTTO =
            "product-price";

    private static final String SELETTORE_IMMAGINE_PRODOTTO =
            "img.card-img-top";


    private Locator grigliaProdotti() {
        return page.locator(SELETTORE_GRIGLIA_PRODOTTI);
    }

    private Locator cardProdotti() {
        return page.locator(SELETTORE_CARD_PRODOTTO);
    }

    private Locator buttonMenuCategorie() {

        return page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions()
                        .setName("Categories")
        );
    }

    private Locator categoriaByName(String nomeCategoria) {

        return page.getByRole(
                AriaRole.LINK,
                new Page.GetByRoleOptions()
                        .setName(nomeCategoria)
        );
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

    public boolean verificaPresenzaGrigliaProdotti() {

        grigliaProdotti().waitFor();

        return grigliaProdotti().isVisible();
    }

    public int getNumeroProdotti() {

        return cardProdotti().count();
    }

    public boolean verificaNumeroMinimoProdotti(int numeroMinimo) {

        cardProdotti().first().waitFor();

        int numeroProdotti =
                cardProdotti().count();

        return numeroProdotti >= numeroMinimo;
    }


    // =========================
    // PRODUCT CARD
    // =========================

    public boolean verificaImmaginePerOgniProdotto() {

        for (Locator cardProdotto : cardProdotti().all()) {

            Locator immagine =
                    cardProdotto.locator(
                            SELETTORE_IMMAGINE_PRODOTTO
                    );

            page.waitForCondition(
                    () -> ((Number) immagine.evaluate(
                            "img => img.naturalWidth"
                    )).intValue() > 0
            );

            String src =
                    immagine.getAttribute("src");

            if (src == null || src.trim().isEmpty()) {
                return false;
            }

            int larghezzaNaturale =
                    ((Number) immagine.evaluate(
                            "img => img.naturalWidth"
                    )).intValue();

            if (larghezzaNaturale <= 0) {
                return false;
            }
        }

        return true;
    }

    public boolean verificaNomePerOgniProdotto() {

        return verificaTestoPerOgniProdotto(
                TEST_ID_NOME_PRODOTTO
        );
    }

    public boolean verificaPrezzoPerOgniProdotto() {

        return verificaTestoPerOgniProdotto(
                TEST_ID_PREZZO_PRODOTTO
        );
    }

    public String selezionaProdotto() {

        Locator prodotto =
                cardProdotti().first();

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


    // =========================
    // CATEGORY NAVIGATION
    // =========================

    public String selezionaCategoria(String nomeCategoria) {

        buttonMenuCategorie().click();

        Locator categoria =
                categoriaByName(nomeCategoria);

        String urlCategoria =
                categoria.getAttribute("href");

        categoria.click();

        page.waitForURL(
                "**" + urlCategoria
        );

        return urlCategoria;
    }

    public Response selezionaCategoriaConResponse(String nomeCategoria) {

        buttonMenuCategorie().click();

        Locator categoria =
                categoriaByName(nomeCategoria);

        String urlCategoria =
                categoria.getAttribute("href");

        Response response =
                page.waitForResponse(
                        currentResponse ->
                                currentResponse.url()
                                        .endsWith("/products")
                                        && currentResponse.status() == 200,
                        categoria::click
                );

        page.waitForURL(
                "**" + urlCategoria
        );

        return response;
    }


    // =========================
    // PRIVATE SUPPORT
    // =========================

    private boolean verificaTestoPerOgniProdotto(String testId) {

        for (Locator cardProdotto : cardProdotti().all()) {

            Locator elemento =
                    cardProdotto.getByTestId(testId);

            if (!elemento.isVisible()) {
                return false;
            }

            String testo =
                    elemento.innerText().trim();

            if (testo.isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
