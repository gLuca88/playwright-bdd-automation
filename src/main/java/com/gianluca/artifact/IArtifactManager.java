package com.gianluca.artifact;

import com.microsoft.playwright.*;

import java.nio.file.Path;

public interface IArtifactManager {

    void startTrace(BrowserContext context);

    Path stopTrace(BrowserContext context, String scenarioName);

    Path takeScreenshot(Page page, String scenarioName);

    Video getVideo(Page page);

    Path getVideoPath(Video video);
}



