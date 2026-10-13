# Playwright BDD Automation

Framework di Test Automation sviluppato in Java che utilizza Playwright per l'automazione browser e Cucumber (BDD) per la definizione degli scenari. Il progetto automatizza l'applicazione "Practice Software Testing" e mira a fornire una struttura mantenibile, organizzata e scalabile per test end-to-end.

## Panoramica

Questo repository contiene un framework BDD che esegue scenari Gherkin tramite Cucumber su Playwright. Le test-suite verificano caratteristiche dell'interfaccia: alcuni scenari utilizzano l'intercettazione di request/response di rete tramite Playwright per supportare le validazioni UI. Il framework include gestione di artefatti (trace, video, screenshot), logging e integrazione con Allure per il reporting.

## Stack Tecnologico

Le dipendenze e le versioni sono quelle configurate in `pom.xml`.

| Tecnologia | Versione | Utilizzo nel progetto |
|---|---:|---|
| Java (release) | 21 | Linguaggio di implementazione |
| Microsoft Playwright | 1.62.0 | Automazione browser (API Playwright Java) |
| Cucumber JVM | 7.34.3 | BDD — step definitions e integrazione con JUnit Platform |
| cucumber-picocontainer | 7.34.3 | Dependency injection dei context nelle Step Definitions |
| JUnit Jupiter / JUnit Platform | 5.14.2 / 1.14.2 | Esecuzione della suite Cucumber tramite JUnit Platform |
| Allure | 2.29.1 (report) / allure-maven-plugin 2.12.0 | Generazione e allegato artefatti per report |
| AssertJ | 3.26.0 | Libreria di assertion disponibile nel progetto |
| Log4j2 | 2.25.1 | Logging configurato per esecuzioni globali e per scenario |

## Architettura del framework

Componenti principali e responsabilità (derivato dalle classi presenti nel codice):

| Componente | Responsabilità |
|---|---|
| Page Objects (`com.gianluca.pages`) | Incapsulano locators e azioni sulle pagine: `HomePage`, `ProductDetailPage`, `CategoryPage`, `ContactPage`. |
| PageManager (`com.gianluca.pages.PageManager`) | Centralizza accesso ai Page Objects; inizializzazione lazy dei page object per singolo `Page`. |
| Step Definitions (`com.gianluca.steps`) | Implementano i passi Gherkin. `BaseSteps` fornisce helper per ottenere `PageManager`. |
| TestContext (`com.gianluca.context.TestContext`) | Contiene oggetti condivisi dello scenario: Playwright, Browser, BrowserContext, Page, PageManager. |
| Hooks (`com.gianluca.hooks.Hooks`) | Setup/teardown dello scenario: inizializza risorse Playwright, avvia/ferma trace, acquisisce artefatti e allega ad Allure. |
| PlaywrightFactory (`com.gianluca.factory.PlaywrightFactory`) | Factory centralizzata per la creazione e la chiusura di Playwright, Browser, BrowserContext e Page; il ciclo di vita dello scenario è orchestrato dai `Hooks` che la utilizzano. |
| Browser Factories (`com.gianluca.factory.browser`) | Implementazioni per browser specifici (es. `ChromiumBrowserFactory`). Selezione browser tramite `config-*.properties`. |
| ConfigReader (`com.gianluca.config.ConfigReader`) | Caricamento dei file `config-<env>.properties` e accesso alle proprietà con valori di default (`env` di default `qa`). |
| Artifact Manager (`com.gianluca.artifact.PlaywrightArtifactManager`) | Gestione salvataggio trace (zip), screenshot e video (directory `target/artifacts/...`). |
| Report Manager (`com.gianluca.report.AllureReportManager`) | Allegati dei file (screenshot, trace, video e log) al report Allure. |
| Logger Util (`com.gianluca.logging.LoggerUtil`) | Utility per Log4j2: impostazione ThreadContext per log dedicati allo scenario e recupero del path del log per attachment. |

Collaborazione tra componenti (sintesi):

1. I Hooks creano e inizializzano risorse Playwright tramite `PlaywrightFactory` all'inizio dello scenario.
2. Viene creato un `Page` e passato a `PageManager`, il quale fornisce Page Objects alle Step Definitions tramite `BaseSteps` e `TestContext` (iniettato da PicoContainer/Cucumber).
3. Durante l'esecuzione delle step, i Page Objects interagiscono con Playwright (navigazioni, attese, click, intercettazioni response).
4. Al termine dello scenario i `Hooks` utilizzano `PlaywrightArtifactManager` e `AllureReportManager` per generare/recuperare artefatti e allegarli al report; infine chiudono risorse Playwright e puliscono il `TestContext`.

## Flusso di esecuzione (scenario reale)

Esempio di flusso durante l'esecuzione di un singolo scenario:

```
Cucumber Scenario
        ↓
Hooks (@Before)
        ↓
PlaywrightFactory
        ↓
TestContext + PageManager
        ↓
Step Definitions
        ↓
Page Objects
        ↓
Playwright / Browser
        ↓
Hooks (@After)
        ↓
Artefatti + Allure + Teardown
```

@Before inizializza le risorse Playwright e prepara `TestContext` e `PageManager`.
Le Step Definitions accedono ai Page Object tramite `BaseSteps` e `PageManager`.
I Page Object incapsulano le interazioni con Playwright.
@After gestisce gli artefatti abilitati e il teardown delle risorse.

## Scenari automatizzati

Le feature presenti in `src/test/resources/features/Sprint1` coprono le aree implementate del progetto. Le step definitions corrispondenti sono disponibili in `src/test/java/com/gianluca/steps`.

| Feature (file) | Scenario | Tag |
|---|---|---|
| `product_overview.feature` | Visualizzazione del catalogo prodotti | @product-overview, @smoke |
| `product_overview.feature` | Visualizzazione delle informazioni dei prodotti | @product-overview, @regression |
| `product_overview.feature` | Navigazione al dettaglio di un prodotto | @product-overview, @smoke, @navigation |
| `product_detail.feature` | Visualizzazione delle informazioni del prodotto | @product-detail, @regression |
| `product_detail.feature` | Visualizzazione e navigazione dei prodotti correlati | @product-detail, @regression, @navigation |
| `category.feature` | Navigazione a una categoria | @category, @smoke, @navigation |
| `category.feature` | Visualizzazione del nome della categoria | @category, @regression |
| `category.feature` | Visualizzazione dei prodotti della categoria | @category, @regression |
| `contact.feature` | Visualizzazione del modulo di contatto | @contact, @smoke |
| `contact.feature` | Visualizzazione dei campi obbligatori | @contact, @regression |
| `contact.feature` | Validazione dei campi obbligatori | @contact, @regression, @validation |
| `contact.feature` | Validazione del formato email | @contact, @regression, @validation |
| `contact.feature` | Visualizzazione delle opzioni del subject | @contact, @regression |
| `contact.feature` | Validazione della lunghezza minima del messaggio | @contact, @regression, @validation |
| `contact.feature` | Invio corretto del modulo di contatto | @contact, @smoke, @regression |

Nota: la suite attualmente implementata include `ProductOverviewSteps`, `ProductDetailSteps`, `CategorySteps`, `ContactSteps` e `CommonSteps`.

## Struttura del progetto (sintesi)

Cartelle e classi chiave:

```
src/
  main/
    java/com/gianluca/
      artifact/
        IArtifactManager.java
        PlaywrightArtifactManager.java
      config/
        ConfigReader.java
      factory/
        PlaywrightFactory.java
        browser/
          IBrowserFactory.java
          ChromiumBrowserFactory.java
          FirefoxBrowserFactory.java
          WebkitBrowserFactory.java
      logging/
        LoggerUtil.java
      pages/
        PageManager.java
        HomePage.java
        ProductDetailPage.java
        CategoryPage.java
        ContactPage.java
      report/
        IReportManager.java
        AllureReportManager.java
      utils/
        JsonDataReader.java
  test/
    java/com/gianluca/
      context/
        TestContext.java
      data/
        ContactTestData.java
      hooks/
        Hooks.java
      runner/
        RunCucumberTest.java
      steps/
        BaseSteps.java
        CommonSteps.java
        ProductOverviewSteps.java
        ProductDetailSteps.java
        CategorySteps.java
        ContactSteps.java
      utils/
        JsonDataReaderTest.java
    resources/
      allure.properties
      config-qa.properties
      log4j2.xml
      features/
        Sprint1/
          product_overview.feature
          product_detail.feature
          category.feature
          contact.feature
      testdata/
        contact-data.json
        json-reader-test.json
```

## Gestione dati di test e utility

La suite implementa una gestione centralizzata dei dati di test tramite JSON e mapping in record Java.

- `JsonDataReader` (`src/main/java/com/gianluca/utils/JsonDataReader.java`) legge risorse da classpath tramite `ClassLoader.getResourceAsStream(...)` e deserializza il file JSON in un tipo Java (`record`/POJO) con Gson.
- `ContactTestData` (`src/test/java/com/gianluca/data/ContactTestData.java`) mappa i dati del modulo contatti a partire da `src/test/resources/testdata/contact-data.json`.
- Il file `contact-data.json` contiene dati di test validi per nome, cognome, email, subject e messaggio, oltre alla lista delle opzioni attese del campo `subject`.
- `JsonDataReaderTest` (`src/test/java/com/gianluca/utils/JsonDataReaderTest.java`) verifica il comportamento di lettura corretta e la gestione della eccezione quando il file JSON non esiste.

Questa utility è usata nelle step di `ContactSteps` per caricare dati validi e verificare le opzioni previste del modulo di contatto.

## Configurazione

Il caricamento della configurazione è gestito da `ConfigReader`. Per impostazione predefinita viene utilizzato l'environment `qa` (se non viene specificato `-Denv=...`). I file di configurazione sono `src/test/resources/config-<env>.properties`.

Proprietà principali (presenti in `config-qa.properties`):

| Proprietà | Descrizione | Valori |
|---|---|---|
| `base.url` | URL base dell'applicazione sotto test | es. `https://practicesoftwaretesting.com` |
| `browser.name` | Browser da utilizzare | `chromium` (altre opzioni previste: `firefox`, `webkit`) |
| `browser.headless` | Esecuzione in headless o meno | `true` / `false` |
| `browser.timeout` | Timeout di default per Playwright (ms) | intero (es. 30000) |
| `playwright.trace.enabled` | Abilita acquisizione Playwright trace | `true` / `false` |
| `playwright.video.enabled` | Abilita registrazione video | `true` / `false` |
| `playwright.screenshot.enabled` | Abilita screenshot al fallimento | `true` / `false` |

Selezione dell'environment: passare `-Denv=<environment>` a Maven. Valore di default: `qa`.

Esempio: `mvn test -Denv=qa`.

## Esecuzione dei test

Comandi principali (Maven):

```powershell
mvn test
```

Per eseguire con l'environment `qa` (valore di default, ma mostrato per chiarezza):

```powershell
mvn test -Denv=qa
```

Nota: il runner Cucumber configurato in `RunCucumberTest` e il plugin Allure attivato in `pom.xml` generano report nella fase `verify`.

## Reporting, logging e artefatti

Artefatti e gestione reali implementati nel codice:

| Artefatto | Quando viene generato | Gestione/Utilizzo |
|---|---|---|
| Playwright Trace (zip) | `Hooks` avvia lo trace all'inizio e lo ferma nel teardown (salvato solo se `playwright.trace.enabled=true`) | Salvato in `target/artifacts/traces/*.zip` tramite `PlaywrightArtifactManager`; allegato ad Allure tramite `AllureReportManager` |
| Video (webm) | Se `playwright.video.enabled=true`, Playwright registra il video del context | Salvato in `target/artifacts/videos/`; il path viene recuperato e allegato ad Allure |
| Screenshot (png) | Solo in caso di scenario fallito (nel `@After`) | Salvato in `target/artifacts/screenshots/*.png`; allegato ad Allure |
| Log di esecuzione (per scenario) | Generato da Log4j2 con Routing basato sullo scenario | File `target/logs/scenarios/<scenario>.log`; allegato ad Allure nel teardown |
| Allure Results | Risultati Allure (adapter Cucumber) | Directory: `${project.build.directory}/allure-results` (configurata nel `pom.xml`) |

In caso di test fallito, lo `@After` gestisce gli artefatti abilitati dalla configurazione: acquisisce lo screenshot, finalizza trace e video quando previsti e allega gli artefatti disponibili al report Allure. Al termine viene allegato anche il log dello scenario.

## Scelte architetturali (confermate dal codice)

- Separazione chiara tra Step Definitions e Page Objects: le step contengono le asserzioni e la logica di verifica, mentre le Page Object espongono metodi di interazione con l'interfaccia.
- Page Object Model completo per le pagine del dominio: `HomePage`, `ProductDetailPage`, `CategoryPage`, `ContactPage` e `PageManager` come access point centralizzato.
- PageManager fornisce accesso centralizzato ai Page Object e applica inizializzazione lazy (vedi `PageManager`).
- Condivisione dello stato dello scenario tramite `TestContext`, iniettato nelle step tramite PicoContainer (presenza della dipendenza `cucumber-picocontainer` e costruttori `TestContext` nelle step).
- Il ciclo di vita Playwright è orchestrato dagli `Hooks`, che utilizzano `PlaywrightFactory` per creare e chiudere centralmente le risorse Playwright.
- Gestione degli artefatti separata: `PlaywrightArtifactManager` si occupa di creazione/salvataggio degli artefatti; `AllureReportManager` li allega al report.
- Logging per scenario tramite Log4j2 con Routing e `LoggerUtil` per gestire `ThreadContext`, permettendo file di log indipendenti per scenario.
- `BaseSteps` centralizza l'accesso al `TestContext` e al `PageManager`, riducendo la duplicazione nelle step definitions.
- `Hooks.beforeScenario` imposta `playwright.selectors().setTestIdAttribute("data-test")` per usare identificatori test id stabili (`getByTestId`) sui componenti UI.
- La suite implementa una gestione centralizzata dei test data JSON attualmente utilizzata dagli scenari del modulo Contact (`JsonDataReader` + `ContactTestData`).

## Struttura delle feature Cucumber e Sprint 1

Il repository è strutturato in modo da organizzare le feature per sprint. L'attuale implementazione del Sprint 1 include quattro aree funzionali complete:

- `Product Overview`: catalogo, informazioni prodotto e navigazione verso il dettaglio.
- `Product Detail`: dettaglio del prodotto e prodotti correlati.
- `Category`: navigazione per categoria e verifica della pagina/categoria dei prodotti.
- `Contact`: modulo contatti, validazione campi, subject, email, messaggio e invio positivo.

Le feature sono organizzate in `src/test/resources/features/Sprint1/*.feature` e sono eseguite tramite il runner `RunCucumberTest` con engine `cucumber-junit-platform-engine`.

## Riepilogo

Il framework attuale dimostra un'architettura BDD in Java con Playwright, organizzata per scalabilità e manutenzione. Il progetto include:

- Page Object Model con inizializzazione lazy di Page Object
- Gestione condivisa dello stato dello scenario via `TestContext`
- Dependency injection con PicoContainer
- Lifecycle del browser e dello scenario
- Centralizzazione di configurazione, logging e artefatti
- Allure integration per report e attachment
- Gestione centralizzata dei test data JSON per il modulo Contact tramite `JsonDataReader`
- Implementazione reale del Sprint 1 completo, incluso il modulo `Contact`
- Un set di test Cucumber coerente con la struttura attuale del codice e delle risorse
