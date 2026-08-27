package com.gianluca.steps;

import com.gianluca.context.TestContext;
import com.gianluca.pages.PageManager;


public abstract class BaseSteps {

    private final TestContext testContext;

    protected BaseSteps(TestContext testContext) {

        this.testContext = testContext;
    }


    protected PageManager pages() {
        return testContext.getPageManager();
    }


}
