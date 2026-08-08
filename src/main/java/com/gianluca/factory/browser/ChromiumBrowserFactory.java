package com.gianluca.factory.browser;

import com.gianluca.config.ConfigReader;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

public class ChromiumBrowserFactory implements IBrowserFactory {

    @Override
    public Browser createBrowser(Playwright playwright) {

        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(ConfigReader.getBoolean("browser.headless"));


        return playwright.chromium().launch(options);
    }
}

