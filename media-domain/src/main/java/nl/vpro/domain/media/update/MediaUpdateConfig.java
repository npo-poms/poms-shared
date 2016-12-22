/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

/**
 * @author Roelof Jan Koekoek
 * @since 2.3
 */
public class MediaUpdateConfig {

    private boolean isMemberOfUpdate = true;

    public boolean isMemberOfUpdate() {
        return isMemberOfUpdate;
    }

    public void setMemberOfUpdate(boolean memberOfUpdate) {
        isMemberOfUpdate = memberOfUpdate;
    }
}
