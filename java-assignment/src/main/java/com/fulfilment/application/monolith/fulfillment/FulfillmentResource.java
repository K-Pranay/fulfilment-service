package com.fulfilment.application.monolith.fulfillment;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/fulfillment")
public class FulfillmentResource {

  @Autowired private FulfillmentAssociationService service;

  public static class FulfillmentRequest {
    public Long storeId;
    public Long productId;
    public String warehouseBusinessUnitCode;
  }

  @GetMapping
  public List<FulfillmentAssociation> listAll() {
    return service.listAll();
  }

  @GetMapping("/store/{storeId}")
  public List<FulfillmentAssociation> listByStore(@PathVariable("storeId") Long storeId) {
    return service.listByStore(storeId);
  }

  @PostMapping
  public ResponseEntity<FulfillmentAssociation> create(
      @RequestBody FulfillmentRequest request) {
    if (request == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body cannot be empty");
    }
    try {
      FulfillmentAssociation assoc =
          service.associate(
              request.storeId, request.productId, request.warehouseBusinessUnitCode);
      return ResponseEntity.status(HttpStatus.CREATED).body(assoc);
    } catch (IllegalArgumentException e) {
      if (e.getMessage().contains("not found")) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
      }
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @DeleteMapping
  public ResponseEntity<Void> delete(
      @RequestParam("storeId") Long storeId,
      @RequestParam("productId") Long productId,
      @RequestParam("warehouseBusinessUnitCode") String warehouseBusinessUnitCode) {
    if (storeId == null || productId == null || warehouseBusinessUnitCode == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Query params storeId, productId, and warehouseBusinessUnitCode are required");
    }
    service.disassociate(storeId, productId, warehouseBusinessUnitCode);
    return ResponseEntity.noContent().build();
  }
}
