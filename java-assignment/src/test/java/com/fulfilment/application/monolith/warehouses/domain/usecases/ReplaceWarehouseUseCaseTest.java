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

public class ReplaceWarehouseUseCaseTest {

  private ReplaceWarehouseUseCase replaceWarehouseUseCase;
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
            return storeList.stream().filter(w -> w.archivedAt == null).toList();
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
                .filter(w -> w.businessUnitCode.equalsIgnoreCase(buCode) && w.archivedAt == null)
                .findFirst()
                .orElse(null);
          }
        };

    replaceWarehouseUseCase =
        new ReplaceWarehouseUseCase(mockWarehouseStore, locationResolver);
  }

  @Test
  public void testReplaceWarehouseSuccess() {
    Warehouse oldW = new Warehouse();
    oldW.businessUnitCode = "MWH.001";
    oldW.location = "ZWOLLE-001";
    oldW.capacity = 40;
    oldW.stock = 10;
    storeList.add(oldW);

    Warehouse newW = new Warehouse();
    newW.location = "ZWOLLE-001";
    newW.capacity = 40;
    newW.stock = 10;

    Warehouse replaced = replaceWarehouseUseCase.replaceByCode("MWH.001", newW);

    assertNotNull(replaced);
    assertNotNull(oldW.archivedAt);
    assertEquals("MWH.001", replaced.businessUnitCode);
  }

  @Test
  public void testReplaceWarehouseInsufficientCapacityThrowsException() {
    Warehouse oldW = new Warehouse();
    oldW.businessUnitCode = "MWH.001";
    oldW.location = "ZWOLLE-001";
    oldW.capacity = 40;
    oldW.stock = 30;
    storeList.add(oldW);

    Warehouse newW = new Warehouse();
    newW.location = "ZWOLLE-001";
    newW.capacity = 20; // smaller than stock 30
    newW.stock = 30;

    Exception ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              replaceWarehouseUseCase.replaceByCode("MWH.001", newW);
            });
    assertTrue(ex.getMessage().contains("cannot accommodate"));
  }
}
