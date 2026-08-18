package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ArchiveWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.CreateWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ReplaceWarehouseUseCase;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/warehouse")
public class WarehouseResourceImpl {

  @Autowired private WarehouseRepository warehouseRepository;
  @Autowired private CreateWarehouseUseCase createWarehouseUseCase;
  @Autowired private ReplaceWarehouseUseCase replaceWarehouseUseCase;
  @Autowired private ArchiveWarehouseUseCase archiveWarehouseUseCase;

  @GetMapping
  public List<WarehouseDTO> listAllWarehousesUnits() {
    return warehouseRepository.getAll().stream().map(this::toWarehouseResponse).toList();
  }

  @PostMapping
  @Transactional
  public ResponseEntity<WarehouseDTO> createANewWarehouseUnit(@RequestBody WarehouseDTO data) {
    try {
      var domainModel = toDomainModel(data);
      createWarehouseUseCase.create(domainModel);
      return ResponseEntity.status(HttpStatus.CREATED).body(toWarehouseResponse(domainModel));
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @GetMapping("/{id}")
  public WarehouseDTO getAWarehouseUnitByID(@PathVariable("id") String id) {
    var domainModel = warehouseRepository.findByIdentifier(id);
    if (domainModel == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Warehouse not found with id: " + id);
    }
    return toWarehouseResponse(domainModel);
  }

  @DeleteMapping("/{id}")
  @Transactional
  public ResponseEntity<Void> archiveAWarehouseUnitByID(@PathVariable("id") String id) {
    var domainModel = warehouseRepository.findByIdentifier(id);
    if (domainModel == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Warehouse not found with id: " + id);
    }
    archiveWarehouseUseCase.archive(domainModel);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{businessUnitCode}/replacement")
  @Transactional
  public WarehouseDTO replaceTheCurrentActiveWarehouse(
      @PathVariable("businessUnitCode") String businessUnitCode, @RequestBody WarehouseDTO data) {
    try {
      var domainModel = toDomainModel(data);
      var replacedDomainModel =
          replaceWarehouseUseCase.replaceByCode(businessUnitCode, domainModel);
      return toWarehouseResponse(replacedDomainModel);
    } catch (IllegalArgumentException e) {
      if (e.getMessage().contains("not found")) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
      }
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  private WarehouseDTO toWarehouseResponse(
      com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {
    var response = new WarehouseDTO();
    response.setBusinessUnitCode(warehouse.businessUnitCode);
    response.setLocation(warehouse.location);
    response.setCapacity(warehouse.capacity);
    response.setStock(warehouse.stock);
    return response;
  }

  private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse toDomainModel(
      WarehouseDTO warehouseResponse) {
    var domainModel = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
    domainModel.businessUnitCode = warehouseResponse.getBusinessUnitCode();
    domainModel.location = warehouseResponse.getLocation();
    domainModel.capacity = warehouseResponse.getCapacity();
    domainModel.stock = warehouseResponse.getStock();
    return domainModel;
  }
}
