package com.gianluca.hooks;

import com.gianluca.artifact.IArtifactManager;
import com.gianluca.artifact.PlaywrightArtifactManager;
import com.gianluca.context.TestContext;
import com.gianluca.factory.PlaywrightFactory;
import com.microsoft.playwright.*;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;

import java.nio.file.Path;

public class Hooks {

    private final TestContext testContext;
    private final IArtifactManager artifactManager;

    public Hooks(TestContext testContext) {

        this.testContext = testContext;
        this.artifactManager = new PlaywrightArtifactManager();
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

        Video video =
                artifactManager.getVideo(page);

        try {

            if (scenario.isFailed()) {

                System.out.println(
                        "Scenario fallito: " + scenarioName
                );

                artifactManager.takeScreenshot(
                        page,
                        scenarioName
                );
            }

            artifactManager.stopTrace(
                    browserContext,
                    scenarioName
            );

        } finally {

            PlaywrightFactory.closeContext(
                    browserContext
            );

            Path videoPath =
                    artifactManager.getVideoPath(video);

            if (videoPath != null) {

                System.out.println(
                        "Video salvato: " + videoPath
                );
            }

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

