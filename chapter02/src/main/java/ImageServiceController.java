/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 *
 * Copyright (c) 2018 https://rdp.reactiveplatform.xyz/
 *
 */

import java.awt.*;
import java.awt.image.BufferedImage;

// Listing 2.1 Excerpt from a simple controller for an image service
public class ImageServiceController {
  private static final Image FALLBACK = new BufferedImage(100, 100, BufferedImage.TYPE_INT_BGR);

  // #snip
  public interface Images {
    Image get(String key);

    void add(String key, Image image);
  }

  private Images cache;
  private Images database;

  public Image retrieveImages(String key) {
    Image result = cache.get(key);
    if (result != null) {
      return result;
    } else {
      result = database.get(key);
      if (result != null) {
        cache.add(key, result);
        return result;
      } else {
        return FALLBACK;
      }
    }
  }
  // #snip

}
