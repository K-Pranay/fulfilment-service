package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.*;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CreateWarehouseUseCaseTest {

  private CreateWarehouseUseCase createWarehouseUseCase;
  private WarehouseStore mockWarehouseStore;
  private LocationResolver locationResolver;
  private List<Warehouse> storeList;

  @BeforeEach
  public void setUp() {
    storeList = new ArrayList<>();
    locationResolver = new LocationGateway();
    mockWarehouseStore =
        new WarehouseStore() {
          @Override
          public List<Warehouse> getAll() {
            return storeList;
          }

          @Override
          public void create(Warehouse warehouse) {
            storeList.add(warehouse);
          }

          @Override
          public void update(Warehouse warehouse) {}

          @Override
          public void remove(Warehouse warehouse) {}

          @Override
          public Warehouse findByBusinessUnitCode(String buCode) {
            return storeList.stream()
                .filter(w -> w.businessUnitCode.equalsIgnoreCase(buCode))
                .findFirst()
                .orElse(null);
          }
        };

    createWarehouseUseCase = new CreateWarehouseUseCase(mockWarehouseStore, locationResolver);
  }

  @Test
  public void testCreateWarehouseSuccess() {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "MWH.NEW";
    warehouse.location = "AMSTERDAM-001";
    warehouse.capacity = 50;
    warehouse.stock = 20;

    createWarehouseUseCase.create(warehouse);

    assertEquals(1, storeList.size());
    assertEquals("MWH.NEW", storeList.get(0).businessUnitCode);
  }

  @Test
  public void testCreateWarehouseDuplicateBUCodeThrowsException() {
    Warehouse w1 = new Warehouse();
    w1.businessUnitCode = "MWH.001";
    w1.location = "AMSTERDAM-001";
    w1.capacity = 30;
    w1.stock = 10;
    storeList.add(w1);

    Warehouse w2 = new Warehouse();
    w2.businessUnitCode = "MWH.001";
    w2.location = "AMSTERDAM-001";
    w2.capacity = 40;
    w2.stock = 10;

    Exception ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              createWarehouseUseCase.create(w2);
            });
    assertTrue(ex.getMessage().contains("already exists"));
  }

  @Test
  public void testCreateWarehouseInvalidLocationThrowsException() {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "MWH.NEW";
    warehouse.location = "INVALID-LOC";
    warehouse.capacity = 50;
    warehouse.stock = 20;

    Exception ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              createWarehouseUseCase.create(warehouse);
            });
    assertTrue(ex.getMessage().contains("Invalid location"));
  }

  @Test
  public void testCreateWarehouseStockExceedsCapacityThrowsException() {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "MWH.NEW";
    warehouse.location = "AMSTERDAM-001";
    warehouse.capacity = 30;
    warehouse.stock = 50;

    Exception ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              createWarehouseUseCase.create(warehouse);
            });
    assertTrue(ex.getMessage().contains("exceeds warehouse capacity"));
  }
}
