/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.asset;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import nl.vpro.domain.media.update.Asset;

public interface AssetService {

    File get(String fileName);

    /**
     * Stores the given assets inputstream in the local asset store and replaces the wrapped asset with a new asset
     * source referencing the filename in the local store.
     */
    void store(String fileName, Asset asset);

    String store(String fileName, InputStream stream);

    String append(String fileName, InputStream stream);

    boolean remove(String fileName);

    List<File> list();

    boolean exists(String itemizedFile);

}
