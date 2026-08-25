package com.gianluca.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ProductDetailPage {

    private final Page page;

    public ProductDetailPage(Page page) {
        this.page = page;
    }

    private static final String SELETTORE_IMMAGINE_PRODOTTO = "img.figure-img";
    private static final String TEST_ID_NOME_PRODOTTO = "product-name";
    private static final String TEST_ID_DESCRIZIONE_PRODOTTO = "product-description";
    private static final String TEST_ID_PREZZO_PRODOTTO = "unit-price";
    private static final String SELETTORE_BADGE_CATEGORIA = "[aria-label='category']";
    private static final String SELETTORE_BADGE_BRAND = "[aria-label='brand']";
    private static final String XPATH_GRIGLIA_PRODOTTI_CORRELATI =
            "//h2[normalize-space()='Related products']" +
                    "/following-sibling::div[contains(@class,'col')]" +
                    "/div[contains(@class,'container')]";
    private static final String SELETTORE_CARD_PRODOTTO_CORRELATO = ".card";


    // =========================
    // PRODUCT DETAIL
    // =========================

    public boolean verificaUrlProdotto(String urlProdotto) {

        return page.url().endsWith(urlProdotto);
    }

    private Locator immagineProdotto() {
        return page.locator(SELETTORE_IMMAGINE_PRODOTTO);
    }

    private Locator nomeProdotto() {
        return page.getByTestId(TEST_ID_NOME_PRODOTTO);
    }

    private Locator descrizioneProdotto() {
        return page.getByTestId(TEST_ID_DESCRIZIONE_PRODOTTO);
    }

    private Locator prezzoProdotto() {
        return page.getByTestId(TEST_ID_PREZZO_PRODOTTO);
    }

    private Locator badgeCategoria() {
        return page.locator(SELETTORE_BADGE_CATEGORIA);
    }

    private Locator badgeBrand() {
        return page.locator(SELETTORE_BADGE_BRAND);
    }


    private Locator grigliaProdottiCorrelati() {
        return page.locator(XPATH_GRIGLIA_PRODOTTI_CORRELATI);
    }

    private Locator cardProdottiCorrelati() {
        return grigliaProdottiCorrelati()
                .locator(SELETTORE_CARD_PRODOTTO_CORRELATO);
    }


    public boolean verificaImmagineProdotto() {

        Locator immagine = immagineProdotto();

        immagine.waitFor();

        String src = immagine.getAttribute("src");

        if (src == null || src.trim().isEmpty()) {
            return false;
        }

        int naturalWidth = ((Number) immagine.evaluate(
                "img => img.naturalWidth"
        )).intValue();

        return naturalWidth > 0;
    }

    public boolean verificaNomeProdotto() {

        Locator nome = nomeProdotto();

        nome.waitFor();

        if (!nome.isVisible()) {
            return false;
        }

        String testoNome = nome.innerText().trim();

        return !testoNome.isEmpty();
    }

    public boolean verificaDescrizioneProdotto() {

        Locator descrizione = descrizioneProdotto();

        descrizione.waitFor();

        if (!descrizione.isVisible()) {
            return false;
        }

        String testoDescrizione = descrizione.innerText().trim();

        return !testoDescrizione.isEmpty();
    }

    public boolean verificaPrezzoProdotto() {

        Locator prezzo = prezzoProdotto();

        prezzo.waitFor();

        if (!prezzo.isVisible()) {
            return false;
        }

        String testoPrezzo = prezzo.innerText().trim();

        return !testoPrezzo.isEmpty();
    }

    public boolean verificaBadgeCategoria() {

        Locator categoria = badgeCategoria();

        categoria.waitFor();

        if (!categoria.isVisible()) {
            return false;
        }

        String testoCategoria = categoria.innerText().trim();

        return !testoCategoria.isEmpty();
    }

    public boolean verificaBadgeBrand() {

        Locator brand = badgeBrand();

        brand.waitFor();

        if (!brand.isVisible()) {
            return false;
        }

        String testoBrand = brand.innerText().trim();

        return !testoBrand.isEmpty();
    }

    public boolean verificaPresenzaGrigliaProdottiCorrelati() {

        Locator griglia = grigliaProdottiCorrelati();

        griglia.waitFor();

        return griglia.isVisible();
    }

    public int getNumeroProdottiCorrelati() {
        return cardProdottiCorrelati().count();
    }

    public boolean verificaNumeroMinimoProdottiCorrelati(int numeroMinimo) {

        cardProdottiCorrelati().first().waitFor();

        int numeroProdotti = cardProdottiCorrelati().count();

        return numeroProdotti >= numeroMinimo;
    }

    public String selezionaProdottoCorrelato() {

        Locator prodottoCorrelato =
                cardProdottiCorrelati().first();

        prodottoCorrelato.waitFor();

        page.waitForCondition(
                () -> prodottoCorrelato.getAttribute("href") != null
        );

        String urlProdottoCorrelato =
                prodottoCorrelato.getAttribute("href");

        prodottoCorrelato.click();

        page.waitForURL(
                "**" + urlProdottoCorrelato
        );

        return urlProdottoCorrelato;
    }
}
