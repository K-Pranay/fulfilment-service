package com.fulfilment.application.monolith.fulfillment;

import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.stores.StoreRepository;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FulfillmentAssociationService {

  @Autowired private FulfillmentAssociationRepository repository;
  @Autowired private StoreRepository storeRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private WarehouseRepository warehouseRepository;
  @Autowired private FulfillmentValidator fulfillmentValidator;

  @Transactional
  public FulfillmentAssociation associate(
      Long storeId, Long productId, String warehouseBusinessUnitCode) {

    // 1. Separate input and entity existence validation
    fulfillmentValidator.validateEntitiesExist(
        storeId,
        productId,
        warehouseBusinessUnitCode,
        storeRepository,
        productRepository,
        warehouseRepository);

    // Check if already associated
    var existing =
        repository.findByStoreIdAndProductIdAndWarehouseBusinessUnitCode(
            storeId, productId, warehouseBusinessUnitCode);
    if (existing.isPresent()) {
      return existing.get();
    }

    // 2. Separate domain constraint validation (Max 2 Whs/Product/Store, Max 3 Whs/Store, Max 5 Products/Wh)
    fulfillmentValidator.validateFulfillmentConstraints(
        storeId, productId, warehouseBusinessUnitCode, repository);

    FulfillmentAssociation newAssoc =
        new FulfillmentAssociation(storeId, productId, warehouseBusinessUnitCode);
    return repository.save(newAssoc);
  }

  @Transactional
  public void disassociate(Long storeId, Long productId, String warehouseBusinessUnitCode) {
    var assoc =
        repository.findByStoreIdAndProductIdAndWarehouseBusinessUnitCode(
            storeId, productId, warehouseBusinessUnitCode);
    assoc.ifPresent(repository::delete);
  }

  public List<FulfillmentAssociation> listAll() {
    return repository.findAll();
  }

  public List<FulfillmentAssociation> listByStore(Long storeId) {
    return repository.findByStoreId(storeId);
  }
}
