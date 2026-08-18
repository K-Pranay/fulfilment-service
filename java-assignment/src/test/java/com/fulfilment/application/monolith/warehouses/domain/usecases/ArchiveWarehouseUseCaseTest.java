package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArchiveWarehouseUseCaseTest {

  private ArchiveWarehouseUseCase archiveWarehouseUseCase;
  private List<Warehouse> storeList;

  @BeforeEach
  public void setUp() {
    storeList = new ArrayList<>();
    WarehouseStore mockWarehouseStore =
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
            return null;
          }
        };

    archiveWarehouseUseCase = new ArchiveWarehouseUseCase(mockWarehouseStore);
  }

  @Test
  public void testArchiveWarehouseSetsArchivedAtTimestamp() {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "MWH.001";
    warehouse.location = "ZWOLLE-001";
    warehouse.capacity = 40;
    warehouse.stock = 10;

    assertNull(warehouse.archivedAt);

    archiveWarehouseUseCase.archive(warehouse);

    assertNotNull(warehouse.archivedAt);
  }
}
