package com.gianluca.hooks;

import com.gianluca.artifact.IArtifactManager;
import com.gianluca.artifact.PlaywrightArtifactManager;
import com.gianluca.context.TestContext;
import com.gianluca.factory.PlaywrightFactory;
import com.gianluca.logging.LoggerUtil;
import com.gianluca.report.AllureReportManager;
import com.gianluca.report.IReportManager;
import com.microsoft.playwright.*;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class Hooks {

    private static final Logger logger =
            LoggerUtil.getLogger(Hooks.class);

    private final TestContext testContext;
    private final IArtifactManager artifactManager;
    private final IReportManager reportManager;

    public Hooks(TestContext testContext) {

        this.testContext = testContext;
        this.artifactManager = new PlaywrightArtifactManager();
        this.reportManager = new AllureReportManager();
    }


    @Before
    public void beforeScenario(Scenario scenario) {

        /*
         * Associa lo scenario corrente al ThreadContext.
         * Deve essere fatto prima di qualsiasi log.
         */
        LoggerUtil.setScenario(
                scenario.getName()
        );

        logger.info(
                "Avvio scenario: {}",
                scenario.getName()
        );

        Playwright playwright =
                PlaywrightFactory.createPlaywright();
        playwright.selectors().setTestIdAttribute("data-test");

        Browser browser =
                PlaywrightFactory.createBrowser(playwright);

        BrowserContext browserContext =
                PlaywrightFactory.createContext(browser);

        Page page =
                PlaywrightFactory.createPage(browserContext);

        testContext.setPlaywright(playwright);
        testContext.setBrowser(browser);
        testContext.setBrowserContext(browserContext);
        testContext.setPage(page);

        artifactManager.startTrace(
                browserContext
        );

        logger.info(
                "Risorse Playwright inizializzate per lo scenario: {}",
                scenario.getName()
        );
    }


    @After
    public void afterScenario(Scenario scenario) {

        String scenarioName =
                scenario.getName();

        Page page =
                testContext.getPage();

        BrowserContext browserContext =
                testContext.getBrowserContext();

        Video video = null;

        logger.info(
                "Avvio teardown dello scenario: {}",
                scenarioName
        );

        try {

            /*
             * Screenshot solo in caso di fallimento.
             */
            if (scenario.isFailed()) {

                logger.error(
                        "Scenario fallito: {}",
                        scenarioName
                );

                Path screenshotPath =
                        artifactManager.takeScreenshot(
                                page,
                                scenarioName
                        );

                reportManager.attachScreenshot(
                        screenshotPath
                );

                logger.info(
                        "Screenshot acquisito e allegato al report per lo scenario: {}",
                        scenarioName
                );
            }

            /*
             * Stop trace e attachment ad Allure.
             */
            Path tracePath =
                    artifactManager.stopTrace(
                            browserContext,
                            scenarioName
                    );

            reportManager.attachTrace(
                    tracePath
            );

            logger.info(
                    "Trace Playwright salvato e allegato al report per lo scenario: {}",
                    scenarioName
            );

            /*
             * Recuperiamo il Video prima
             * della chiusura del BrowserContext.
             */
            video =
                    artifactManager.getVideo(page);

        } finally {

            /*
             * La chiusura del BrowserContext
             * finalizza il video.
             */
            PlaywrightFactory.closeContext(
                    browserContext
            );

            /*
             * Ora possiamo recuperare il Path
             * del video finalizzato.
             */
            Path videoPath =
                    artifactManager.getVideoPath(
                            video
                    );

            reportManager.attachVideo(
                    videoPath
            );

            logger.info(
                    "Video Playwright salvato e allegato al report per lo scenario: {}",
                    scenarioName
            );

            /*
             * Chiusura delle restanti risorse Playwright.
             */
            PlaywrightFactory.closeBrowser(
                    testContext.getBrowser()
            );

            PlaywrightFactory.closePlaywright(
                    testContext.getPlaywright()
            );

            testContext.clear();

            /*
             * Log finale dello scenario.
             */
            if (scenario.isFailed()) {

                logger.error(
                        "Fine scenario con esito FAILED: {}",
                        scenarioName
                );

            } else {

                logger.info(
                        "Fine scenario con esito PASSED: {}",
                        scenarioName
                );
            }

            /*
             * Recuperiamo il file di log relativo
             * esclusivamente allo scenario corrente.
             */
            Path logPath =
                    LoggerUtil.getScenarioLogPath();

            /*
             * Attachment del log ad Allure.
             */
            reportManager.attachLog(
                    logPath
            );

            /*
             * Il ThreadContext viene pulito solo
             * dopo aver recuperato e allegato il log.
             */
            LoggerUtil.clearScenario();
        }
    }
}

