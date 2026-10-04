package com.gianluca.steps;

import com.gianluca.context.TestContext;
import com.gianluca.logging.LoggerUtil;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ContactSteps extends BaseSteps {

    private static final Logger logger =
            LoggerUtil.getLogger(ContactSteps.class);

    public ContactSteps(TestContext testContext) {
        super(testContext);
    }

    // =========================
    // CONTACT NAVIGATION
    // =========================

    @Given("il visitatore si trova nella pagina dei contatti")
    public void navigaAllaPaginaContatti() {

        logger.info("Navigazione alla pagina dei contatti");

        pages().homePage()
                .openHomePage();

        pages().homePage()
                .navigaAllaPaginaContatti();

        logger.info("Pagina dei contatti caricata");
    }

    @Then("viene visualizzato il modulo di contatto")
    public void verificaVisualizzazioneModuloContatti() {

        logger.info("Verifica visualizzazione del modulo di contatto");

        String urlAtteso =
                pages().contactPage()
                        .getUrlAtteso();

        String urlCorrente =
                pages().contactPage()
                        .getUrlPaginaCorrente();

        boolean urlCorretta =
                pages().contactPage()
                        .verificaUrlPaginaContatti();

        logger.info(
                "Verifica URL pagina contatti - URL atteso: {} - URL corrente: {} - RESULT: {}",
                urlAtteso,
                urlCorrente,
                urlCorretta
        );

        boolean moduloVisibile =
                pages().contactPage()
                        .verificaModuloContatti();

        logger.info(
                "Verifica modulo contatti visibile - RESULT: {}",
                moduloVisibile
        );

        assertAll(
                "Verifica pagina dei contatti",

                () -> assertTrue(
                        urlCorretta,
                        "La pagina visualizzata non corrisponde alla pagina dei contatti"
                ),

                () -> assertTrue(
                        moduloVisibile,
                        "Il modulo di contatto non è visualizzato"
                )
        );

        logger.info(
                "Verifica pagina contatti completata - URL: {} - Modulo visibile: {}",
                urlCorrente,
                moduloVisibile
        );
    }

    @Then("vengono visualizzati tutti i campi obbligatori del modulo di contatto")
    public void verificaVisualizzazioneCampiObbligatori() {

        logger.info(
                "Verifica visualizzazione dei campi obbligatori del modulo di contatto"
        );

        boolean nomeVisibile =
                pages().contactPage()
                        .verificaCampoNomeVisibile();

        boolean cognomeVisibile =
                pages().contactPage()
                        .verificaCampoCognomeVisibile();

        boolean emailVisibile =
                pages().contactPage()
                        .verificaCampoEmailVisibile();

        boolean subjectVisibile =
                pages().contactPage()
                        .verificaCampoSubjectVisibile();

        boolean messaggioVisibile =
                pages().contactPage()
                        .verificaCampoMessaggioVisibile();

        logger.info(
                "Campi modulo contatti - Nome: {} - Cognome: {} - Email: {} - Subject: {} - Messaggio: {}",
                nomeVisibile,
                cognomeVisibile,
                emailVisibile,
                subjectVisibile,
                messaggioVisibile
        );

        assertAll(
                "Verifica visualizzazione campi obbligatori",

                () -> assertTrue(
                        nomeVisibile,
                        "Il campo nome non è visualizzato"
                ),

                () -> assertTrue(
                        cognomeVisibile,
                        "Il campo cognome non è visualizzato"
                ),

                () -> assertTrue(
                        emailVisibile,
                        "Il campo email non è visualizzato"
                ),

                () -> assertTrue(
                        subjectVisibile,
                        "Il campo subject non è visualizzato"
                ),

                () -> assertTrue(
                        messaggioVisibile,
                        "Il campo messaggio non è visualizzato"
                )
        );

        logger.info(
                "Verifica campi obbligatori completata - RESULT: true"
        );
    }

    @When("il visitatore invia il modulo di contatto senza compilare i campi obbligatori")
    public void inviaModuloSenzaCampiObbligatori() {

        logger.info(
                "Invio del modulo di contatto senza compilare i campi obbligatori"
        );

        pages().contactPage()
                .inviaModuloContatti();

        logger.info(
                "Modulo di contatto inviato senza compilare i campi obbligatori"
        );
    }

    @Then("viene visualizzato un errore per ogni campo obbligatorio")
    public void verificaErroriCampiObbligatori() {

        logger.info(
                "Verifica degli errori relativi ai campi obbligatori"
        );

        boolean erroreNomeVisibile =
                pages().contactPage()
                        .verificaErroreNomeVisibile();

        boolean erroreCognomeVisibile =
                pages().contactPage()
                        .verificaErroreCognomeVisibile();

        boolean erroreEmailVisibile =
                pages().contactPage()
                        .verificaErroreEmailVisibile();

        boolean erroreSubjectVisibile =
                pages().contactPage()
                        .verificaErroreSubjectVisibile();

        boolean erroreMessaggioVisibile =
                pages().contactPage()
                        .verificaErroreMessaggioVisibile();

        String erroreNome =
                pages().contactPage()
                        .getErroreNome();

        String erroreCognome =
                pages().contactPage()
                        .getErroreCognome();

        String erroreEmail =
                pages().contactPage()
                        .getErroreEmail();

        String erroreSubject =
                pages().contactPage()
                        .getErroreSubject();

        String erroreMessaggio =
                pages().contactPage()
                        .getErroreMessaggio();

        logger.info(
                "Errori campi obbligatori - Nome: [{}] - Cognome: [{}] - Email: [{}] - Subject: [{}] - Messaggio: [{}]",
                erroreNome,
                erroreCognome,
                erroreEmail,
                erroreSubject,
                erroreMessaggio
        );

        assertAll(
                "Verifica errori dei campi obbligatori",

                // Verifica visualizzazione degli alert
                () -> assertTrue(
                        erroreNomeVisibile,
                        "L'errore del campo First name non è visualizzato"
                ),

                () -> assertTrue(
                        erroreCognomeVisibile,
                        "L'errore del campo Last name non è visualizzato"
                ),

                () -> assertTrue(
                        erroreEmailVisibile,
                        "L'errore del campo Email non è visualizzato"
                ),

                () -> assertTrue(
                        erroreSubjectVisibile,
                        "L'errore del campo Subject non è visualizzato"
                ),

                () -> assertTrue(
                        erroreMessaggioVisibile,
                        "L'errore del campo Message non è visualizzato"
                ),

                // Verifica testo degli alert
                () -> assertEquals(
                        "First name is required",
                        erroreNome,
                        "Il testo dell'errore del campo First name non è corretto"
                ),

                () -> assertEquals(
                        "Last name is required",
                        erroreCognome,
                        "Il testo dell'errore del campo Last name non è corretto"
                ),

                () -> assertEquals(
                        "Email is required",
                        erroreEmail,
                        "Il testo dell'errore del campo Email non è corretto"
                ),

                () -> assertEquals(
                        "Subject is required",
                        erroreSubject,
                        "Il testo dell'errore del campo Subject non è corretto"
                ),

                () -> assertEquals(
                        "Message is required",
                        erroreMessaggio,
                        "Il testo dell'errore del campo Message non è corretto"
                )
        );

        logger.info(
                "Verifica degli errori dei campi obbligatori completata"
        );
    }

    @Given("il visitatore compila gli altri campi obbligatori con dati validi")
    public void compilaAltriCampiObbligatoriConDatiValidi() {

        logger.info("Compilazione degli altri campi obbligatori con dati validi");

        String nome = "Test";
        String cognome = "User";
        String subject = "Customer service";
        String messaggio =
                "This is a valid test message used to verify the contact form submission.";

        pages().contactPage().compilaNome(nome);
        pages().contactPage().compilaCognome(cognome);
        pages().contactPage().selezionaSubject(subject);
        pages().contactPage().compilaMessaggio(messaggio);

        logger.info(
                "Campi obbligatori compilati - Nome: {} - Cognome: {} - Subject: {}",
                nome,
                cognome,
                subject
        );
    }

    @When("inserisce nel campo email {string}")
    public void inserisceNelCampoEmail(String email) {

        logger.info(
                "Inserimento email nel modulo di contatto - Email: {}",
                email
        );

        pages().contactPage()
                .compilaEmail(email);

        logger.info(
                "Email inserita nel modulo di contatto - Email: {}",
                email
        );
    }

    @And("invia il modulo di contatto")
    public void inviaModuloDiContatto() {

        logger.info("Invio del modulo di contatto");

        pages().contactPage()
                .inviaModuloContatti();

        logger.info("Modulo di contatto inviato");
    }

    @Then("l'esito della validazione email è {string}")
    public void verificaEsitoValidazioneEmail(String esito) {

        logger.info(
                "Verifica esito validazione email - Esito atteso: {}",
                esito
        );

        if (esito.equals("non valida")) {

            boolean erroreVisibile =
                    pages().contactPage()
                            .verificaErroreEmailVisibile();

            String erroreEmail =
                    pages().contactPage()
                            .getErroreEmail();

            logger.info(
                    "Validazione email negativa - Errore visibile: {} - Messaggio: {}",
                    erroreVisibile,
                    erroreEmail
            );

            assertAll(
                    "Verifica errore validazione email",

                    () -> assertTrue(
                            erroreVisibile,
                            "L'errore di validazione dell'email non è visibile"
                    ),

                    () -> assertEquals(
                            "Email format is invalid",
                            erroreEmail,
                            "Il messaggio di errore dell'email non è corretto"
                    )
            );

        } else if (esito.equals("valida")) {

            boolean invioRiuscito =
                    pages().contactPage()
                            .verificaInvioModuloRiuscito();

            logger.info(
                    "Validazione email positiva - Invio modulo riuscito: {}",
                    invioRiuscito
            );

            assertTrue(
                    invioRiuscito,
                    "Il modulo non è stato inviato con un'email valida"
            );

        } else {

            fail(
                    "Esito della validazione email non gestito: " + esito
            );
        }

        logger.info(
                "Verifica esito validazione email completata - Esito atteso: {}",
                esito
        );
    }

    @Then("il subject contiene tutte le opzioni previste")
    public void verificaOpzioniSubject() {

        logger.info("Verifica delle opzioni disponibili nel Subject");

        List<String> opzioniAttese = List.of(
                "Customer service",
                "Webmaster",
                "Return",
                "Payments",
                "Warranty",
                "Status of order"
        );

        List<String> opzioniEffettive =
                pages().contactPage()
                        .getOpzioniSubject();

        logger.info(
                "Opzioni Subject recuperate dalla UI: {}",
                opzioniEffettive
        );

        List<String> opzioniSubject =
                opzioniEffettive.stream()
                        .filter(opzione -> !opzione.equals("Select a subject *"))
                        .toList();

        logger.info(
                "Opzioni Subject attese: {} - Opzioni Subject effettive: {}",
                opzioniAttese,
                opzioniSubject
        );

        assertEquals(
                opzioniAttese,
                opzioniSubject,
                "Le opzioni del Subject non corrispondono a quelle previste"
        );

        logger.info("Verifica delle opzioni del Subject completata");
    }


    @Given("il visitatore compila i campi obbligatori escluso il messaggio con dati validi")
    public void compilaCampiObbligatoriEsclusoMessaggio() {

        logger.info(
                "Compilazione dei campi obbligatori escluso il messaggio con dati validi"
        );

        String nome = "Test";
        String cognome = "User";
        String email = "testuser@example.com";
        String subject = "Customer service";

        pages().contactPage().compilaNome(nome);
        pages().contactPage().compilaCognome(cognome);
        pages().contactPage().compilaEmail(email);
        pages().contactPage().selezionaSubject(subject);

        logger.info(
                "Campi obbligatori compilati - Nome: {} - Cognome: {} - Email: {} - Subject: {}",
                nome,
                cognome,
                email,
                subject
        );
    }

    @When("inserisce un messaggio di {int} caratteri")
    public void inserisceMessaggioDiLunghezza(int lunghezza) {

        logger.info(
                "Inserimento messaggio nel modulo di contatto - Lunghezza richiesta: {}",
                lunghezza
        );

        String messaggio = "a".repeat(lunghezza);

        pages().contactPage()
                .compilaMessaggio(messaggio);

        logger.info(
                "Messaggio inserito nel modulo di contatto - Lunghezza: {}",
                messaggio.length()
        );
    }

    @Then("l'esito della validazione del messaggio è {string}")
    public void verificaEsitoValidazioneMessaggio(String esito) {

        logger.info(
                "Verifica esito validazione messaggio - Esito atteso: {}",
                esito
        );

        if (esito.equals("non valido")) {

            boolean erroreVisibile =
                    pages().contactPage()
                            .verificaErroreMessaggioVisibile();

            String erroreMessaggio =
                    pages().contactPage()
                            .getErroreMessaggio();

            logger.info(
                    "Validazione messaggio negativa - Errore visibile: {} - Messaggio: {}",
                    erroreVisibile,
                    erroreMessaggio
            );

            assertAll(
                    "Verifica errore validazione messaggio",

                    () -> assertTrue(
                            erroreVisibile,
                            "L'errore di validazione del messaggio non è visibile"
                    ),

                    () -> assertEquals(
                            "Message must be minimal 50 characters",
                            erroreMessaggio,
                            "Il messaggio di errore non è corretto"
                    )
            );

        } else if (esito.equals("valido")) {

            boolean invioRiuscito =
                    pages().contactPage()
                            .verificaInvioModuloRiuscito();

            logger.info(
                    "Validazione messaggio positiva - Invio modulo riuscito: {}",
                    invioRiuscito
            );

            assertTrue(
                    invioRiuscito,
                    "Il modulo non è stato inviato con un messaggio valido"
            );

        } else {

            fail(
                    "Esito della validazione del messaggio non gestito: " + esito
            );
        }

        logger.info(
                "Verifica esito validazione messaggio completata - Esito atteso: {}",
                esito
        );
    }

    @Given("il visitatore compila tutti i campi obbligatori con dati validi")
    public void compilaTuttiCampiObbligatoriConDatiValidi() {

        logger.info(
                "Compilazione di tutti i campi obbligatori con dati validi"
        );

        String nome = "Test";
        String cognome = "User";
        String email = "testuser@example.com";
        String subject = "Customer service";
        String messaggio =
                "This is a valid test message used to verify the contact form submission.";

        pages().contactPage().compilaNome(nome);
        pages().contactPage().compilaCognome(cognome);
        pages().contactPage().compilaEmail(email);
        pages().contactPage().selezionaSubject(subject);
        pages().contactPage().compilaMessaggio(messaggio);

        logger.info(
                "Tutti i campi obbligatori compilati - Nome: {} - Cognome: {} - Email: {} - Subject: {}",
                nome,
                cognome,
                email,
                subject
        );
    }

    @Then("viene visualizzato un messaggio di conferma")
    public void verificaMessaggioConferma() {

        logger.info("Verifica del messaggio di conferma");

        String messaggioConferma =
                pages().contactPage()
                        .getMessaggioConferma();

        logger.info(
                "Messaggio di conferma visualizzato: [{}]",
                messaggioConferma
        );

        assertEquals(
                "Thanks for your message! We will contact you shortly.",
                messaggioConferma,
                "Il messaggio di conferma non corrisponde a quello atteso"
        );

        logger.info("Verifica del messaggio di conferma completata");
    }

    @And("il modulo di contatto non viene più visualizzato")
    public void verificaModuloContattiNonVisualizzato() {

        logger.info(
                "Verifica che il modulo di contatto non sia più visualizzato"
        );

        boolean moduloAssente =
                pages().contactPage()
                        .verificaModuloContattiAssente();

        logger.info(
                "Modulo di contatto non visualizzato - RESULT: {}",
                moduloAssente
        );

        assertTrue(
                moduloAssente,
                "Il modulo di contatto è ancora visualizzato dopo l'invio"
        );

        logger.info(
                "Verifica assenza del modulo di contatto completata"
        );
    }
}

