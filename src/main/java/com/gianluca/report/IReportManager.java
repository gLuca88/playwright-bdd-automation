package com.gianluca.report;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;

import java.nio.file.Path;

public interface IReportManager {

    void attachScreenshot(Path screenshotPath);

    void attachTrace(Path tracePath);

    void attachVideo(Path videoPath);

    void attachLog(Path logPath);
}