package com.fulfilment.application.monolith.stores;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "store")
public class Store {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(length = 40, unique = true)
  public String name;

  public int quantityProductsInStock;

  public Store() {}

  public Store(String name, int quantityProductsInStock) {
    this.name = name;
    this.quantityProductsInStock = quantityProductsInStock;
  }
}
