package com.gianluca.hooks;

import com.gianluca.context.TestContext;
import com.gianluca.lifecycle.ScenarioLifecycle;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Hooks {
    private final ScenarioLifecycle lifecycle;

    public Hooks(TestContext context) {
        lifecycle = new ScenarioLifecycle(context);
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        lifecycle.start(scenario.getName());
    }

    @After
    public void afterScenario(Scenario scenario) {
        lifecycle.finish(scenario.getName(), scenario.isFailed());
    }
}
