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
| Hooks (`com.gianluca.hooks.Hooks`) | Collegano gli eventi Cucumber `@Before` e `@After` ai metodi `start` e `finish` di `ScenarioLifecycle`. |
| ScenarioLifecycle (`com.gianluca.lifecycle.ScenarioLifecycle`) | Valida la configurazione, registra le risorse appena create e orchestra artefatti, reporting e cleanup, anche dopo errori di setup o teardown. |
| PlaywrightFactory (`com.gianluca.factory.PlaywrightFactory`) | Centralizza creazione e chiusura di Playwright, Browser, BrowserContext e Page. Se l'inizializzazione del timeout fallisce, chiude il context prima di rilanciare l'errore. |
| Browser Factories (`com.gianluca.factory.browser`) | Implementazioni per browser specifici (es. `ChromiumBrowserFactory`). Selezione browser tramite `config-*.properties`. |
| ConfigReader (`com.gianluca.config.ConfigReader`) | Carica `config-<env>.properties` (`qa` di default), applica gli override JVM e valida le proprietà necessarie prima dell'avvio del browser. |
| Artifact Manager (`com.gianluca.artifact.PlaywrightArtifactManager`) | Gestione salvataggio trace (zip), screenshot e video (directory `target/artifacts/...`). |
| Report Manager (`com.gianluca.report.AllureReportManager`) | Allegati dei file (screenshot, trace, video e log) al report Allure. |
| Logger Util (`com.gianluca.logging.LoggerUtil`) | Utility per Log4j2: impostazione ThreadContext per log dedicati allo scenario e recupero del path del log per attachment. |

Collaborazione tra componenti (sintesi):

1. I Hooks delegano l'avvio a `ScenarioLifecycle`, che valida la configurazione e crea le risorse tramite `PlaywrightFactory`, registrandole subito nel `TestContext`.
2. Viene creato un `Page` e passato a `PageManager`, il quale fornisce Page Objects alle Step Definitions tramite `BaseSteps` e `TestContext` (iniettato da PicoContainer/Cucumber).
3. Durante l'esecuzione delle step, i Page Objects interagiscono con Playwright (navigazioni, attese, click, intercettazioni response).
4. Al termine dello scenario i Hooks delegano il teardown a `ScenarioLifecycle`: ogni operazione di diagnostica e chiusura viene tentata separatamente; infine vengono puliti `TestContext` e il contesto di logging.

## Flusso di esecuzione (scenario reale)

Esempio di flusso durante l'esecuzione di un singolo scenario:

```
Cucumber Scenario
        ↓
Hooks (@Before)
        ↓
ScenarioLifecycle.start → ConfigReader.validate
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
ScenarioLifecycle.finish
        ↓
Artefatti + Allure + Teardown
```

`@Before` chiama `ScenarioLifecycle.start`, che inizializza le risorse e prepara `TestContext` e `PageManager`.
Le Step Definitions accedono ai Page Object tramite `BaseSteps` e `PageManager`.
I Page Object incapsulano le interazioni con Playwright.
`@After` chiama `ScenarioLifecycle.finish`, che gestisce gli artefatti abilitati e il teardown.

Se il setup fallisce, il lifecycle tenta subito di chiudere le risorse già create e
rilancia l'errore originale. Il successivo `@After` può essere eseguito senza
chiudere nuovamente le risorse già rimosse dal context. Nel teardown gli errori
vengono raccolti: se lo scenario era riuscito, il primo viene rilanciato e gli
altri aggiunti come eccezioni `suppressed`; se era già fallito, gli errori
secondari vengono registrati nei log per conservare il fallimento originale.
Il percorso del video viene recuperato solo dopo una chiusura riuscita del context.

`ScenarioLifecycle` dispone di un costruttore per l'esecuzione ordinaria e di un
costruttore con visibilità di package che riceve `Resources`, `IArtifactManager`
e `IReportManager`. `Resources` delega la creazione alla factory e permette di
sostituirla nei test. La classe interna `Failures` esegue le operazioni di
teardown separatamente e raccoglie le eccezioni.

## Scenari automatizzati

Le feature presenti in `src/test/resources/features/e2e` coprono le aree implementate del progetto. Le step definitions corrispondenti sono disponibili in `src/test/java/com/gianluca/steps`.

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

```text
.github/
  workflows/
    ci.yml
pom.xml
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
      config/
        ConfigReaderTest.java
      context/
        TestContext.java
      data/
        ContactTestData.java
      hooks/
        Hooks.java
      lifecycle/
        ScenarioLifecycle.java
        ScenarioLifecycleTest.java
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
        e2e/
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
| `browser.timeout` | Timeout di default per Playwright (ms) | intero maggiore di zero (default QA: 30000) |
| `playwright.trace.enabled` | Abilita acquisizione Playwright trace | `true` / `false` |
| `playwright.video.enabled` | Abilita registrazione video | `true` / `false` |
| `playwright.screenshot.enabled` | Abilita screenshot al fallimento | `true` / `false` |

Selezione dell'environment: passare `-Denv=<environment>` a Maven. Valore di default: `qa`.

Esempio: `mvn test -Denv=qa`.

L'ambiente viene scelto al primo caricamento di `ConfigReader`. Al momento è
versionato soltanto `config-qa.properties`: per aggiungere un ambiente serve il
corrispondente file nel classpath dei test.

Per le proprietà applicative, la precedenza è: proprietà JVM `-Dchiave=valore`,
poi file dell'ambiente, poi eventuale default per i metodi che lo prevedono.
Un valore obbligatorio vuoto viene rifiutato. `validate()` controlla browser
supportato, timeout positivo, booleani e `base.url` HTTP/HTTPS assoluto.

Nel file QA, Chromium è il browser predefinito, headless è disabilitato e tutti
gli artefatti sono abilitati. La pipeline sovrascrive browser e headless.

## Esecuzione dei test

Maven Surefire esegue i test JUnit e la suite `RunCucumberTest`. Il runner seleziona
le risorse `features`, usa il glue `com.gianluca` e registra i plugin `pretty`,
`summary` e `AllureCucumber7Jvm`.

Esecuzione completa con ambiente QA predefinito:

```powershell
mvn test
```

Esecuzione dei soli test del framework:

```powershell
mvn "-Dtest=*Test,!RunCucumberTest" test
```

Esecuzione della suite E2E completa con override headless, come nel workflow CI:

```powershell
mvn "-Dtest=RunCucumberTest" "-Dbrowser.name=chromium" "-Dbrowser.headless=true" test
```

## Test dei componenti del framework

| Classe | Comportamenti verificati |
|---|---|
| `ConfigReaderTest` | Caricamento QA, file mancante, override JVM, valori vuoti, browser/timeout/booleani/URL non validi. |
| `ScenarioLifecycleTest` | Setup parziale, cleanup dopo errori di screenshot, video, attachment e chiusura, mantenimento dell'errore principale e pulizia dello stato. Usa proxy Java e sostituti dei manager senza avviare browser reali. |
| `JsonDataReaderTest` | Deserializzazione di un record e gestione di una risorsa JSON mancante. |

## Pipeline GitHub Actions

Il workflow `.github/workflows/ci.yml`, chiamato **Framework and E2E tests**,
definisce i trigger `push` sul branch `main`, `pull_request` verso `main` e
`workflow_dispatch` per l'esecuzione manuale. I permessi del workflow sono
limitati a `contents: read`.

| Job | Esecuzione | Timeout |
|---|---|---|
| `framework` | Ubuntu, Java 21, cache Maven; tutti i test Java eccetto `RunCucumberTest`. | 10 minuti |
| `e2e` | Parte solo se `framework` passa; compila, installa Chromium e dipendenze Linux, esegue l'intera suite `RunCucumberTest` senza filtri sui tag, con `browser.name=chromium` e `browser.headless=true`. | 20 minuti |

Ogni job tenta l'upload dei file disponibili anche dopo un fallimento, grazie a
`if: always()`. La conservazione è di 14 giorni:

- `framework-reports`: `target/surefire-reports/`;
- `e2e-diagnostics`: `target/surefire-reports/`, `target/allure-results/`,
  `target/artifacts/` e `target/logs/`.

Il workflow esegue la fase Maven `test` e conserva i risultati Allure come
artefatti. La generazione HTML è gestita separatamente dal plugin Maven Allure.

## Reporting, logging e artefatti

Artefatti e gestione reali implementati nel codice:

| Artefatto | Quando viene generato | Gestione/Utilizzo |
|---|---|---|
| Playwright Trace (zip) | `ScenarioLifecycle` delega avvio e arresto al manager, se `playwright.trace.enabled=true` | Salvato in `target/artifacts/traces/*.zip` tramite `PlaywrightArtifactManager`; allegato ad Allure tramite `AllureReportManager` |
| Video (webm) | Se `playwright.video.enabled=true`, Playwright registra il video del context | Salvato in `target/artifacts/videos/`; il path viene recuperato e allegato ad Allure |
| Screenshot (png) | Solo in caso di scenario fallito (nel `@After`) | Salvato in `target/artifacts/screenshots/*.png`; allegato ad Allure |
| Log di esecuzione (per scenario) | Generato da Log4j2 con Routing basato sullo scenario | File `target/logs/scenarios/<scenario>.log`; allegato ad Allure nel teardown |
| Allure Results | Risultati dell'adapter Cucumber durante i test | `target/allure-results/`, configurata in `src/test/resources/allure.properties` e letta dal plugin Maven |
| Surefire | Risultati dei test Java e della suite | `target/surefire-reports/` |

Trace e video sono raccolti anche per scenari riusciti, se abilitati; gli
screenshot sono richiesti solo per scenari falliti con pagina disponibile.
`ScenarioLifecycle` tenta separatamente raccolta, attachment e chiusure. Il log
dello scenario viene allegato prima di pulire il contesto di logging.

L'adapter `AllureCucumber7Jvm` produce risultati e attachment durante i test.
Nel `pom.xml`, il goal `allure:report` è associato alla fase `verify` e legge
`target/allure-results/`. Il report HTML viene generato in
`target/site/allure-maven-plugin/`.

## Scelte architetturali

- Separazione chiara tra Step Definitions e Page Objects: le step contengono le asserzioni e la logica di verifica, mentre le Page Object espongono metodi di interazione con l'interfaccia.
- Page Object Model completo per le pagine del dominio: `HomePage`, `ProductDetailPage`, `CategoryPage`, `ContactPage` e `PageManager` come access point centralizzato.
- PageManager fornisce accesso centralizzato ai Page Object e applica inizializzazione lazy (vedi `PageManager`).
- Condivisione dello stato dello scenario tramite `TestContext`, iniettato nelle step tramite PicoContainer (presenza della dipendenza `cucumber-picocontainer` e costruttori `TestContext` nelle step).
- Gli Hooks delegano a `ScenarioLifecycle`, che orchestra `PlaywrightFactory`, artefatti e reporting. Le risorse create vengono subito registrate nel `TestContext` per gestire setup parziali.
- Gestione degli artefatti separata: `PlaywrightArtifactManager` si occupa di creazione/salvataggio degli artefatti; `AllureReportManager` li allega al report.
- Logging tramite Log4j2 con Routing e `LoggerUtil` per gestire `ThreadContext`; il percorso del file dipende dal nome normalizzato dello scenario.
- `BaseSteps` centralizza l'accesso al `TestContext` e al `PageManager`, riducendo la duplicazione nelle step definitions.
- `ScenarioLifecycle.start` imposta `playwright.selectors().setTestIdAttribute("data-test")` per usare `getByTestId` sui componenti UI.
- La suite implementa una gestione centralizzata dei test data JSON attualmente utilizzata dagli scenari del modulo Contact (`JsonDataReader` + `ContactTestData`).

## Organizzazione delle feature Cucumber

Le feature sono organizzate per area funzionale sotto `features/e2e`:

- `Product Overview`: catalogo, informazioni prodotto e navigazione verso il dettaglio.
- `Product Detail`: dettaglio del prodotto e prodotti correlati.
- `Category`: navigazione per categoria e verifica della pagina/categoria dei prodotti.
- `Contact`: modulo contatti, validazione campi, subject, email, messaggio e invio positivo.

Le feature sono organizzate in `src/test/resources/features/e2e/*.feature` e sono eseguite tramite il runner `RunCucumberTest` con engine `cucumber-junit-platform-engine`.

## Riepilogo

Il framework attuale dimostra un'architettura BDD in Java con Playwright, organizzata per scalabilità e manutenzione. Il progetto include:

- Page Object Model con inizializzazione lazy di Page Object
- Gestione condivisa dello stato dello scenario via `TestContext`
- Dependency injection con PicoContainer
- Lifecycle del browser e dello scenario separato dagli hook, con gestione del cleanup dopo errori
- Centralizzazione di configurazione, logging e artefatti
- Allure integration per report e attachment
- Gestione centralizzata dei test data JSON per il modulo Contact tramite `JsonDataReader`
- Feature E2E per catalogo, dettaglio prodotto, categorie e modulo `Contact`
- Test del framework per configurazione, lifecycle e lettura JSON
- Pipeline GitHub Actions con suite E2E completa in headless e raccolta degli artefatti
- Un set di test Cucumber coerente con la struttura attuale del codice e delle risorse
