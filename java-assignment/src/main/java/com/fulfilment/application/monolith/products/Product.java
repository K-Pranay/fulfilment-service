package com.fulfilment.application.monolith.products;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "product")
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(length = 40, unique = true)
  public String name;

  @Column(nullable = true)
  public String description;

  @Column(precision = 10, scale = 2, nullable = true)
  public BigDecimal price;

  public int stock;

  public Product() {}

  public Product(String name) {
    this.name = name;
  }
}
