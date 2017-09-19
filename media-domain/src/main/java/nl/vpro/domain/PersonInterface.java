package nl.vpro.domain;

import nl.vpro.domain.media.gtaa.GTAARecord;

public interface PersonInterface {


    String getGivenName();

    String getFamilyName();

    String getGtaaUri();

    String getPrefLabel();

}
