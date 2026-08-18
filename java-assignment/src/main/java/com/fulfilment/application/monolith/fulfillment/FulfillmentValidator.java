package com.fulfilment.application.monolith.fulfillment;

import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.stores.StoreRepository;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class FulfillmentValidator {

  public void validateEntitiesExist(
      Long storeId,
      Long productId,
      String warehouseBusinessUnitCode,
      StoreRepository storeRepository,
      ProductRepository productRepository,
      WarehouseRepository warehouseRepository) {

    if (storeId == null
        || productId == null
        || warehouseBusinessUnitCode == null
        || warehouseBusinessUnitCode.isBlank()) {
      throw new IllegalArgumentException(
          "storeId, productId, and warehouseBusinessUnitCode are required");
    }

    // Verify Store existence
    Store store = storeRepository.findById(storeId).orElse(null);
    if (store == null) {
      throw new IllegalArgumentException("Store not found with id: " + storeId);
    }

    // Verify Product existence
    if (!productRepository.existsById(productId)) {
      throw new IllegalArgumentException("Product not found with id: " + productId);
    }

    // Verify Warehouse existence (must be active)
    if (warehouseRepository.findByBusinessUnitCode(warehouseBusinessUnitCode) == null) {
      throw new IllegalArgumentException(
          "Active warehouse not found with code: " + warehouseBusinessUnitCode);
    }
  }

  public void validateFulfillmentConstraints(
      Long storeId,
      Long productId,
      String warehouseBusinessUnitCode,
      FulfillmentAssociationRepository repository) {

    // Constraint 1: Max 2 different Warehouses per Product per Store
    List<FulfillmentAssociation> existingProductStoreAssocs =
        repository.findByStoreIdAndProductId(storeId, productId);
    long distinctWarehousesForProductStore =
        existingProductStoreAssocs.stream()
            .map(a -> a.warehouseBusinessUnitCode)
            .distinct()
            .count();
    if (existingProductStoreAssocs.stream()
        .noneMatch(a -> a.warehouseBusinessUnitCode.equalsIgnoreCase(warehouseBusinessUnitCode))) {
      if (distinctWarehousesForProductStore >= 2) {
        throw new IllegalArgumentException(
            "Constraint violation: Product "
                + productId
                + " is already fulfilled by 2 warehouses for Store "
                + storeId);
      }
    }

    // Constraint 2: Max 3 different Warehouses per Store
    List<FulfillmentAssociation> existingStoreAssocs = repository.findByStoreId(storeId);
    long distinctWarehousesForStore =
        existingStoreAssocs.stream().map(a -> a.warehouseBusinessUnitCode).distinct().count();
    if (existingStoreAssocs.stream()
        .noneMatch(a -> a.warehouseBusinessUnitCode.equalsIgnoreCase(warehouseBusinessUnitCode))) {
      if (distinctWarehousesForStore >= 3) {
        throw new IllegalArgumentException(
            "Constraint violation: Store " + storeId + " is already fulfilled by 3 warehouses");
      }
    }

    // Constraint 3: Max 5 types of Products per Warehouse
    List<FulfillmentAssociation> existingWarehouseAssocs =
        repository.findByWarehouseBusinessUnitCode(warehouseBusinessUnitCode);
    long distinctProductsForWarehouse =
        existingWarehouseAssocs.stream().map(a -> a.productId).distinct().count();
    if (existingWarehouseAssocs.stream().noneMatch(a -> a.productId.equals(productId))) {
      if (distinctProductsForWarehouse >= 5) {
        throw new IllegalArgumentException(
            "Constraint violation: Warehouse "
                + warehouseBusinessUnitCode
                + " already stores 5 different product types");
      }
    }
  }
}
