/*
 * Copyright (C) 2010 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.exceptions;

import java.io.Serial;
import java.util.*;

import nl.vpro.domain.media.MediaObject;

public class CircularReferenceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -6557241432901240561L;

    private final List<MediaObject> familyTree = new ArrayList<>();

    private CircularReferenceException(String message, MediaObject child, List<MediaObject> ancestry) {
        super(message);
        familyTree.addAll(ancestry);
        familyTree.add(child);
    }

    public CircularReferenceException(MediaObject child, List<MediaObject> ancestry) {
        this("Circular reference: " + child + " has ancestors " + ancestry, child, ancestry);
    }

    public static CircularReferenceException self(MediaObject child, List<MediaObject> ancestry) {
        return new CircularReferenceException("Can't make " + child + " member of itself", child, ancestry);
    }

    public List<MediaObject> getFamilyTree() {
        return Collections.unmodifiableList(familyTree);
    }
}
