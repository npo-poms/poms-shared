/*
 * Copyright (C) 2010 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.exceptions;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import nl.vpro.domain.media.MediaObject;

public class CircularReferenceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -6557241432901240561L;

    private final List<MediaObject> familyTree = new ArrayList<>();

    public CircularReferenceException(MediaObject child, List<MediaObject> ancestry) {
        super("Circular reference: " + child + " has ancestors " + ancestry);
        familyTree.addAll(ancestry);
        familyTree.add(child);
    }

    public List<MediaObject> getFamilyTree() {
        return familyTree;
    }
}
