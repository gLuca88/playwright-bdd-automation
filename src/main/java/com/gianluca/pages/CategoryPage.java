package com.gianluca.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.ArrayList;
import java.util.List;

public class CategoryPage {

    private final Page page;

    public CategoryPage(Page page) {
        this.page = page;
    }

    // =========================
    // LOCATORS
    // =========================

    private static final String TEST_ID_TITOLO_PAGINA = "page-title";
    private static final String SELETTORE_CARD_PRODOTTO =
            ".card";

    private Locator titoloPaginaCategoria() {

        return page.getByTestId(
                TEST_ID_TITOLO_PAGINA
        );
    }

    private Locator cardProdotti() {
        return page.locator(SELETTORE_CARD_PRODOTTO);
    }

    // =========================
    // NAVIGATION
    // =========================

    public boolean verificaUrlCategoria(String urlCategoria) {

        return page.url().endsWith(urlCategoria);
    }

    public boolean verificaTitoloCategoria(String nomeCategoria) {

        titoloPaginaCategoria().waitFor();

        String titolo =
                titoloPaginaCategoria()
                        .innerText()
                        .trim();

        return titolo.equals(
                "Category: " + nomeCategoria
        );
    }

    public List<String> getIdProdottiVisualizzati() {

        cardProdotti().first().waitFor();

        List<String> idProdotti = new ArrayList<>();

        for (Locator card : cardProdotti().all()) {

            String href = card.getAttribute("href");

            String idProdotto =
                    href.substring(
                            href.lastIndexOf("/") + 1
                    );

            idProdotti.add(idProdotto);
        }

        return idProdotti;
    }
}
