package com.mastercard.codetest.jerseystore.controller;

import com.jayway.restassured.RestAssured;
import com.mastercard.codetest.jerseystore.JerseyStoreApplication;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

public class JerseyControllerTest extends RestAssuredTest{

    static final String ID = "67a62edb-61f2-4cef-87c9-89f40f98df7c";
    static final int SIZE = 76;

    @Test
    public void can_get_jersey_by_id() {
        when().
                get("/rest/api/v1/jersey/{id}", ID).
        then().
                statusCode(200).
                body("id", equalTo(ID));
    }

    @Test
    public void can_get_all_jerseys() {
        when().
                get("/rest/api/v1/jersey").
        then().
                statusCode(200).
                body("size()", is(SIZE));
    }
}
