/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import javax.annotation.Nonnull;

import nl.vpro.domain.media.support.Image;
import nl.vpro.domain.media.support.OwnerType;

public interface ImageImporter {

    Image save(
        @Nonnull ImageUpdate download, boolean metadata, @Nonnull  OwnerType owne) throws DownloadException;

    class DownloadException extends RuntimeException {

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
