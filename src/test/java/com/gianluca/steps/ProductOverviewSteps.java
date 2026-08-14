package com.gianluca.steps;

import com.gianluca.context.TestContext;
import com.gianluca.logging.LoggerUtil;
import com.gianluca.pages.HomePage;
import com.gianluca.pages.ProductDetailPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProductOverviewSteps extends BaseSteps {

    private static final Logger logger =
            LoggerUtil.getLogger(ProductOverviewSteps.class);

    private HomePage homePage;
    private ProductDetailPage productDetailPage;

    private String urlProdottoSelezionato;

    public ProductOverviewSteps(TestContext testContext) {
        super(testContext);
    }


    // =========================
    // PAGE OBJECTS
    // =========================

    private HomePage homePage() {

        if (homePage == null) {
            homePage = getPageManager().homePage();
        }

        return homePage;
    }

    private ProductDetailPage productDetailPage() {

        if (productDetailPage == null) {
            productDetailPage =
                    getPageManager().productDetailPage();
        }

        return productDetailPage;
    }


    // =========================
    // PRODUCT OVERVIEW
    // =========================

    @Then("viene visualizzata la griglia dei prodotti")
    public void verificaPresenzaGriglia() {

        logger.info(
                "Verifica presenza griglia prodotti"
        );

        boolean result =
                homePage().verificaPresenzaGrigliaProdotti();

        assertTrue(
                result,
                "La griglia dei prodotti non è visualizzata"
        );

        logger.info(
                "Griglia prodotti visualizzata correttamente - RESULT: {}",
                result
        );
    }


    @And("viene visualizzato almeno un prodotto")
    public void verificaPresenzaProdotti() {

        logger.info(
                "Verifica presenza prodotti nel catalogo"
        );

        boolean result =
                homePage().verificaNumeroMinimoProdotti(1);

        int numeroProdotti =
                homePage().getNumeroProdotti();

        assertTrue(
                result,
                "Il catalogo non contiene prodotti"
        );

        logger.info(
                "Catalogo popolato correttamente - prodotti trovati: {} - RESULT: {}",
                numeroProdotti,
                result
        );
    }


    @Given("il catalogo prodotti è visualizzato")
    public void verificaCatalogoProdottiVisualizzato() {

        logger.info(
                "Verifica presenza catalogo prodotti"
        );

        boolean result =
                homePage().verificaNumeroMinimoProdotti(1);

        int numeroProdotti =
                homePage().getNumeroProdotti();

        assertTrue(
                result,
                "Il catalogo prodotti non è visualizzato"
        );

        logger.info(
                "Catalogo visualizzato correttamente - prodotti trovati: {} - RESULT: {}",
                numeroProdotti,
                result
        );
    }


    // =========================
    // PRODUCT INFORMATION
    // =========================

    @Then("ogni prodotto visualizza un'immagine")
    public void verificaImmagineProdotti() {

        int numeroProdotti =
                homePage().getNumeroProdotti();

        logger.info(
                "Verifica immagine su {} prodotti",
                numeroProdotti
        );

        boolean result =
                homePage().verificaImmaginePerOgniProdotto();

        assertTrue(
                result,
                "Uno o più prodotti non visualizzano correttamente l'immagine"
        );

        logger.info(
                "Immagini verificate correttamente su tutti i {} prodotti - RESULT: {}",
                numeroProdotti,
                result
        );
    }


    @And("ogni prodotto visualizza il nome")
    public void verificaNomeProdotti() {

        int numeroProdotti =
                homePage().getNumeroProdotti();

        logger.info(
                "Verifica nome su {} prodotti",
                numeroProdotti
        );

        boolean result =
                homePage().verificaNomePerOgniProdotto();

        assertTrue(
                result,
                "Uno o più prodotti non visualizzano il nome"
        );

        logger.info(
                "Nome verificato correttamente su tutti i {} prodotti - RESULT: {}",
                numeroProdotti,
                result
        );
    }


    @And("ogni prodotto visualizza il prezzo")
    public void verificaPrezzoProdotti() {

        int numeroProdotti =
                homePage().getNumeroProdotti();

        logger.info(
                "Verifica prezzo su {} prodotti",
                numeroProdotti
        );

        boolean result =
                homePage().verificaPrezzoPerOgniProdotto();

        assertTrue(
                result,
                "Uno o più prodotti non visualizzano il prezzo"
        );

        logger.info(
                "Prezzo verificato correttamente su tutti i {} prodotti - RESULT: {}",
                numeroProdotti,
                result
        );
    }


    // =========================
    // PRODUCT NAVIGATION
    // =========================

    @When("il visitatore seleziona un prodotto")
    public void selezionaProdotto() {

        logger.info(
                "Selezione di un prodotto dal catalogo"
        );

        urlProdottoSelezionato =
                homePage().selezionaProdotto();

        logger.info(
                "Prodotto selezionato con URL: {}",
                urlProdottoSelezionato
        );
    }


    @Then("viene visualizzata la pagina di dettaglio del prodotto selezionato")
    public void verificaPaginaDettaglioProdotto() {

        logger.info(
                "Verifica navigazione alla pagina prodotto: {}",
                urlProdottoSelezionato
        );

        boolean result =
                productDetailPage().verificaUrlProdotto(
                        urlProdottoSelezionato
                );

        assertTrue(
                result,
                "La pagina visualizzata non corrisponde al prodotto selezionato"
        );

        logger.info(
                "Navigazione alla pagina di dettaglio verificata - RESULT: {}",
                result
        );
    }
}