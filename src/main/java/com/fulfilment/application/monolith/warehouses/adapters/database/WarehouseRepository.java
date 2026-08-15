package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class WarehouseRepository implements WarehouseStore {

  @Autowired private DbWarehouseJpaRepository jpaRepository;

  @Override
  public List<Warehouse> getAll() {
    return jpaRepository.findByArchivedAtIsNull().stream().map(DbWarehouse::toWarehouse).toList();
  }

  @Override
  public void create(Warehouse warehouse) {
    DbWarehouse dbWarehouse = new DbWarehouse();
    dbWarehouse.businessUnitCode = warehouse.businessUnitCode;
    dbWarehouse.location = warehouse.location;
    dbWarehouse.capacity = warehouse.capacity;
    dbWarehouse.stock = warehouse.stock;
    dbWarehouse.createdAt =
        warehouse.createdAt != null ? warehouse.createdAt : LocalDateTime.now();
    dbWarehouse.archivedAt = warehouse.archivedAt;
    jpaRepository.save(dbWarehouse);
  }

  @Override
  public void update(Warehouse warehouse) {
    DbWarehouse dbWarehouse =
        jpaRepository
            .findByBusinessUnitCodeAndArchivedAtIsNull(warehouse.businessUnitCode)
            .orElseGet(
                () ->
                    jpaRepository
                        .findByBusinessUnitCode(warehouse.businessUnitCode)
                        .orElse(null));

    if (dbWarehouse != null) {
      dbWarehouse.location = warehouse.location;
      dbWarehouse.capacity = warehouse.capacity;
      dbWarehouse.stock = warehouse.stock;
      dbWarehouse.archivedAt = warehouse.archivedAt;
      jpaRepository.save(dbWarehouse);
    } else {
      create(warehouse);
    }
  }

  @Override
  public void remove(Warehouse warehouse) {
    DbWarehouse dbWarehouse =
        jpaRepository
            .findByBusinessUnitCodeAndArchivedAtIsNull(warehouse.businessUnitCode)
            .orElse(null);
    if (dbWarehouse != null) {
      dbWarehouse.archivedAt = LocalDateTime.now();
      jpaRepository.save(dbWarehouse);
    }
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    return jpaRepository
        .findByBusinessUnitCodeAndArchivedAtIsNull(buCode)
        .map(DbWarehouse::toWarehouse)
        .orElse(null);
  }

  public Warehouse findByIdentifier(String idOrBuCode) {
    DbWarehouse dbWarehouse = null;
    try {
      Long id = Long.parseLong(idOrBuCode);
      dbWarehouse = jpaRepository.findById(id).filter(w -> w.archivedAt == null).orElse(null);
    } catch (NumberFormatException ignored) {
    }
    if (dbWarehouse == null) {
      dbWarehouse =
          jpaRepository.findByBusinessUnitCodeAndArchivedAtIsNull(idOrBuCode).orElse(null);
    }
    return dbWarehouse != null ? dbWarehouse.toWarehouse() : null;
  }

  public int countActiveByLocation(String location) {
    return (int) jpaRepository.countByLocationAndArchivedAtIsNull(location);
  }

  public int getActiveCapacityByLocation(String location) {
    return jpaRepository.sumCapacityByLocationAndArchivedAtIsNull(location);
  }
}
