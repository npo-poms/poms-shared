/**
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

/**
 * @author Roelof Jan Koekoek
 * @since 2.3
 */
public class ProgramUpdateConfig extends MediaUpdateConfig {

    private boolean isEpisodeOfUpdate = true;

    public boolean isEpisodeOfUpdate() {
        return isEpisodeOfUpdate;
    }

    public void setEpisodeOfUpdate(boolean episodeOfUpdate) {
        isEpisodeOfUpdate = episodeOfUpdate;
    }

    private boolean ratingsUpdate = true;

    public boolean isRatingsUpdate() {
        return ratingsUpdate;
    }

    public void setRatingsUpdate(boolean ratingsUpdate) {
        this.ratingsUpdate = ratingsUpdate;
    }
}
