package nl.vpro.domain.api.media;

import lombok.*;

import java.net.URI;
import java.util.Objects;

import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.domain.api.*;
import nl.vpro.domain.media.GeoLocation;
import nl.vpro.domain.media.GeoRoleType;
import nl.vpro.domain.media.support.OwnerType;


/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@XmlType(name = "geoLocationSearchType", propOrder = {})
@XmlAccessorType(XmlAccessType.NONE)
@EqualsAndHashCode(callSuper = true)
public class GeoLocationSearch extends AbstractSearch<GeoLocation> {

    @XmlAttribute
    @Getter
    @Setter
    private OwnerType owner;

    @XmlAttribute
    @Getter
    @Setter
    private GeoRoleType role;

    @XmlAttribute
    @Getter
    @Setter
    private URI gtaaUri;

    @XmlValue
    @Setter
    @Getter
    protected String value;


    private Boolean caseSensitive;


    public GeoLocationSearch() {

    }

    @lombok.Builder(builderClassName = "Builder")
    private GeoLocationSearch(
        OwnerType owner,
        String value,
        URI gtaaUri,
        GeoRoleType role,
        Match match,
        Boolean caseSensitive) {
        this.match = match == null ? Match.MUST : match;
        this.value = value;
        this.caseSensitive = caseSensitive;
        this.owner = owner;
        this.gtaaUri = gtaaUri;
        this.role = role;
    }



    @Override
    public boolean test(@Nullable GeoLocation input) {
        if (input == null) {
            return false;
        }
        return asExtendedTextMatcher().test(input.getName());
    }

    @Override
    public String toString() {
        return "GeoLocationSearch{" +
            "owner=" + owner +
            ", role=" + role +
            ", gtaaURI=" + gtaaUri +
            ", value='" + value + '\'' +
            ", caseSensitive=" + caseSensitive +
            '}';
    }

    public ExtendedTextMatcher asExtendedTextMatcher() {
        return new ExtendedTextMatcher(value, match, StandardMatchType.TEXT, caseSensitive == null ? false : caseSensitive);
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

    public boolean searchEquals(GeoLocationSearch other) {
        return Objects.equals(owner, other.owner) &&
            Objects.equals(gtaaUri, other.gtaaUri) &&
            Objects.equals(value, other.value);

    }


}
