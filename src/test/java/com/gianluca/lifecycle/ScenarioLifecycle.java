package com.gianluca.lifecycle;

import com.gianluca.artifact.IArtifactManager;
import com.gianluca.artifact.PlaywrightArtifactManager;
import com.gianluca.config.ConfigReader;
import com.gianluca.context.TestContext;
import com.gianluca.factory.PlaywrightFactory;
import com.gianluca.logging.LoggerUtil;
import com.gianluca.pages.PageManager;
import com.gianluca.report.AllureReportManager;
import com.gianluca.report.IReportManager;
import com.microsoft.playwright.*;
import org.apache.logging.log4j.Logger;

public class ScenarioLifecycle {
    private static final Logger logger = LoggerUtil.getLogger(ScenarioLifecycle.class);
    private final TestContext context;
    private final Resources resources;
    private final IArtifactManager artifacts;
    private final IReportManager reports;
    private boolean traceStarted;

    public ScenarioLifecycle(TestContext context) {
        this(context, new Resources(), new PlaywrightArtifactManager(), new AllureReportManager());
    }

    ScenarioLifecycle(TestContext context, Resources resources,
                      IArtifactManager artifacts, IReportManager reports) {
        this.context = context;
        this.resources = resources;
        this.artifacts = artifacts;
        this.reports = reports;
    }

    public void start(String name) {
        LoggerUtil.setScenario(name);
        logger.info("Avvio scenario: {}", name);
        try {
            ConfigReader.validate();
            context.setPlaywright(resources.playwright());
            context.getPlaywright().selectors().setTestIdAttribute("data-test");
            context.setBrowser(resources.browser(context.getPlaywright()));
            context.setBrowserContext(resources.browserContext(context.getBrowser()));
            context.setPage(resources.page(context.getBrowserContext()));
            context.setPageManager(new PageManager(context.getPage()));
            artifacts.startTrace(context.getBrowserContext());
            traceStarted = true;
        } catch (RuntimeException | Error failure) {
            try {
                finish(name, true);
            } catch (RuntimeException | Error cleanupFailure) {
                if (cleanupFailure != failure) failure.addSuppressed(cleanupFailure);
            }
            throw failure;
        }
    }

    public void finish(String name, boolean scenarioFailed) {
        if (context.getPlaywright() == null && context.getBrowser() == null
                && context.getBrowserContext() == null && context.getPage() == null) {
            context.clear();
            LoggerUtil.clearScenario();
            return;
        }
        Failures failures = new Failures();
        Page page = context.getPage();
        BrowserContext browserContext = context.getBrowserContext();
        try {
            if (scenarioFailed && page != null) {
                failures.attempt(() -> reports.attachScreenshot(artifacts.takeScreenshot(page, name)));
            }
            if (traceStarted && browserContext != null) {
                failures.attempt(() -> reports.attachTrace(artifacts.stopTrace(browserContext, name)));
            }
            Video[] video = new Video[1];
            if (page != null) failures.attempt(() -> video[0] = artifacts.getVideo(page));
            boolean contextClosed = failures.attempt(() -> PlaywrightFactory.closeContext(browserContext));
            // A video may not be finalized after a failed close: do not block on its path.
            if (contextClosed && video[0] != null) {
                failures.attempt(() -> reports.attachVideo(artifacts.getVideoPath(video[0])));
            }
            failures.attempt(() -> PlaywrightFactory.closeBrowser(context.getBrowser()));
            failures.attempt(() -> PlaywrightFactory.closePlaywright(context.getPlaywright()));
            logger.info("Fine scenario: {} - esito: {}", name,
                    scenarioFailed || failures.first != null ? "FAILED" : "PASSED");
            failures.attempt(() -> reports.attachLog(LoggerUtil.getScenarioLogPath()));
        } finally {
            traceStarted = false;
            context.clear();
            LoggerUtil.clearScenario();
        }
        if (failures.first != null) {
            if (scenarioFailed) {
                // Cucumber owns the original failure; diagnostics remain secondary.
                logger.error("Errori di teardown per lo scenario: {}", name, failures.first);
            } else {
                failures.rethrow();
            }
        }
    }

    static class Resources {
        Playwright playwright() { return PlaywrightFactory.createPlaywright(); }
        Browser browser(Playwright playwright) { return PlaywrightFactory.createBrowser(playwright); }
        BrowserContext browserContext(Browser browser) { return PlaywrightFactory.createContext(browser); }
        Page page(BrowserContext context) { return PlaywrightFactory.createPage(context); }
    }

    private static class Failures {
        private Throwable first;

        boolean attempt(Runnable operation) {
            try {
                operation.run();
                return true;
            } catch (RuntimeException | Error failure) {
                if (first == null) first = failure;
                else if (first != failure) first.addSuppressed(failure);
                logger.error("Operazione di teardown fallita", failure);
                return false;
            }
        }

        void rethrow() {
            if (first instanceof Error error) throw error;
            throw (RuntimeException) first;
        }
    }
}
