package nl.vpro.domain.api;

import lombok.EqualsAndHashCode;
import lombok.Setter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlTransient;

import org.meeuw.xml.bind.annotation.XmlDocumentation;

/**
 * @author Michiel Meeuwissen
 * @since 2.3
 */
@Setter
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


    @XmlAttribute(name = "match")
    @XmlDocumentation("The match type. If not specified, the default is MUST. But it can also be SHOULD or NOT.")
    public Match getMatchAttribute() {
        return match == Match.MUST ? null : match;
    }

    public void setMatchAttribute(Match match) {
        this.match = match;
    }
}
