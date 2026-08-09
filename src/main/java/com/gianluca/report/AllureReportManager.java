package com.gianluca.report;


import io.qameta.allure.Allure;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
public class AllureReportManager implements IReportManager {

    @Override
    public void attachScreenshot(Path screenshotPath) {

        attach(
                "Screenshot",
                "image/png",
                screenshotPath
        );
    }

    @Override
    public void attachTrace(Path tracePath) {

        attach(
                "Playwright Trace",
                "application/zip",
                tracePath
        );
    }

    @Override
    public void attachVideo(Path videoPath) {

        attach(
                "Playwright Video",
                "video/webm",
                videoPath
        );
    }

    private void attach(
            String name,
            String contentType,
            Path path) {

        if (path == null || !Files.exists(path)) {
            return;
        }

        try (InputStream inputStream = Files.newInputStream(path)) {

            Allure.addAttachment(
                    name,
                    contentType,
                    inputStream,
                    getExtension(path)
            );

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Impossibile allegare il file al report Allure: " + path,
                    e
            );
        }
    }

    private String getExtension(Path path) {

        String fileName = path
                .getFileName()
                .toString();

        int index = fileName.lastIndexOf('.');

        if (index == -1) {
            return "";
        }

        return fileName.substring(index);
    }
}