package nl.npo.wonvpp.poms;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.*;

import com.google.common.cache.*;

import nl.npo.wonvpp.domain.*;
import nl.vpro.domain.classification.ClassificationService;
import nl.vpro.domain.classification.Term;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;

import static nl.vpro.domain.media.MediaBuilder.*;

/**
 * See <a href="https://github.com/npo-poms/poms-shared/tree/main/media-wonvpp">README for this module</a>
 * @since 8.13
 */
@Log4j2
public class WonToPomsMapper {

    Locale nl_vpp = Locale.of("nl", "", "vpp");

    /**
     * For ownable fields, this owner type is assigned.
     */
    OwnerType owner = OwnerType.AUTHORITY;

    /**
     * This is used to map 'WON' ids to POMS ids.
     */
    private final BroadcasterService broadcasterService;

    /**
     * To match the primary/secondary string to actual term ids as are leading in POMS.
     * @see MediaClassificationService
     */
    private final ClassificationService classificationService;

    /**
     * Ad hoc logic to properly try to map genre?
     */
    private final LoadingCache<GenreType, Genre> genreCache = CacheBuilder.newBuilder()
        .build(new CacheLoader<>() {
            @Override
            public @NonNull Genre load(final @NonNull GenreType key) {
                var primary = key.primary();
                var secondary = key.secondary();
                Term primaryTerm = classificationService.values().stream()
                    .filter(t -> t.depth() == 4)
                    .filter(k -> k.getName(nl_vpp).equals(primary)).findFirst().orElseThrow();

                final Optional<Term> secondaryTerm = classificationService.values().stream()
                        .filter(t -> primaryTerm.equals(t.getParent()))
                        .filter(k -> k.getName(nl_vpp).equals(secondary))
                        .findFirst();


                if (secondaryTerm.isPresent()) {
                    return Genre.of(secondaryTerm.get());
                } else {

                    if (StringUtils.isNotEmpty(secondary)) {
                        if (! "Overig".equalsIgnoreCase(secondary)) {
                            throw new NoSuchElementException("No such term " + secondary + " for " + primaryTerm);
                        }
                    }
                }
                return Genre.of(primaryTerm);
            }
        });


    Clock clock = Clock.systemUTC();

    @Inject
    public WonToPomsMapper(BroadcasterService broadcasterService,
                           ClassificationService classificationService,
                           @Named("won.to.poms.owner") OwnerType ownerType) {
        this.broadcasterService = broadcasterService;
        this.classificationService = classificationService;
        this.owner = ownerType;
    }

    public @NonNull MediaTable mapToPoms(@NonNull InputStream entries) throws IOException {
        return mapToPoms(Utils.unmarshal(entries));
    }

    public @NonNull MediaTable mapToPoms(@NonNull List<@NonNull CatalogEntry> entries) {
        MediaTable table = new MediaTable();
        table.setSource("WONVPP");
        for (CatalogEntry entry : entries) {
            table.add(mapToPoms(entry));
        }
        table.setScheduleIfNeeded();
        LocalDate localDate = Schedule.guideDay(table.getSchedule().getStart());
        table.getSchedule().setGuideDate(localDate);
        return table;
    }

    public @NonNull MediaObject mapToPoms(@NonNull CatalogEntry entry) {
        return switch (entry.contentType()) {
            case episode -> mapToBroadcast(entry);
            case season  -> mapToSeason(entry);
            case serie   -> mapToSeries(entry);
        };
    }
    protected @NonNull Program mapToBroadcast(@NonNull CatalogEntry entry) {
        assert StringUtils.isEmpty(entry.seasonNumber());
        return map(entry, broadcast())
            .vodEvent(entry.prid(),
                Optional.ofNullable(entry.publicationTimestamp()).orElse(Instant.now())
                    .plusSeconds(60).truncatedTo(ChronoUnit.MINUTES) // round to next minute
                )
            .plannedAvailability(Authority.SYSTEM, entry.publicationTimestamp())
            .episodeOf(entry.relations() != null && entry.relations().season() != null ? toMid(entry.relations().season()) : null,
                entry.episodeNumber())
            .build();

    }
    protected Group mapToSeason(CatalogEntry entry) {
        Integer seasonNumber = null;
        if (entry.seasonNumber() != null) {
            try {
                seasonNumber = Integer.parseInt(entry.seasonNumber());
            } catch (NumberFormatException e) {
                log.warn("Could not parse season number {} for entry {}, skipping season number", entry.seasonNumber(), entry.prid());
            }
        }
        return map(entry, season())

            .memberOf(entry.relations() != null && entry.relations().series() != null ? toMid(entry.relations().series()) : null, seasonNumber)
            .build();
    }

    protected @NonNull Group mapToSeries(@NonNull CatalogEntry entry) {
        assert StringUtils.isEmpty(entry.seasonNumber());
        return map(entry, series())
            .build();
    }

    protected @Nullable String toMid(@Nullable PridReference pridReference) {
        if (pridReference == null) {
            return null;
        }
        if (StringUtils.isEmpty(pridReference.prid())) {
            return null;
        }
        return pridReference.prid();
    }

    protected <B extends MediaBuilder<B, T>, T extends MediaObject> @NonNull B map(@NonNull CatalogEntry entry, @NonNull B builder) {
        return builder
            .mid(entry.prid())
            .creationDate(clock.instant())
            .avType(AVType.valueOf(entry.mediaType().name().toUpperCase()))
            .broadcasters(entry.broadcasters() == null ? Collections.emptyList() :
                entry.broadcasters().stream()
                .map(this::mapToBroadcaster)
                .toList()
            )
            .crids("crid://" + entry.metadataSource() + "/" +  entry.prid())
            .ageRating(mapToRating(entry.rating()))
            .contentRatings(mapToRatings(entry))
            .mainTitle(entry.title(), owner)
            .originalTitle(entry.originalTitle(), owner)
            .mainDescription(mainDescription(entry.synopsis()), owner)
            .description(shortDescription(entry.synopsis()), TextualType.SHORT, owner)
            .subTitle(entry.displayTitle(), owner)
            .languages(entry.languages() == null ?
                new UsedLanguage[0] :
                entry.languages().stream()
                .map(this::mapToLanguageCode)
                .filter(Objects::nonNull) // sloppyness
                .toArray(UsedLanguage[]::new)
            )
            .countries(entry.productionCountry())
            .releaseYear(entry.productionYear())
            .credits(mapToCredits(entry.castAndCrew()).toArray(new Credits[0]))
            .genres(entry.genre() == null ? new Genre[0] : new Genre[] {mapToGenre(entry.genre())})
            .dubbed(entry.isDubbed())
            .availableSubtitles(entry.captions() == null ? new AvailableSubtitles[0] :
                entry.captions().stream()
                .map(this::mapToAvailableSubtitles)
                .toArray(AvailableSubtitles[]::new)
            )
            ;
    }

    protected @Nullable String mainDescription(@Nullable SynopsisType synopsis) {
        if (synopsis == null) {
            return null;
        }
        return StringUtils.isNotEmpty(synopsis.longValue()) ? synopsis.longValue() : null;
    }
    protected  @Nullable String shortDescription(@Nullable SynopsisType synopsis) {
        if (synopsis == null) {
            return null;
        }
        return StringUtils.isNotEmpty(synopsis.shortValue()) ? synopsis.shortValue() : null;
    }

    protected Broadcaster mapToBroadcaster(String broadcaster) {
        return broadcasterService
            .findFor(BroadcasterService.IdType.WON, broadcaster)
            .orElseThrow(() -> new IllegalArgumentException("Broadcaster: " + broadcaster + " not found for WON in " + broadcasterService));
    }

    protected @NonNull List<Credits> mapToCredits( @Nullable List<@NonNull CreditsType> castAndCrew) {
        if (castAndCrew == null) {
            return Collections.emptyList();
        }
        List<Credits> credits = new ArrayList<>();
        for (CreditsType creditsType : castAndCrew) {
            PersonType person = creditsType.person();
            Person c  = switch (creditsType.function()) {
                case Presenter -> new Person(person.givenName(), person.familyName(), RoleType.PRESENTER);
                case Director -> new Person(person.givenName(), person.familyName(), RoleType.DIRECTOR);
                case Actor -> new Person(person.givenName(), person.familyName(), RoleType.ACTOR);
                case Scriptwriter -> new Person(person.givenName(), person.familyName(), RoleType.SCRIPTWRITER);
                case Commentator -> new Person(person.givenName(), person.familyName(), RoleType.COMMENTATOR);
                case Guest -> new Person(person.givenName(), person.familyName(), RoleType.GUEST);
            };
            c.setExternalId("whatson:" + person.id());
            credits.add(c);

        }
        return credits;
    }
    protected @PolyNull AgeRating mapToRating(@PolyNull RatingType rating) {
        if (rating == null) {
            return null;
        }
        String ageRating = rating.age();
        if ("AL".equalsIgnoreCase(ageRating)) {
            return AgeRating.ALL;
        }
        return AgeRating.xmlValueOf(ageRating);
    }

    protected @Nullable UsedLanguage mapToLanguageCode(@Nullable LanguageType languageType) {
        if (languageType == null || languageType.language() == null) {
            return null;
        }
        Locale locale = languageType.language();
        UsedLanguage.Usage usage = UsedLanguage.Usage.valueOf(languageType.usage() == null ? "AUDIODESCRIPTION" : languageType.usage().toUpperCase());
        return new UsedLanguage(locale, usage);
    }

    protected static @NonNull ContentRating mapToRating(@NonNull AdvisoryType type) {
        return ContentRating.valueOf(type.name().charAt(0));
    }

    protected  @NonNull ContentRating[] mapToRatings(@NonNull CatalogEntry entry) {
        if (entry.rating() == null || entry.rating().advisories() == null) {
            return new ContentRating[0];
        }
        return entry.rating().advisories().stream().flatMap(m -> m.entrySet().stream())
            .map(e -> mapToRating(e.getKey())).toArray(ContentRating[]::new);
    }


    protected  @NonNull AvailableSubtitles mapToAvailableSubtitles(@NonNull CaptionType captionType) {
        if (captionType.supplemental() != null && captionType.supplemental()) {
            if (captionType.closed() != null && captionType.closed()) {
                log.debug("I guess closed captions are 'supplemental', since they just represent the audio");
            } else {
                log.warn("Don't know what to do with supplemental {}", captionType);
            }
        }
        return new AvailableSubtitles(
            captionType.language(),
            captionType.closed() != null && captionType.closed()? SubtitlesType.CAPTION : SubtitlesType.TRANSLATION);
    }

    protected Genre mapToGenre(GenreType genre) {
        return genreCache.getUnchecked(genre);
    }

}
