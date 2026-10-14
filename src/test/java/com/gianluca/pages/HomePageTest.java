package com.gianluca.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class HomePageTest {
    @ParameterizedTest
    @CsvSource({
            "http://localhost:8091,http://localhost:8091/products,200,true",
            "http://localhost:8091/,http://localhost:8091/products?page=1,200,true",
            "http://localhost:8091,https://api.practicesoftwaretesting.com/products,200,false",
            "http://localhost:8091,http://localhost:8091/products,500,false",
            "http://localhost:8091,http://localhost:8091/products/other,200,false"
    })
    @SuppressWarnings("unchecked")
    void homeWaitsForConfiguredProductsApi(String apiUrl, String responseUrl, int status, boolean matches) {
        String previous = System.getProperty("api.base.url");
        try {
            System.setProperty("api.base.url", apiUrl);
            AtomicReference<Predicate<Response>> responsePredicate = new AtomicReference<>();
            AtomicReference<String> navigation = new AtomicReference<>();
            Page page = (Page) Proxy.newProxyInstance(Page.class.getClassLoader(), new Class<?>[]{Page.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("waitForResponse")) {
                            responsePredicate.set((Predicate<Response>) args[0]);
                            ((Runnable) args[1]).run();
                        } else if (method.getName().equals("navigate")) {
                            navigation.set((String) args[0]);
                        }
                        return null;
                    });
            Response response = (Response) Proxy.newProxyInstance(Response.class.getClassLoader(),
                    new Class<?>[]{Response.class}, (proxy, method, args) -> switch (method.getName()) {
                        case "url" -> responseUrl;
                        case "status" -> status;
                        default -> null;
                    });

            new HomePage(page).openHomePage();

            assertNotNull(navigation.get());
            assertNotNull(responsePredicate.get());
            assertEquals(matches, responsePredicate.get().test(response));
        } finally {
            if (previous == null) System.clearProperty("api.base.url");
            else System.setProperty("api.base.url", previous);
        }
    }
}
