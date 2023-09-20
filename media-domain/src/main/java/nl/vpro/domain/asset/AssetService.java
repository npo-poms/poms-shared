/*
 * Copyright (C) 2011 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.asset;

import java.io.File;
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
        return String.format("/assets/asset/%s", fileName);
    }

    File getFile(String fileName);

    default String getMimeType(String fileName) {
        if (fileName.endsWith(".mp4")) {
            return "video/mp4";
        } else {
            return "application/octet-stream";
        }
    }


    /**
     * Stores the given assets inputstream in the local asset store and replaces the wrapped asset with a new asset
     * source referencing the filename in the local store.
     */
    String store(String fileName, Asset asset);


    boolean remove(String fileName);

    List<File> list();

    default boolean exists(String fileName) {
        File file = getFile(fileName);
        return file.exists();
    }


}
