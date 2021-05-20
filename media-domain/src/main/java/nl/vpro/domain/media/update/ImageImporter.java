/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.media.support.Image;
import nl.vpro.domain.media.support.OwnerType;

public interface ImageImporter {

    Image save(
        @NonNull ImageUpdate download, boolean metadata, @NonNull  OwnerType owne) throws DownloadException;

    class DownloadException extends RuntimeException {

        private static final long serialVersionUID = 960450531709055817L;
        private final boolean retryable;

        public DownloadException(boolean retryable, String s, Throwable cause) {
            super(s, cause);
            this.retryable = retryable;
        }

        public boolean isRetryable() {
            return retryable;
        }
    }
}
