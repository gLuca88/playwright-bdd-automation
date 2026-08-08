package com.gianluca.factory;

import com.gianluca.config.ConfigReader;
import com.gianluca.factory.browser.ChromiumBrowserFactory;
import com.gianluca.factory.browser.FirefoxBrowserFactory;
import com.gianluca.factory.browser.IBrowserFactory;
import com.gianluca.factory.browser.WebkitBrowserFactory;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class PlaywrightFactory {

    private PlaywrightFactory() {
    }

    public static Playwright createPlaywright() {
        return Playwright.create();
    }

    public static Browser createBrowser(Playwright playwright) {

        if (playwright == null) {
            throw new IllegalArgumentException(
                    "Playwright non può essere null"
            );
        }

        IBrowserFactory browserFactory = getBrowserFactory();

        return browserFactory.createBrowser(playwright);
    }

    private static IBrowserFactory getBrowserFactory() {

        String browserName =
                ConfigReader.get("browser.name").toLowerCase();

        return switch (browserName) {
            case "chromium" -> new ChromiumBrowserFactory();
            case "firefox" -> new FirefoxBrowserFactory();
            case "webkit" -> new WebkitBrowserFactory();

            default -> throw new IllegalArgumentException(
                    "Browser non supportato: " + browserName
            );
        };
    }

    private static final Path VIDEO_DIRECTORY =
            Paths.get("target", "artifacts", "videos");

    public static BrowserContext createContext(Browser browser) {

        if (browser == null) {
            throw new IllegalArgumentException(
                    "Browser non può essere null"
            );
        }

        Browser.NewContextOptions options =
                new Browser.NewContextOptions();

        if (ConfigReader.getBoolean("playwright.video.enabled")) {

            options.setRecordVideoDir(VIDEO_DIRECTORY);
        }

        BrowserContext context =
                browser.newContext(options);

        context.setDefaultTimeout(
                ConfigReader.getInt("browser.timeout")
        );

        return context;
    }

    public static Page createPage(BrowserContext context) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "BrowserContext non può essere null"
            );
        }

        return context.newPage();
    }

    public static void closeContext(BrowserContext context) {

        if (context != null) {
            context.close();
        }
    }

    public static void closeBrowser(Browser browser) {

        if (browser != null) {
            browser.close();
        }
    }

    public static void closePlaywright(Playwright playwright) {

        if (playwright != null) {
            playwright.close();
        }
    }
}

