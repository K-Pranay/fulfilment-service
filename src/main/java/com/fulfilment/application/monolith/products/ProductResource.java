package com.fulfilment.application.monolith.products;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/product")
public class ProductResource {

  @Autowired private ProductRepository productRepository;

  @GetMapping
  public List<Product> get() {
    return productRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
  }

  @GetMapping("/{id}")
  public Product getSingle(@PathVariable("id") Long id) {
    return productRepository
        .findById(id)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Product with id of " + id + " does not exist."));
  }

  @PostMapping
  @Transactional
  public ResponseEntity<Product> create(@RequestBody Product product) {
    if (product.id != null) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Id was invalidly set on request.");
    }

    Product savedProduct = productRepository.save(product);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
  }

  @PutMapping("/{id}")
  @Transactional
  public Product update(@PathVariable("id") Long id, @RequestBody Product product) {
    if (product.name == null) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Product Name was not set on request.");
    }

    Product entity =
        productRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product with id of " + id + " does not exist."));

    entity.name = product.name;
    entity.description = product.description;
    entity.price = product.price;
    entity.stock = product.stock;

    return productRepository.save(entity);
  }

  @DeleteMapping("/{id}")
  @Transactional
  public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
    Product entity =
        productRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product with id of " + id + " does not exist."));

    productRepository.delete(entity);
    return ResponseEntity.noContent().build();
  }
}
