/*
 * Copyright 2017 http://rdp.reactiveplatform.xyz/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.awt.*;
import java.awt.image.BufferedImage;

// Listing 2.1 Excerpt from a simple controller for an image service
public class ImageServiceController {
    private static final Image fallback= new BufferedImage(100,100,BufferedImage.TYPE_INT_BGR);

    // #snip
    public interface Images {
        Image get(String Key);
        void add(String key, Image image);
    }

    public Images cache;
    public Images database;

    public Image retrieveImages(String key){
        Image result = cache.get(key);
        if (result != null) {
            return result;
        } else {
            result = database.get(key);
            if (result != null) {
                cache.add(key, result);
                return result;
            } else {
                return fallback;
            }
        }
    }
    // #snip

}
