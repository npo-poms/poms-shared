/*
 * Copyright (C) 2022 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.image.backend;

import java.io.Serial;

/**
 * A stream representing the image was found, but it is empty, unlikely representing a valid image.
 */
public class ImageEmptyException extends ImageNotFoundException {
    @Serial
    private static final long serialVersionUID = 1607385729781522225L;



    public ImageEmptyException(long id, String s) {
        super(id, s);
    }
}
