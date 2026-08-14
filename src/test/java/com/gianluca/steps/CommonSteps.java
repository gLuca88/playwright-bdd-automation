package com.gianluca.steps;

import com.gianluca.context.TestContext;
import com.gianluca.logging.LoggerUtil;
import com.gianluca.pages.HomePage;
import io.cucumber.java.en.Given;
import org.apache.logging.log4j.Logger;


public class CommonSteps extends BaseSteps {

    private static final Logger logger =
            LoggerUtil.getLogger(CommonSteps.class);

    public CommonSteps(TestContext testContext) {

        super(testContext);
    }

    @Given("il visitatore si trova nella home page")
    public void openHomePage() {

        HomePage homePage = new HomePage(page());

        logger.info("Navigazione alla home page");

        homePage.openHomePage();

        logger.info("Home page caricata");
    }


}


