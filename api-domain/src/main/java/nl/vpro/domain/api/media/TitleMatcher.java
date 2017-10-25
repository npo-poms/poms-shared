package nl.vpro.domain.api.media;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.api.*;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.media.support.Title;

/**
 * @author lies
 */
@XmlType(name = "titleSearchType", propOrder = {})
@XmlAccessorType(XmlAccessType.NONE)
@EqualsAndHashCode(callSuper = true)
public class TitleMatcher extends AbstractSearch implements Predicate<Title>  {
    @XmlAttribute
    @Getter
    @Setter
    private OwnerType owner;

    @XmlAttribute
    @Getter
    @Setter
    private TextualType type;


    @XmlValue
    @Setter
    @Getter
    protected String value;

    @XmlAttribute
    @Setter
    @Getter
    private ExtendedMatchType matchType;

    @XmlAttribute
    @Setter
    @Getter
    private Boolean caseSensitive;


    public TitleMatcher() {
    }

    @lombok.Builder(builderClassName = "Builder")
    private TitleMatcher(OwnerType owner, TextualType type, String value, Match match, ExtendedMatchType matchType, boolean caseSensitive) {
        this.match = match;
        this.value = value;
        this.match = match;
        this.matchType = matchType;
        this.caseSensitive = caseSensitive;

        this.owner = owner;
        this.type = type;
    }



    @Override
    public boolean test(@Nullable Title input) {
        if (input == null) {
            return false;
        }
        return asExtendedTextMatcher().test(input.getTitle()) && Objects.equals(input.getOwner(), owner) && Objects.equals(input.getType(), type);
    }

    @Override
    public String toString() {
        return "TitleMatcher{ownertype=" + owner + ", textualtype=" + type + ", value=" + value + "}";
    }


    public ExtendedTextMatcher asExtendedTextMatcher() {
        return new ExtendedTextMatcher(value, match, matchType, caseSensitive == null ? false : caseSensitive);
    }

    @Override
    public boolean hasSearches() {
        return false;

    }
}
