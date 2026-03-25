package nl.vpro.domain.npo.wonvpp;

import java.time.Instant;
import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.meeuw.i18n.languages.ISO_639_Code;

import nl.vpro.domain.npo.wonvpp.validation.ValidCatalogEntry;
import nl.vpro.domain.user.validation.BroadcasterValidation;

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
    @Nullable  List<@BroadcasterValidation String> broadcasters,
    @Nullable ISO_639_Code productionCountry,
    @Nullable Short productionYear,
    @Nullable Integer episodeNumber,
    @Nullable Integer seasonNumber,
    @Nullable List<@Valid CreditsType> castAndCrew,
    @Nullable RelationType relations,
    @Nullable List<@Valid AvailabilityType> availability,
    @Nullable String metadataSource
) {
}
