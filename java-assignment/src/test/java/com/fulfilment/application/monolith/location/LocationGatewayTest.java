package com.fulfilment.application.monolith.location;

import static org.junit.jupiter.api.Assertions.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LocationGatewayTest {

  private LocationGateway locationGateway;

  @BeforeEach
  public void setUp() {
    locationGateway = new LocationGateway();
  }

  @Test
  public void testWhenResolveExistingLocationShouldReturn() {
    Location location = locationGateway.resolveByIdentifier("ZWOLLE-001");
    assertNotNull(location);
    assertEquals("ZWOLLE-001", location.identification);
    assertEquals(1, location.maxNumberOfWarehouses);
    assertEquals(40, location.maxCapacity);
  }

  @Test
  public void testWhenResolveExistingLocationCaseInsensitiveShouldReturn() {
    Location location = locationGateway.resolveByIdentifier("amsterdam-001");
    assertNotNull(location);
    assertEquals("AMSTERDAM-001", location.identification);
    assertEquals(5, location.maxNumberOfWarehouses);
    assertEquals(100, location.maxCapacity);
  }

  @Test
  public void testWhenResolveNonExistingLocationShouldReturnNull() {
    Location location = locationGateway.resolveByIdentifier("UNKNOWN-999");
    assertNull(location);
  }

  @Test
  public void testWhenResolveNullOrBlankShouldReturnNull() {
    assertNull(locationGateway.resolveByIdentifier(null));
    assertNull(locationGateway.resolveByIdentifier("   "));
  }
}
