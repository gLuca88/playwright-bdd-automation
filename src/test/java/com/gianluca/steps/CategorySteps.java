package com.gianluca.steps;

import com.gianluca.context.TestContext;
import com.gianluca.logging.LoggerUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.Request;
import com.microsoft.playwright.Response;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CategorySteps extends BaseSteps {

    private static final Logger logger =
            LoggerUtil.getLogger(CategorySteps.class);

    private static final String CATEGORIA_TEST = "Hand Tools";

    private static final String CATEGORIA_SLUG_TEST =
            CATEGORIA_TEST
                    .toLowerCase()
                    .replace(" ", "-");

    private String urlCategoriaSelezionata;
    private Response responseProdottiCategoria;

    public CategorySteps(TestContext testContext) {
        super(testContext);
    }

    // =========================
    // CATEGORY NAVIGATION
    // =========================

    @When("il visitatore seleziona una categoria")
    public void selezionaCategoria() {

        logger.info(
                "Selezione della categoria: {}",
                CATEGORIA_TEST
        );

        urlCategoriaSelezionata =
                pages().homePage()
                        .selezionaCategoria(CATEGORIA_TEST);

        logger.info(
                "Categoria selezionata - URL: {}",
                urlCategoriaSelezionata
        );
    }

    @Then("viene visualizzata la pagina della categoria selezionata")
    public void verificaPaginaCategoriaSelezionata() {

        logger.info(
                "Verifica navigazione alla categoria - URL atteso: {}",
                urlCategoriaSelezionata
        );

        boolean result =
                pages().categoryPage()
                        .verificaUrlCategoria(
                                urlCategoriaSelezionata
                        );

        assertTrue(
                result,
                "La pagina visualizzata non corrisponde alla categoria selezionata"
        );

        logger.info(
                "Navigazione alla categoria verificata - RESULT: {}",
                result
        );
    }

    // =========================
    // CATEGORY PAGE
    // =========================

    @Given("il visitatore si trova nella pagina di una categoria")
    public void navigaAllaPaginaCategoria() {

        logger.info(
                "Navigazione alla categoria: {}",
                CATEGORIA_TEST
        );

        responseProdottiCategoria =
                pages().homePage()
                        .selezionaCategoriaConResponse(
                                CATEGORIA_TEST
                        );

        logger.info(
                "Pagina categoria caricata - Response prodotti: {}",
                responseProdottiCategoria.url()
        );
    }

    @Then("il nome della categoria selezionata viene visualizzato come titolo della pagina")
    public void verificaNomeCategoriaNelTitolo() {

        logger.info(
                "Verifica titolo categoria - Valore atteso: {}",
                CATEGORIA_TEST
        );

        boolean result =
                pages().categoryPage()
                        .verificaTitoloCategoria(
                                CATEGORIA_TEST
                        );

        assertTrue(
                result,
                "Il nome della categoria selezionata non è visualizzato correttamente nel titolo della pagina"
        );

        logger.info(
                "Titolo categoria verificato - Categoria: {} - RESULT: {}",
                CATEGORIA_TEST,
                result
        );
    }

    @Then("vengono visualizzati solo prodotti appartenenti alla categoria selezionata")
    public void verificaProdottiCategoria() {

        logger.info(
                "Verifica prodotti visualizzati per la categoria: {}",
                CATEGORIA_TEST
        );

        // recuperiamo la request che ha generato la response intercettata
        Request request =
                responseProdottiCategoria.request();

        // recuperiamo il payload inviato al backend
        String payload =
                request.postData();

        // convertiamo il payload in json
        JsonObject requestPayload =
                JsonParser.parseString(payload)
                        .getAsJsonObject();

        // estraiamo dal payload la categoria richiesta al backend
        String categorySlug =
                requestPayload
                        .get("by_category_slug")
                        .getAsString();

        logger.info(
                "Categoria richiesta al backend - Slug: {}",
                categorySlug
        );

        // verifichiamo che la request sia relativa alla categoria selezionata
        assertEquals(
                CATEGORIA_SLUG_TEST,
                categorySlug,
                "La richiesta prodotti non è relativa alla categoria selezionata"
        );

        // convertiamo il body della response in stringa
        String body =
                new String(
                        responseProdottiCategoria.body(),
                        StandardCharsets.UTF_8
                );

        // convertiamo la stringa della response in json
        JsonObject jsonResponse =
                JsonParser.parseString(body)
                        .getAsJsonObject();

        // estraiamo la proprietà data che contiene i prodotti della categoria
        JsonArray prodotti =
                jsonResponse.getAsJsonArray("data");

        // creiamo la lista con gli id dei prodotti restituiti dal backend
        List<String> idProdottiAttesi =
                new ArrayList<>();

        for (int i = 0; i < prodotti.size(); i++) {

            String idProdotto =
                    prodotti.get(i)
                            .getAsJsonObject()
                            .get("id")
                            .getAsString();

            idProdottiAttesi.add(idProdotto);
        }

        logger.info(
                "Prodotti attesi dalla response - Totale: {} - ID: {}",
                idProdottiAttesi.size(),
                idProdottiAttesi
        );

        // recuperiamo gli id dei prodotti visualizzati nella UI
        List<String> idProdottiVisualizzati =
                pages().categoryPage()
                        .getIdProdottiVisualizzati();

        logger.info(
                "Prodotti visualizzati nella UI - Totale: {} - ID: {}",
                idProdottiVisualizzati.size(),
                idProdottiVisualizzati
        );

        // verifichiamo che nella pagina sia presente almeno un prodotto
        assertFalse(
                idProdottiVisualizzati.isEmpty(),
                "Nessun prodotto visualizzato nella pagina della categoria"
        );

        // verifichiamo che tutti i prodotti visualizzati appartengano alla response
        assertTrue(
                idProdottiAttesi.containsAll(idProdottiVisualizzati),
                "Sono visualizzati prodotti non appartenenti alla categoria selezionata"
        );

        logger.info(
                "Verifica prodotti categoria completata - Categoria: {} - RESULT: true",
                CATEGORIA_TEST
        );
    }
}