package com.fulfilment.application.monolith.fulfillment;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FulfillmentResourceTest {

  @LocalServerPort private int port;

  @BeforeEach
  public void setUp() {
    RestAssured.port = port;
  }

  @Test
  public void testFulfillmentAssociationCreationAndConstraints() {
    var req = new FulfillmentResource.FulfillmentRequest();
    req.storeId = 1L;
    req.productId = 1L;
    req.warehouseBusinessUnitCode = "MWH.001";

    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/fulfillment")
        .then()
        .statusCode(201)
        .body("storeId", is(1))
        .body("productId", is(1))
        .body("warehouseBusinessUnitCode", is("MWH.001"));

    req.warehouseBusinessUnitCode = "MWH.012";
    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/fulfillment")
        .then()
        .statusCode(201);

    req.warehouseBusinessUnitCode = "MWH.023";
    given()
        .contentType(ContentType.JSON)
        .body(req)
        .when()
        .post("/fulfillment")
        .then()
        .statusCode(400);

    given()
        .queryParam("storeId", 1)
        .queryParam("productId", 1)
        .queryParam("warehouseBusinessUnitCode", "MWH.012")
        .when()
        .delete("/fulfillment")
        .then()
        .statusCode(204);
  }
}
