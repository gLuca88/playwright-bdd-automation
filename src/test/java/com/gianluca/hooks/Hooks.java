package com.gianluca.hooks;

import com.gianluca.artifact.IArtifactManager;
import com.gianluca.artifact.PlaywrightArtifactManager;
import com.gianluca.context.TestContext;
import com.gianluca.factory.PlaywrightFactory;
import com.gianluca.report.AllureReportManager;
import com.gianluca.report.IReportManager;
import com.microsoft.playwright.*;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;

import java.nio.file.Path;

public class Hooks {

    private final TestContext testContext;
    private final IArtifactManager artifactManager;
    private final IReportManager reportManager;

    public Hooks(TestContext testContext) {

        this.testContext = testContext;
        this.artifactManager = new PlaywrightArtifactManager();
        this.reportManager =
                new AllureReportManager();
    }

    @Before
    public void beforeScenario(Scenario scenario) {

        System.out.println(
                "Avvio scenario: " + scenario.getName()
        );

        Playwright playwright =
                PlaywrightFactory.createPlaywright();

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

        artifactManager.startTrace(browserContext);
    }

    @After
    public void afterScenario(Scenario scenario) {

        String scenarioName = scenario.getName();

        Page page = testContext.getPage();

        BrowserContext browserContext =
                testContext.getBrowserContext();

        Video video = null;

        try {

            /*
             * Screenshot solo in caso di fallimento
             */
            if (scenario.isFailed()) {

                System.out.println(
                        "Scenario fallito: " + scenarioName
                );

                Path screenshotPath =
                        artifactManager.takeScreenshot(
                                page,
                                scenarioName
                        );

                reportManager.attachScreenshot(
                        screenshotPath
                );
            }

            /*
             * Stop trace e attachment ad Allure
             */
            Path tracePath =
                    artifactManager.stopTrace(
                            browserContext,
                            scenarioName
                    );

            reportManager.attachTrace(
                    tracePath
            );

            /*
             * Recuperiamo l'oggetto Video prima
             * della chiusura del BrowserContext
             */
            video = artifactManager.getVideo(page);

        } finally {

            /*
             * La chiusura del BrowserContext
             * finalizza il file video
             */
            PlaywrightFactory.closeContext(
                    browserContext
            );

            /*
             * Dopo la chiusura del context possiamo
             * recuperare il Path del video
             */
            Path videoPath =
                    artifactManager.getVideoPath(video);

            /*
             * Attachment del video ad Allure
             */
            reportManager.attachVideo(
                    videoPath
            );

            PlaywrightFactory.closeBrowser(
                    testContext.getBrowser()
            );

            PlaywrightFactory.closePlaywright(
                    testContext.getPlaywright()
            );

            testContext.clear();

            System.out.println(
                    "Fine scenario: " + scenarioName
            );
        }
    }
}

