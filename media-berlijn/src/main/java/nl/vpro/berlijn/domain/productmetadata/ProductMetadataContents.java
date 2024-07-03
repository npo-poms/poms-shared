package nl.vpro.berlijn.domain.productmetadata;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.meeuw.i18n.countries.Country;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @param season  A number indicating the season
 * @param mediaType Whether it is audio or video
 */

@JsonIgnoreProperties({
    "s3FilePath"
})
public record ProductMetadataContents(
    int version,
    ContentType contentType,
    String prid,

    LocalDate date,

    String title,
    String displayTitle,
    Synopsis synopsis,

    Instant lastUpdated,
    String metadataSource,
    int totalEpisodes,

    List<Genre> genres,

    ProductRelations relations,

    Instant created,
    Instant publicationTimestamp,


    Country productionCountry,
    List<Language> languages,
    List<CaptionLanguage> captionLanguages,
    List<SignLanguage> signLanguages,

    MediaType mediaType,
    List<String> broadcasters,

    Integer productionYear,
    @Nullable Integer episodeNumber,

    List<Credits> castAndCrew,
    ContentRating contentRating,

    String season
) {


}
