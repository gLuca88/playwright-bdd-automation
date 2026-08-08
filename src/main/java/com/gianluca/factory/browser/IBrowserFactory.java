package com.gianluca.factory.browser;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;

public interface IBrowserFactory {

    Browser createBrowser(Playwright playwright);
}

