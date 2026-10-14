package com.gianluca.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class ConfigReaderTest {
    @Test
    void loadsQaAndReportsMissingEnvironment() {
        assertNotNull(ConfigReader.loadProperties("qa").getProperty("base.url"));
        var failure = assertThrows(IllegalStateException.class,
                () -> ConfigReader.loadProperties("missing-test-environment"));
        assertTrue(failure.getMessage().contains("config-missing-test-environment.properties"));
    }

    @Test
    void jvmOverrideTakesPrecedenceAndIsTrimmed() {
        withProperty("browser.headless", " true ",
                () -> assertTrue(ConfigReader.getBoolean("browser.headless")));
        withProperty("test.config.optional", "override",
                () -> assertEquals("override", ConfigReader.get("test.config.optional", "default")));
    }

    @Test
    void blankRequiredValueIsRejected() {
        withProperty("base.url", " ", () -> assertThrows(IllegalArgumentException.class,
                () -> ConfigReader.get("base.url")));
    }

    @ParameterizedTest
    @CsvSource({"browser.name,safari", "browser.timeout,0", "browser.timeout,-1",
            "browser.timeout,abc", "browser.headless,tru", "playwright.video.enabled,yes",
            "playwright.trace.enabled,yes", "playwright.screenshot.enabled,yes",
            "base.url,/relative", "base.url,ftp://example.com",
            "api.base.url,/relative", "api.base.url,ftp://example.com",
            "api.base.url,http://", "api.base.url,http://bad host"})
    void rejectsInvalidConfiguration(String key, String value) {
        withProperty(key, value, () -> assertThrows(IllegalArgumentException.class, ConfigReader::validate));
    }

    @Test
    void acceptsValidOverrides() {
        withProperty("browser.name", "FIREFOX", ConfigReader::validate);
        withProperty("api.base.url", "http://localhost:8091", ConfigReader::validate);
    }

    @Test
    void ciConfigurationUsesLocalApplicationAndHeadlessChromium() {
        var ci = ConfigReader.loadProperties("ci");
        assertAll(
                () -> assertEquals("http://localhost:4200", ci.getProperty("base.url")),
                () -> assertEquals("http://localhost:8091", ci.getProperty("api.base.url")),
                () -> assertEquals("chromium", ci.getProperty("browser.name")),
                () -> assertEquals("true", ci.getProperty("browser.headless"))
        );
    }

    @Test
    void qaConfigurationKeepsPublicApplicationAndApi() {
        var qa = ConfigReader.loadProperties("qa");
        assertEquals("https://practicesoftwaretesting.com", qa.getProperty("base.url"));
        assertEquals("https://api.practicesoftwaretesting.com", qa.getProperty("api.base.url"));
    }

    @Test
    void rejectsBlankApiUrlWithPropertyName() {
        withProperty("api.base.url", " ", () -> {
            var failure = assertThrows(IllegalArgumentException.class, ConfigReader::validate);
            assertTrue(failure.getMessage().contains("api.base.url"));
        });
    }

    private void withProperty(String key, String value, Runnable assertion) {
        String previous = System.getProperty(key);
        try {
            System.setProperty(key, value);
            assertion.run();
        } finally {
            if (previous == null) System.clearProperty(key);
            else System.setProperty(key, previous);
        }
    }
}
