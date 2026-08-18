package com.fulfilment.application.monolith.stores;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/store")
public class StoreResource {

  @Autowired private StoreRepository storeRepository;
  @Autowired private LegacyStoreManagerGateway legacyStoreManagerGateway;

  @GetMapping
  public List<Store> get() {
    return storeRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
  }

  @GetMapping("/{id}")
  public Store getSingle(@PathVariable("id") Long id) {
    return storeRepository
        .findById(id)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Store with id of " + id + " does not exist."));
  }

  @PostMapping
  @Transactional
  public ResponseEntity<Store> create(@RequestBody Store store) {
    if (store.id != null) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Id was invalidly set on request.");
    }

    Store savedStore = storeRepository.save(store);

    if (TransactionSynchronizationManager.isActualTransactionActive()) {
      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCommit() {
              legacyStoreManagerGateway.createStoreOnLegacySystem(savedStore);
            }
          });
    } else {
      legacyStoreManagerGateway.createStoreOnLegacySystem(savedStore);
    }

    return ResponseEntity.status(HttpStatus.CREATED).body(savedStore);
  }

  @PutMapping("/{id}")
  @Transactional
  public Store update(@PathVariable("id") Long id, @RequestBody Store updatedStore) {
    if (updatedStore.name == null) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Store Name was not set on request.");
    }

    Store entity =
        storeRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Store with id of " + id + " does not exist."));

    entity.name = updatedStore.name;
    entity.quantityProductsInStock = updatedStore.quantityProductsInStock;
    Store savedStore = storeRepository.save(entity);

    if (TransactionSynchronizationManager.isActualTransactionActive()) {
      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCommit() {
              legacyStoreManagerGateway.updateStoreOnLegacySystem(savedStore);
            }
          });
    } else {
      legacyStoreManagerGateway.updateStoreOnLegacySystem(savedStore);
    }

    return savedStore;
  }

  @PatchMapping("/{id}")
  @Transactional
  public Store patch(@PathVariable("id") Long id, @RequestBody Store updatedStore) {
    if (updatedStore.name == null) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Store Name was not set on request.");
    }

    Store entity =
        storeRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Store with id of " + id + " does not exist."));

    if (updatedStore.name != null) {
      entity.name = updatedStore.name;
    }

    if (updatedStore.quantityProductsInStock != 0) {
      entity.quantityProductsInStock = updatedStore.quantityProductsInStock;
    }

    Store savedStore = storeRepository.save(entity);

    if (TransactionSynchronizationManager.isActualTransactionActive()) {
      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCommit() {
              legacyStoreManagerGateway.updateStoreOnLegacySystem(savedStore);
            }
          });
    } else {
      legacyStoreManagerGateway.updateStoreOnLegacySystem(savedStore);
    }

    return savedStore;
  }

  @DeleteMapping("/{id}")
  @Transactional
  public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
    Store entity =
        storeRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Store with id of " + id + " does not exist."));

    storeRepository.delete(entity);
    return ResponseEntity.noContent().build();
  }
}
