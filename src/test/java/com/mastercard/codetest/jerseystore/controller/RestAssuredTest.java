package com.mastercard.codetest.jerseystore.controller;

import com.jayway.restassured.RestAssured;
import com.mastercard.codetest.jerseystore.JerseyStoreApplication;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class RestAssuredTest {

    @LocalServerPort
    int randomServerPort;

    @Before
    public void setup() {
        RestAssured.port = randomServerPort;
    }


}
