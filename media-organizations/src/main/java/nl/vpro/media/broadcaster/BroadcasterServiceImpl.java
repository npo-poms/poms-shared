/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.broadcaster;

/**
 * @author rico
 * @since 3.0
 * @deprecated  Use {@link URLBroadcasterServiceImpl} instead
 */
@Deprecated
public class BroadcasterServiceImpl extends URLBroadcasterServiceImpl {


    public BroadcasterServiceImpl(String configFile) {
        super(configFile);
    }

}
