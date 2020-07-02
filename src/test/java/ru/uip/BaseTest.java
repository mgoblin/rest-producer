package ru.uip;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "server.port=0"
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public abstract class BaseTest {

    @LocalServerPort
    int localPort;


    @BeforeEach
    public void setUp(
            TestInfo testInfo,
            RestDocumentationContextProvider restDocumentation) {

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = localPort;

        final RequestSpecification idx = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(localPort)
                .addFilter(documentationConfiguration(restDocumentation))
                .build();

        RestAssured.requestSpecification =
                idx.filter(document("contract/" + testInfo.getTestMethod().orElseThrow().getName()));
    }

    @AfterEach
    public void tearDown() {
        RestAssured.reset();
    }

}
