/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.exceptions;

import java.util.ArrayList;
import java.util.List;

import nl.vpro.domain.media.MediaObject;

public class CircularReferenceException extends RuntimeException {

    private List<MediaObject> familyTree = new ArrayList<>();

    public CircularReferenceException(MediaObject child, List<MediaObject> ancestry) {
        super("Circular reference: " + child + " has ancestors " + ancestry);
        familyTree.addAll(ancestry);
        familyTree.add(child);
    }

    public List<MediaObject> getFamilyTree() {
        return familyTree;
    }
}
