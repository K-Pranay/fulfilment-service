package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;
  private final WarehouseValidator warehouseValidator;

  public CreateWarehouseUseCase(
      WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this(warehouseStore, locationResolver, new WarehouseValidator());
  }

  @Autowired
  public CreateWarehouseUseCase(
      WarehouseStore warehouseStore,
      LocationResolver locationResolver,
      WarehouseValidator warehouseValidator) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
    this.warehouseValidator = warehouseValidator;
  }

  @Override
  public void create(Warehouse warehouse) {
    // Separate validation logic delegated to WarehouseValidator
    warehouseValidator.validateCreation(warehouse, warehouseStore, locationResolver);

    // Create the warehouse after validation passes
    warehouseStore.create(warehouse);
  }
}
