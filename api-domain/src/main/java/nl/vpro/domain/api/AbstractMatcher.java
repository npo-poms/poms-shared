package nl.vpro.domain.api;

import lombok.EqualsAndHashCode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Michiel Meeuwissen
 * @since 2.3
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlTransient
@EqualsAndHashCode
public abstract class AbstractMatcher<V> implements Matcher<V> {

    @XmlTransient
    protected Match match;

    protected AbstractMatcher() {
    }

    protected AbstractMatcher(Match match) {
        this.match = match == Match.MUST ? null : match;
    }

    @Override
    public Match getMatch() {
        return match == null ? Match.MUST : match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }


    @XmlAttribute(name = "match")
    public Match getMatchAttribute() {
        return match == Match.MUST ? null : match;
    }

    public void setMatchAttribute(Match match) {
        this.match = match;
    }
}
