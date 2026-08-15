package com.fulfilment.application.monolith.fulfillment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "fulfillment_association")
public class FulfillmentAssociation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(nullable = false)
  public Long storeId;

  @Column(nullable = false)
  public Long productId;

  @Column(nullable = false)
  public String warehouseBusinessUnitCode;

  public LocalDateTime createdAt;

  public FulfillmentAssociation() {}

  public FulfillmentAssociation(Long storeId, Long productId, String warehouseBusinessUnitCode) {
    this.storeId = storeId;
    this.productId = productId;
    this.warehouseBusinessUnitCode = warehouseBusinessUnitCode;
    this.createdAt = LocalDateTime.now();
  }
}
