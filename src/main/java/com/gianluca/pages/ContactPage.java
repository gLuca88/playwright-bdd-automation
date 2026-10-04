package com.gianluca.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;

import java.util.List;

public class ContactPage {

    private final Page page;

    public ContactPage(Page page) {
        this.page = page;
    }

    // =========================
    // LOCATORS
    // =========================

    private static final String XPATH_MODULO_CONTATTI =
            "//h3[normalize-space()='Contact']/following-sibling::form";

    private static final String URL_CONTATTI = "/contact";

    // Form
    private static final String TEST_ID_NOME = "first-name";
    private static final String TEST_ID_COGNOME = "last-name";
    private static final String TEST_ID_EMAIL = "email";
    private static final String TEST_ID_SUBJECT = "subject";
    private static final String TEST_ID_MESSAGGIO = "message";
    private static final String TEST_ID_INVIA = "contact-submit";

    // Validation errors
    private static final String TEST_ID_ERRORE_NOME = "first-name-error";
    private static final String TEST_ID_ERRORE_COGNOME = "last-name-error";
    private static final String TEST_ID_ERRORE_EMAIL = "email-error";
    private static final String TEST_ID_ERRORE_SUBJECT = "subject-error";
    private static final String TEST_ID_ERRORE_MESSAGGIO = "message-error";
    private static final String SELETTORE_CONTAINER_CONTATTI =
            ".container.auth-container";

    // =========================
    // FORM LOCATORS
    // =========================

    private Locator moduloContatti() {
        return page.locator(XPATH_MODULO_CONTATTI);
    }

    private Locator campoNome() {
        return page.getByTestId(TEST_ID_NOME);
    }

    private Locator campoCognome() {
        return page.getByTestId(TEST_ID_COGNOME);
    }

    private Locator campoEmail() {
        return page.getByTestId(TEST_ID_EMAIL);
    }

    private Locator campoSubject() {
        return page.getByTestId(TEST_ID_SUBJECT);
    }

    private Locator campoMessaggio() {
        return page.getByTestId(TEST_ID_MESSAGGIO);
    }

    private Locator pulsanteInvia() {
        return page.getByTestId(TEST_ID_INVIA);
    }

    private Locator containerContatti() {
        return page.locator(SELETTORE_CONTAINER_CONTATTI);
    }

    private Locator messaggioConferma() {
        return containerContatti()
                .locator("div[role='alert'].alert-success");
    }

    // =========================
    // ERROR LOCATORS
    // =========================

    private Locator erroreNome() {
        return page.getByTestId(TEST_ID_ERRORE_NOME);
    }

    private Locator erroreCognome() {
        return page.getByTestId(TEST_ID_ERRORE_COGNOME);
    }

    private Locator erroreEmail() {
        return page.getByTestId(TEST_ID_ERRORE_EMAIL);
    }

    private Locator erroreSubject() {
        return page.getByTestId(TEST_ID_ERRORE_SUBJECT);
    }

    private Locator erroreMessaggio() {
        return page.getByTestId(TEST_ID_ERRORE_MESSAGGIO);
    }


    // =========================
    // PAGE VALIDATION
    // =========================

    public String getUrlPaginaCorrente() {
        return page.url();
    }

    public String getUrlAtteso() {
        return URL_CONTATTI;
    }

    public boolean verificaUrlPaginaContatti() {
        return page.url().endsWith(URL_CONTATTI);
    }

    public boolean verificaModuloContatti() {
        Locator modulo = moduloContatti();

        modulo.waitFor();
        return modulo.isVisible();
    }


    // =========================
    // FORM VALIDATION
    // =========================

    public boolean verificaCampoNomeVisibile() {
        return campoNome().isVisible();
    }

    public boolean verificaCampoCognomeVisibile() {
        return campoCognome().isVisible();
    }

    public boolean verificaCampoEmailVisibile() {
        return campoEmail().isVisible();
    }

    public boolean verificaCampoSubjectVisibile() {
        return campoSubject().isVisible();
    }

    public boolean verificaCampoMessaggioVisibile() {
        return campoMessaggio().isVisible();
    }


    // =========================
    // FORM ACTIONS
    // =========================

    public void compilaNome(String nome) {
        campoNome().fill(nome);
    }

    public void compilaCognome(String cognome) {
        campoCognome().fill(cognome);
    }

    public void selezionaSubject(String subject) {
        campoSubject().selectOption(
                new SelectOption().setLabel(subject)
        );
    }

    public void compilaMessaggio(String messaggio) {
        campoMessaggio().fill(messaggio);
    }

    public void compilaEmail(String email) {
        campoEmail().fill(email);
    }

    public void inviaModuloContatti() {
        pulsanteInvia().click();
    }


    // =========================
    // ERROR VALIDATION
    // =========================

    public boolean verificaErroreNomeVisibile() {
        return verificaErroreVisibile(erroreNome());
    }

    public boolean verificaErroreCognomeVisibile() {
        return verificaErroreVisibile(erroreCognome());
    }

    public boolean verificaErroreEmailVisibile() {
        return verificaErroreVisibile(erroreEmail());
    }

    public boolean verificaErroreEmailAssente() {
        return !erroreEmail().isVisible();
    }

    public boolean verificaErroreSubjectVisibile() {
        return verificaErroreVisibile(erroreSubject());
    }

    public boolean verificaErroreMessaggioVisibile() {
        return verificaErroreVisibile(erroreMessaggio());
    }

    public boolean verificaErroreMessaggioAssente() {
        return !erroreMessaggio().isVisible();
    }


    public String getErroreNome() {
        return getTestoErrore(erroreNome());
    }

    public String getErroreCognome() {
        return getTestoErrore(erroreCognome());
    }

    public String getErroreEmail() {
        return getTestoErrore(erroreEmail());
    }

    public String getErroreSubject() {
        return getTestoErrore(erroreSubject());
    }

    public String getErroreMessaggio() {
        return getTestoErrore(erroreMessaggio());
    }

    public String getMessaggioConferma() {
        Locator messaggio = messaggioConferma();

        messaggio.waitFor();

        return messaggio.innerText()
                .trim();
    }

    public boolean verificaModuloContattiAssente() {
        return !moduloContatti().isVisible();
    }


    public List<String> getOpzioniSubject() {

        return campoSubject()
                .locator("option")
                .allTextContents();
    }


    // =========================
    // PRIVATE METHODS
    // =========================

    private String getTestoErrore(Locator errore) {
        return errore.innerText()
                .trim();
    }

    private boolean verificaErroreVisibile(Locator errore) {
        errore.waitFor();
        return errore.isVisible();
    }
}
