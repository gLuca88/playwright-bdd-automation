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

public class ProductDetailSteps extends BaseSteps {


    private static final Logger logger =
            LoggerUtil.getLogger(ProductDetailSteps.class);

    private HomePage homePage;
    private ProductDetailPage productDetailPage;
    private String urlProdottoCorrelatoSelezionato;

    public ProductDetailSteps(TestContext testContext) {
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
            productDetailPage = getPageManager().productDetailPage();
        }

        return productDetailPage;
    }


    // =========================
    // PRODUCT DETAIL
    // =========================
    @Given("il visitatore si trova nella pagina di dettaglio di un prodotto")
    public void navigaAllaPaginaDettaglioProdotto() {

        logger.info(
                "Navigazione alla pagina di dettaglio di un prodotto"
        );

        homePage().selezionaProdotto();

        logger.info(
                "Pagina di dettaglio del prodotto caricata"
        );
    }

    @Then("viene visualizzata l'immagine del prodotto")
    public void verificaImmagineProdotto() {

        logger.info(
                "Verifica visualizzazione immagine del prodotto"
        );

        boolean result =
                productDetailPage().verificaImmagineProdotto();

        assertTrue(
                result,
                "L'immagine del prodotto non è visualizzata correttamente"
        );

        logger.info(
                "Immagine del prodotto visualizzata correttamente - RESULT: {}",
                result
        );
    }

    @And("viene visualizzato il nome del prodotto")
    public void verificaNomeProdotto() {

        logger.info(
                "Verifica visualizzazione nome del prodotto"
        );

        boolean result =
                productDetailPage().verificaNomeProdotto();

        assertTrue(
                result,
                "Il nome del prodotto non è visualizzato correttamente"
        );

        logger.info(
                "Nome del prodotto visualizzato correttamente - RESULT: {}",
                result
        );
    }


    @And("viene visualizzata la descrizione del prodotto")
    public void verificaDescrizioneProdotto() {

        logger.info(
                "Verifica visualizzazione descrizione del prodotto"
        );

        boolean result =
                productDetailPage().verificaDescrizioneProdotto();

        assertTrue(
                result,
                "La descrizione del prodotto non è visualizzata correttamente"
        );

        logger.info(
                "Descrizione del prodotto visualizzata correttamente - RESULT: {}",
                result
        );
    }

    @And("viene visualizzato il prezzo del prodotto")
    public void verificaPrezzoProdotto() {

        logger.info(
                "Verifica visualizzazione prezzo del prodotto"
        );

        boolean result =
                productDetailPage().verificaPrezzoProdotto();

        assertTrue(
                result,
                "Il prezzo del prodotto non è visualizzato correttamente"
        );

        logger.info(
                "Prezzo del prodotto visualizzato correttamente - RESULT: {}",
                result
        );
    }

    @And("viene visualizzato il badge della categoria")
    public void verificaBadgeCategoria() {

        logger.info(
                "Verifica visualizzazione badge della categoria"
        );

        boolean result =
                productDetailPage().verificaBadgeCategoria();

        assertTrue(
                result,
                "Il badge della categoria non è visualizzato correttamente"
        );

        logger.info(
                "Badge della categoria visualizzato correttamente - RESULT: {}",
                result
        );
    }

    @And("viene visualizzato il badge del brand")
    public void verificaBadgeBrand() {

        logger.info(
                "Verifica visualizzazione badge del brand"
        );

        boolean result =
                productDetailPage().verificaBadgeBrand();

        assertTrue(
                result,
                "Il badge del brand non è visualizzato correttamente"
        );

        logger.info(
                "Badge del brand visualizzato correttamente - RESULT: {}",
                result
        );
    }

    @Then("viene visualizzata la sezione dei prodotti correlati")
    public void verificaSezioneProdottiCorrelati() {

        logger.info(
                "Verifica presenza sezione prodotti correlati"
        );

        boolean result =
                productDetailPage().verificaPresenzaGrigliaProdottiCorrelati();

        assertTrue(
                result,
                "La sezione dei prodotti correlati non è visualizzata"
        );

        logger.info(
                "Sezione prodotti correlati visualizzata correttamente - RESULT: {}",
                result
        );
    }

    @And("viene visualizzato almeno un prodotto correlato")
    public void verificaPresenzaProdottiCorrelati() {

        logger.info(
                "Verifica presenza prodotti correlati"
        );

        boolean result =
                productDetailPage()
                        .verificaNumeroMinimoProdottiCorrelati(1);

        int numeroProdotti =
                productDetailPage()
                        .getNumeroProdottiCorrelati();

        assertTrue(
                result,
                "La sezione non contiene prodotti correlati"
        );

        logger.info(
                "Prodotti correlati presenti - prodotti trovati: {} - RESULT: {}",
                numeroProdotti,
                result
        );
    }

    @When("il visitatore seleziona un prodotto correlato")
    public void selezionaProdottoCorrelato() {

        logger.info(
                "Selezione di un prodotto correlato"
        );

        urlProdottoCorrelatoSelezionato =
                productDetailPage().selezionaProdottoCorrelato();

        logger.info(
                "Prodotto correlato selezionato con URL: {}",
                urlProdottoCorrelatoSelezionato
        );
    }

    @Then("viene visualizzata la pagina di dettaglio del prodotto correlato")
    public void verificaPaginaDettaglioProdottoCorrelato() {

        logger.info(
                "Verifica navigazione alla pagina di dettaglio del prodotto correlato: {}",
                urlProdottoCorrelatoSelezionato
        );

        boolean result =
                productDetailPage().verificaUrlProdotto(
                        urlProdottoCorrelatoSelezionato
                );

        assertTrue(
                result,
                "La pagina visualizzata non corrisponde al prodotto correlato selezionato"
        );

        logger.info(
                "Navigazione alla pagina di dettaglio del prodotto correlato verificata - RESULT: {}",
                result
        );
    }

}
