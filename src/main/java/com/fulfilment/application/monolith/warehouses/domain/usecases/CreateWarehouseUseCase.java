package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  @Autowired
  public CreateWarehouseUseCase(
      WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void create(Warehouse warehouse) {
    if (warehouse == null) {
      throw new IllegalArgumentException("Warehouse data must not be null");
    }

    // 1. Business Unit Code Verification
    if (warehouse.businessUnitCode == null || warehouse.businessUnitCode.isBlank()) {
      throw new IllegalArgumentException("Business unit code is required");
    }
    if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
      throw new IllegalArgumentException(
          "Business unit code already exists: " + warehouse.businessUnitCode);
    }

    // 2. Location Validation
    if (warehouse.location == null || warehouse.location.isBlank()) {
      throw new IllegalArgumentException("Location is required");
    }
    Location location = locationResolver.resolveByIdentifier(warehouse.location);
    if (location == null) {
      throw new IllegalArgumentException("Invalid location identifier: " + warehouse.location);
    }

    // 3. Basic Stock and Capacity checks
    if (warehouse.capacity == null || warehouse.capacity <= 0) {
      throw new IllegalArgumentException("Warehouse capacity must be greater than zero");
    }
    if (warehouse.stock == null || warehouse.stock < 0) {
      throw new IllegalArgumentException("Stock cannot be negative");
    }
    if (warehouse.stock > warehouse.capacity) {
      throw new IllegalArgumentException(
          "Stock ("
              + warehouse.stock
              + ") exceeds warehouse capacity ("
              + warehouse.capacity
              + ")");
    }

    // 4. Warehouse Creation Feasibility & Location Capacity limits
    List<Warehouse> activeInLocation =
        warehouseStore.getAll().stream()
            .filter(
                w ->
                    w.location != null
                        && w.location.equalsIgnoreCase(location.identification)
                        && w.archivedAt == null)
            .toList();

    if (activeInLocation.size() >= location.maxNumberOfWarehouses) {
      throw new IllegalArgumentException(
          "Maximum number of warehouses ("
              + location.maxNumberOfWarehouses
              + ") reached for location: "
              + location.identification);
    }

    int currentCapacity =
        activeInLocation.stream().mapToInt(w -> w.capacity != null ? w.capacity : 0).sum();
    if (currentCapacity + warehouse.capacity > location.maxCapacity) {
      throw new IllegalArgumentException(
          "New total capacity ("
              + (currentCapacity + warehouse.capacity)
              + ") exceeds location max capacity ("
              + location.maxCapacity
              + ")");
    }

    // If all went well, create the warehouse
    warehouseStore.create(warehouse);
  }
}
