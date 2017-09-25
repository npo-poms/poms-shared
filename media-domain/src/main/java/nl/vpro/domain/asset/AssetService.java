/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.asset;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import nl.vpro.domain.media.update.Asset;

public interface AssetService {

    default Optional<File> get(String fileName) {
        File file = getFile(fileName);
        if (file.exists() && file.isFile() && file.canRead()) {
            return Optional.of(file);
        }
        return Optional.empty();
    }

    default String getFilePath(String fileName) {
        return String.format("/ext-api/assets/asset/%s.asset", fileName);
    }

    File getFile(String fileName);

    /**
     * Stores the given assets inputstream in the local asset store and replaces the wrapped asset with a new asset
     * source referencing the filename in the local store.
     */
    void store(String fileName, Asset asset);

    String store(String fileName, InputStream stream, Runnable... callbacks);

    String append(String fileName, InputStream stream);

    boolean remove(String fileName);

    List<File> list();

    boolean exists(String itemizedFile);

}
