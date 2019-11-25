package nl.vpro.domain.api.profile;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import nl.vpro.domain.api.Result;

/**
 * @author Michiel Meeuwissen
 * @since 4.3
 */
@XmlRootElement
@XmlSeeAlso(Profile.class)
public class ProfileResult extends Result<Profile> {

    public ProfileResult() {
    }

    public ProfileResult(List<? extends Profile> pages, Long offset, Integer max, long listSizes) {
        super(pages, offset, max, listSizes, TotalQualifier.EQUAL_TO);
    }
}
