package nl.vpro.domain.api.thesaurus;

import nl.vpro.domain.media.gtaa.GTAARecord;
import nl.vpro.validation.NoHtml;

import javax.xml.bind.annotation.XmlElement;

public interface PersonInterface {


    String getGivenName();

    String getFamilyName();

    String getGtaaUri();

    String getPrefLabel();

}
