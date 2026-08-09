package com.gianluca.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class LoggerUtil {

    private static final String SCENARIO_KEY = "scenario";

    private static final Path SCENARIO_LOG_DIR =
            Paths.get("target", "logs", "scenarios");

    private LoggerUtil() {
    }

    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }

    public static void setScenario(String scenarioName) {

        String normalizedScenarioName =
                scenarioName
                        .toLowerCase()
                        .replaceAll("[^a-z0-9]+", "_")
                        .replaceAll("^_|_$", "");

        ThreadContext.put(
                SCENARIO_KEY,
                normalizedScenarioName
        );
    }

    public static Path getScenarioLogPath() {

        String scenario =
                ThreadContext.get(SCENARIO_KEY);

        if (scenario == null || scenario.isBlank()) {
            throw new IllegalStateException(
                    "Nessuno scenario presente nel ThreadContext"
            );
        }

        return SCENARIO_LOG_DIR.resolve(
                scenario + ".log"
        );
    }

    public static void clearScenario() {
        ThreadContext.remove(SCENARIO_KEY);
    }
}
