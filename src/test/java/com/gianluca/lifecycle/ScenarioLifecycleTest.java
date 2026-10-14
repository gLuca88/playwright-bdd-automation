package com.gianluca.lifecycle;

import com.gianluca.artifact.IArtifactManager;
import com.gianluca.context.TestContext;
import com.gianluca.report.IReportManager;
import com.microsoft.playwright.*;
import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class ScenarioLifecycleTest {
    private final TestContext context = new TestContext();
    private final List<String> events = new ArrayList<>();
    private final RuntimeException original = new IllegalStateException("original failure");
    private String failingCreation;
    private String failingClose;
    private String failingAttachment;
    private boolean failScreenshot;
    private boolean failVideoPath;

    private final Selectors selectors = proxy(Selectors.class, method -> null);
    private final Playwright playwright = proxy(Playwright.class,
            method -> method.equals("selectors") ? selectors : close("playwright", method));
    private final Browser browser = proxy(Browser.class, method -> close("browser", method));
    private final BrowserContext browserContext = proxy(BrowserContext.class, method -> close("context", method));
    private final Page page = proxy(Page.class, method -> null);
    private final Video video = proxy(Video.class, method -> null);

    private final ScenarioLifecycle.Resources resources = new ScenarioLifecycle.Resources() {
        @Override Playwright playwright() { return create("playwright", playwright); }
        @Override Browser browser(Playwright p) { return create("browser", browser); }
        @Override BrowserContext browserContext(Browser b) { return create("context", browserContext); }
        @Override Page page(BrowserContext c) { return create("page", page); }
    };

    private final IArtifactManager artifacts = new IArtifactManager() {
        public void startTrace(BrowserContext c) { events.add("trace.start"); }
        public Path stopTrace(BrowserContext c, String n) { events.add("trace.stop"); return Path.of("trace.zip"); }
        public Path takeScreenshot(Page p, String n) {
            assertNotNull(p);
            events.add("screenshot");
            if (failScreenshot) throw original;
            return Path.of("screenshot.png");
        }
        public Video getVideo(Page p) { events.add("video.get"); return video; }
        public Path getVideoPath(Video v) {
            assertTrue(events.contains("context.close"));
            events.add("video.path");
            if (failVideoPath) throw original;
            return Path.of("video.webm");
        }
    };

    private final IReportManager reports = new IReportManager() {
        public void attachScreenshot(Path p) { attach("screenshot"); }
        public void attachTrace(Path p) { attach("trace"); }
        public void attachVideo(Path p) { attach("video"); }
        public void attachLog(Path p) { attach("log"); }
    };

    @Test
    void browserCreationFailureClosesPlaywrightAndPreservesError() {
        failingCreation = "browser";
        var lifecycle = lifecycle();
        assertSame(original, assertThrows(RuntimeException.class, () -> lifecycle.start("browser failure")));
        assertTrue(events.contains("playwright.close"));
        assertFalse(events.contains("screenshot"));
        assertCleared();
        assertDoesNotThrow(() -> lifecycle.finish("browser failure", true));
        assertEquals(1, events.stream().filter("playwright.close"::equals).count());
    }

    @Test
    void pageCreationFailureClosesEveryCreatedResource() {
        failingCreation = "page";
        assertSame(original, assertThrows(RuntimeException.class, () -> lifecycle().start("page failure")));
        assertTrue(events.containsAll(List.of("context.close", "browser.close", "playwright.close")));
        assertCleared();
    }

    @Test
    void screenshotFailureDoesNotInterruptTraceVideoOrCleanup() {
        var lifecycle = lifecycle();
        lifecycle.start("screenshot failure");
        failScreenshot = true;
        assertDoesNotThrow(() -> lifecycle.finish("screenshot failure", true));
        assertTrue(events.containsAll(List.of("trace.stop", "video.path", "browser.close", "playwright.close", "attach.log")));
        assertCleared();
    }

    @ParameterizedTest
    @ValueSource(strings = {"trace", "video", "log"})
    void attachmentFailureFailsOtherwisePassingScenarioAfterCleanup(String attachment) {
        var lifecycle = lifecycle();
        lifecycle.start("attachment failure");
        failingAttachment = attachment;
        assertSame(original, assertThrows(RuntimeException.class, () -> lifecycle.finish("attachment failure", false)));
        assertTrue(events.containsAll(List.of("browser.close", "playwright.close", "attach.log")));
        assertCleared();
    }

    @Test
    void contextCloseFailureSkipsVideoPathButClosesRemainingResources() {
        var lifecycle = lifecycle();
        lifecycle.start("close failure");
        failingClose = "context";
        failingAttachment = "log";
        var failure = assertThrows(RuntimeException.class, () -> lifecycle.finish("close failure", false));
        assertEquals("context close failed", failure.getMessage());
        assertSame(original, failure.getSuppressed()[0]);
        assertFalse(events.contains("video.path"));
        assertTrue(events.containsAll(List.of("browser.close", "playwright.close")));
        assertCleared();
    }

    @Test
    void videoPathFailureStillClosesBrowserAndPlaywright() {
        var lifecycle = lifecycle();
        lifecycle.start("video failure");
        failVideoPath = true;
        assertSame(original, assertThrows(RuntimeException.class, () -> lifecycle.finish("video failure", false)));
        assertTrue(events.containsAll(List.of("browser.close", "playwright.close")));
        assertCleared();
    }

    @Test
    void successfulRunFinalizesVideoAndClearsState() {
        var lifecycle = lifecycle();
        lifecycle.start("success");
        lifecycle.finish("success", false);
        assertFalse(events.contains("screenshot"));
        assertTrue(events.containsAll(List.of("trace.stop", "video.path", "attach.video", "attach.log")));
        assertCleared();
    }

    @Test
    void invalidConfigurationDoesNotCreateResources() {
        String previous = System.getProperty("browser.timeout");
        try {
            System.setProperty("browser.timeout", "0");
            assertThrows(IllegalArgumentException.class, () -> lifecycle().start("invalid config"));
            assertTrue(events.isEmpty());
            assertCleared();
        } finally {
            if (previous == null) System.clearProperty("browser.timeout");
            else System.setProperty("browser.timeout", previous);
        }
    }

    @Test
    void browserCloseFailureStillClosesPlaywright() {
        var lifecycle = lifecycle();
        lifecycle.start("browser close failure");
        failingClose = "browser";
        assertThrows(RuntimeException.class, () -> lifecycle.finish("browser close failure", false));
        assertTrue(events.contains("playwright.close"));
        assertCleared();
    }

    private ScenarioLifecycle lifecycle() { return new ScenarioLifecycle(context, resources, artifacts, reports); }

    private <T> T create(String name, T value) {
        if (name.equals(failingCreation)) throw original;
        return value;
    }

    private Object close(String name, String method) {
        if (method.equals("close")) {
            events.add(name + ".close");
            if (name.equals(failingClose)) throw new IllegalStateException(name + " close failed");
        }
        return null;
    }

    private void attach(String name) {
        events.add("attach." + name);
        if (name.equals(failingAttachment)) throw original;
    }

    private void assertCleared() {
        assertAll(() -> assertNull(context.getPlaywright()), () -> assertNull(context.getBrowser()),
                () -> assertNull(context.getBrowserContext()), () -> assertNull(context.getPage()),
                () -> assertNull(context.getPageManager()), () -> assertNull(ThreadContext.get("scenario")));
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> type, Function<String, Object> handler) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (instance, method, args) -> handler.apply(method.getName()));
    }
}
