package nl.vpro.domain.api.media;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.*;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.media.support.Title;

/**
 * @author lies
 */
@XmlType(name = "TitleSearchType", propOrder = {"owner", "type", "value"})
@XmlAccessorType(XmlAccessType.NONE)
@EqualsAndHashCode
public class TitleSearch implements Predicate<Title> {
    @XmlElement
    @Getter
    @Setter
    private OwnerType owner;

    @XmlElement
    @Getter
    @Setter
    private TextualType type;

    @XmlElement
    @Getter
    @Setter
    private ExtendedTextMatcher value;

    protected Match match;

    public TitleSearch() {
    }


    @lombok.Builder(builderClassName = "Builder")
    public TitleSearch(OwnerType owner, TextualType type, ExtendedTextMatcher value) {
        this.owner = owner;
        this.type = type;
        this.value = value;
    }


    public boolean hasSearches() {
        return owner != null || type != null || value != null;
    }


    @Override
    public boolean test(@Nullable Title input) {
        throw new UnsupportedOperationException("not used");
    }


    public Match getMatch() {
        return match == null ? Match.MUST : match;
    }

    @Override
    public String toString() {
        return "TitleMatcher{ownertype=" + owner + ", textualtype=" + type + ", value=" + value;
    }
}
