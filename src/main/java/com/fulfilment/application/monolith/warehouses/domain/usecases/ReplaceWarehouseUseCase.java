package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  @Autowired
  public ReplaceWarehouseUseCase(
      WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
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

    // Stock Matching & Capacity Accommodation
    if (newWarehouse.stock == null) {
      newWarehouse.stock = oldWarehouse.stock;
    } else if (!newWarehouse.stock.equals(oldWarehouse.stock)) {
      throw new IllegalArgumentException(
          "Replacement stock ("
              + newWarehouse.stock
              + ") must match previous warehouse stock ("
              + oldWarehouse.stock
              + ")");
    }

    if (newWarehouse.capacity == null || newWarehouse.capacity < oldWarehouse.stock) {
      throw new IllegalArgumentException(
          "New capacity ("
              + newWarehouse.capacity
              + ") cannot accommodate existing stock ("
              + oldWarehouse.stock
              + ")");
    }

    // Location Validation
    Location location = locationResolver.resolveByIdentifier(newWarehouse.location);
    if (location == null) {
      throw new IllegalArgumentException("Invalid location identifier: " + newWarehouse.location);
    }

    List<Warehouse> activeInLocation =
        warehouseStore.getAll().stream()
            .filter(
                w ->
                    w.location != null
                        && w.location.equalsIgnoreCase(location.identification)
                        && w.archivedAt == null)
            .toList();

    int activeCapacity =
        activeInLocation.stream().mapToInt(w -> w.capacity != null ? w.capacity : 0).sum();
    if (oldWarehouse.location != null
        && oldWarehouse.location.equalsIgnoreCase(location.identification)) {
      activeCapacity -= (oldWarehouse.capacity != null ? oldWarehouse.capacity : 0);
    }

    if (activeCapacity + newWarehouse.capacity > location.maxCapacity) {
      throw new IllegalArgumentException(
          "New total capacity exceeds location max capacity (" + location.maxCapacity + ")");
    }

    // Archive previous warehouse
    oldWarehouse.archivedAt = LocalDateTime.now();
    warehouseStore.update(oldWarehouse);

    // Create new active warehouse
    newWarehouse.createdAt = LocalDateTime.now();
    warehouseStore.create(newWarehouse);

    return newWarehouse;
  }
}
