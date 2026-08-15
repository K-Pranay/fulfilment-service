package com.fulfilment.application.monolith.fulfillment;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FulfillmentAssociationRepository
    extends JpaRepository<FulfillmentAssociation, Long> {

  Optional<FulfillmentAssociation>
      findByStoreIdAndProductIdAndWarehouseBusinessUnitCode(
          Long storeId, Long productId, String warehouseBusinessUnitCode);

  List<FulfillmentAssociation> findByStoreIdAndProductId(Long storeId, Long productId);

  List<FulfillmentAssociation> findByStoreId(Long storeId);

  List<FulfillmentAssociation> findByWarehouseBusinessUnitCode(String warehouseBusinessUnitCode);
}
