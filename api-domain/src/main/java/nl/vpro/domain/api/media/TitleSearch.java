package nl.vpro.domain.api.media;

import lombok.*;

import java.util.Objects;

import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.domain.api.*;
import nl.vpro.domain.media.support.*;

/**
 * @author Lies Kombrink
 */
@XmlType(name = "titleSearchType", propOrder = {})
@XmlAccessorType(XmlAccessType.NONE)
@EqualsAndHashCode(callSuper = true)
public class TitleSearch extends AbstractSearch<Title>  {

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
    private StandardMatchType matchType;

    private Boolean caseSensitive;


    public TitleSearch() {

    }

    @lombok.Builder(builderClassName = "Builder")
    private TitleSearch(OwnerType owner, TextualType type, String value, Match match, StandardMatchType matchType, Boolean caseSensitive) {
        this.match = match == null ? Match.MUST : match;
        this.value = value;
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
        return asExtendedTextMatcher().test(input.get()) && Objects.equals(input.getOwner(), owner) && Objects.equals(input.getType(), type);
    }

    @Override
    public String toString() {
        return "TitleSearch{ownertype=" + owner + ", textualtype=" + type + ", value=" + value + "}";
    }


    public ExtendedTextMatcher asExtendedTextMatcher() {
        return new ExtendedTextMatcher(value, match, matchType, caseSensitive == null ? false : caseSensitive);
    }

    @Override
    public boolean hasSearches() {
        return true;
    }

    @XmlAttribute
    public void setCaseSensitive(Boolean caseSensitive) {
        this.caseSensitive = caseSensitive ? null : caseSensitive;
    }

    public boolean isCaseSensitive() {
        return caseSensitive == null ? true : caseSensitive;
    }

    public boolean searchEquals(TitleSearch other) {
        return Objects.equals(owner, other.owner) &&
            Objects.equals(type, other.type) &&
            Objects.equals(value, other.value) &&
            Objects.equals(matchType, other.matchType);

    }


}
