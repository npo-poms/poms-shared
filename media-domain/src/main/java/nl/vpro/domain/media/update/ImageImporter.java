/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import nl.vpro.domain.media.support.Image;

public interface ImageImporter {

    Image save(ImageUpdate download, boolean metadata) throws DownloadException;



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
