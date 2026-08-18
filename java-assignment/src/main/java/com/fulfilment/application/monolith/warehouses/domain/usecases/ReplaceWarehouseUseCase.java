package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;
  private final WarehouseValidator warehouseValidator;

  public ReplaceWarehouseUseCase(
      WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this(warehouseStore, locationResolver, new WarehouseValidator());
  }

  @Autowired
  public ReplaceWarehouseUseCase(
      WarehouseStore warehouseStore,
      LocationResolver locationResolver,
      WarehouseValidator warehouseValidator) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
    this.warehouseValidator = warehouseValidator;
  }

  @Override
  public void replace(Warehouse newWarehouse) {
    if (newWarehouse == null || newWarehouse.businessUnitCode == null) {
      throw new IllegalArgumentException("Warehouse and business unit code are required");
    }
    replaceByCode(newWarehouse.businessUnitCode, newWarehouse);
  }

  public Warehouse replaceByCode(String businessUnitCode, Warehouse newWarehouse) {
    Warehouse oldWarehouse = warehouseStore.findByBusinessUnitCode(businessUnitCode);
    if (oldWarehouse == null) {
      throw new IllegalArgumentException(
          "Active warehouse with business unit code " + businessUnitCode + " not found");
    }

    newWarehouse.businessUnitCode = businessUnitCode;

    // Separate replacement validation logic delegated to WarehouseValidator
    warehouseValidator.validateReplacement(
        oldWarehouse, newWarehouse, warehouseStore, locationResolver);

    // Archive previous warehouse
    oldWarehouse.archivedAt = LocalDateTime.now();
    warehouseStore.update(oldWarehouse);

    // Create new active warehouse
    newWarehouse.createdAt = LocalDateTime.now();
    warehouseStore.create(newWarehouse);

    return newWarehouse;
  }
}
