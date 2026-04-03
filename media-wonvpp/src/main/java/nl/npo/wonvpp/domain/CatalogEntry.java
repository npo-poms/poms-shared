package nl.npo.wonvpp.domain;

import java.time.Instant;
import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.meeuw.i18n.countries.Country;
import org.meeuw.i18n.countries.validation.ValidCountry;

import nl.vpro.domain.user.validation.BroadcasterValidation;
import nl.npo.wonvpp.domain.validation.ValidCatalogEntry;

import static nl.vpro.domain.user.BroadcasterService.IdType.WON;

@lombok.Builder
@ValidCatalogEntry
public record CatalogEntry(
    @NotNull String prid,
    @Nullable Instant publicationTimestamp,
    @NotNull MediaTypeEnum mediaType,
    @NotNull ContentTypeEnum contentType,
    @NotNull String title,
    @Nullable String displayTitle,
    @Nullable String originalTitle,
    @Nullable List<@Valid LanguageType> languages,
    @Nullable Boolean isDubbed,
    @Nullable List<@Valid CaptionType> captions,
    @Nullable SynopsisType synopsis,
    @Nullable GenreType genre,
    @Nullable RatingType rating,
    @Nullable  List<@BroadcasterValidation(idType = WON) String> broadcasters,
    @Nullable @ValidCountry Country productionCountry,
    @Nullable Short productionYear,
    @Nullable Integer episodeNumber,
    @Nullable String seasonNumber,
    @Nullable List<@Valid CreditsType> castAndCrew,
    @Nullable RelationType relations,
    @Nullable List<@Valid AvailabilityType> availability,
    @Nullable String metadataSource
) {
}
