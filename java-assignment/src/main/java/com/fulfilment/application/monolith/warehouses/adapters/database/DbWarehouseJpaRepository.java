package com.fulfilment.application.monolith.warehouses.adapters.database;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DbWarehouseJpaRepository extends JpaRepository<DbWarehouse, Long> {

  List<DbWarehouse> findByArchivedAtIsNull();

  Optional<DbWarehouse> findByBusinessUnitCodeAndArchivedAtIsNull(String businessUnitCode);

  Optional<DbWarehouse> findByBusinessUnitCode(String businessUnitCode);

  long countByLocationAndArchivedAtIsNull(String location);

  @Query(
      "SELECT COALESCE(SUM(w.capacity), 0) FROM DbWarehouse w WHERE w.location = :location AND"
          + " w.archivedAt IS NULL")
  int sumCapacityByLocationAndArchivedAtIsNull(@Param("location") String location);
}
