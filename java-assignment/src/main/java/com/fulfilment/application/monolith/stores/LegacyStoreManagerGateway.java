package com.fulfilment.application.monolith.stores;

import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.stereotype.Component;

@Component
public class LegacyStoreManagerGateway {

  public void createStoreOnLegacySystem(Store store) {
    writeToFile(store);
  }

  public void updateStoreOnLegacySystem(Store store) {
    writeToFile(store);
  }

  private void writeToFile(Store store) {
    try {
      Path tempFile = Files.createTempFile(store.name != null ? store.name : "store", ".txt");
      System.out.println("Temporary file created at: " + tempFile.toString());

      String content =
          "Store created. [ name ="
              + store.name
              + " ] [ items on stock ="
              + store.quantityProductsInStock
              + "]";
      Files.write(tempFile, content.getBytes());
      System.out.println("Data written to temporary file.");

      String readContent = new String(Files.readAllBytes(tempFile));
      System.out.println("Data read from temporary file: " + readContent);

      Files.delete(tempFile);
      System.out.println("Temporary file deleted.");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
