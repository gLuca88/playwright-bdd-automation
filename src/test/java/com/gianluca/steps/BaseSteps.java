package com.gianluca.steps;

import com.gianluca.context.TestContext;
import com.microsoft.playwright.Page;

public abstract class BaseSteps {

    private final TestContext testContext;

    protected BaseSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    protected Page page() {
        return testContext.getPage();
    }

}
