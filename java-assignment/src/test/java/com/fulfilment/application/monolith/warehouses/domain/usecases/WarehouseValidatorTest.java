package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WarehouseValidatorTest {

  private WarehouseValidator validator;
  private WarehouseStore warehouseStore;
  private LocationResolver locationResolver;

  @BeforeEach
  public void setUp() {
    validator = new WarehouseValidator();
    warehouseStore = new TestWarehouseStore();
    locationResolver = identifier -> new Location(identifier, 5, 500);
  }

  @Test
  public void testValidateCreationNullWarehouse() {
    assertThrows(
        IllegalArgumentException.class,
        () -> validator.validateCreation(null, warehouseStore, locationResolver));
  }

  @Test
  public void testValidateCreationValidWarehouse() {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "WH-001";
    warehouse.location = "AMSTERDAM-001";
    warehouse.capacity = 100;
    warehouse.stock = 10;

    assertDoesNotThrow(
        () -> validator.validateCreation(warehouse, warehouseStore, locationResolver));
  }

  private static class TestWarehouseStore implements WarehouseStore {
    private final List<Warehouse> warehouses = new ArrayList<>();

    @Override
    public List<Warehouse> getAll() {
      return warehouses;
    }

    @Override
    public void create(Warehouse warehouse) {
      warehouses.add(warehouse);
    }

    @Override
    public void update(Warehouse warehouse) {}

    @Override
    public void remove(Warehouse warehouse) {
      warehouses.remove(warehouse);
    }

    @Override
    public Warehouse findByBusinessUnitCode(String buCode) {
      return warehouses.stream()
          .filter(w -> w.businessUnitCode.equalsIgnoreCase(buCode))
          .findFirst()
          .orElse(null);
    }
  }
}
