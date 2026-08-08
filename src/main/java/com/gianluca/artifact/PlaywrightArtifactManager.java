package com.gianluca.artifact;

import com.gianluca.config.ConfigReader;
import com.microsoft.playwright.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class PlaywrightArtifactManager implements IArtifactManager {

    private static final Path TRACE_DIRECTORY =
            Paths.get("target", "artifacts", "traces");

    private static final Path SCREENSHOT_DIRECTORY =
            Paths.get("target", "artifacts", "screenshots");

    @Override
    public void startTrace(BrowserContext context) {

        if (!ConfigReader.getBoolean("playwright.trace.enabled")) {
            return;
        }

        context.tracing().start(
                new Tracing.StartOptions()
                        .setScreenshots(true)
                        .setSnapshots(true)
                        .setSources(true)
        );
    }

    @Override
    public Path stopTrace(BrowserContext context, String scenarioName) {

        if (!ConfigReader.getBoolean("playwright.trace.enabled")) {
            return null;
        }

        createDirectory(TRACE_DIRECTORY);

        Path tracePath = TRACE_DIRECTORY.resolve(
                createArtifactName(scenarioName, ".zip")
        );

        context.tracing().stop(
                new Tracing.StopOptions()
                        .setPath(tracePath)
        );

        return tracePath;
    }

    @Override
    public Path takeScreenshot(Page page, String scenarioName) {

        if (!ConfigReader.getBoolean("playwright.screenshot.enabled")) {
            return null;
        }

        createDirectory(SCREENSHOT_DIRECTORY);

        Path screenshotPath = SCREENSHOT_DIRECTORY.resolve(
                createArtifactName(scenarioName, ".png")
        );

        page.screenshot(
                new Page.ScreenshotOptions()
                        .setPath(screenshotPath)
                        .setFullPage(true)
        );

        return screenshotPath;
    }

    private String createArtifactName(
            String scenarioName,
            String extension) {

        String sanitizedName = scenarioName
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_|_$", "");

        String uniqueId = UUID.randomUUID()
                .toString()
                .substring(0, 8);

        return sanitizedName + "_" + uniqueId + extension;
    }

    private void createDirectory(Path directory) {

        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Impossibile creare la directory: " + directory,
                    e
            );
        }
    }


    @Override
    public Video getVideo(Page page) {

        if (!ConfigReader.getBoolean("playwright.video.enabled")) {
            return null;
        }

        return page.video();
    }

    @Override
    public Path getVideoPath(Video video) {

        if (video == null) {
            return null;
        }

        return video.path();
    }

}


