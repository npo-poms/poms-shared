package nl.npo.wonvpp.poms;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.time.LocalDate;
import java.util.*;

import jakarta.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import nl.npo.wonvpp.domain.*;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;

import static nl.vpro.domain.media.MediaBuilder.*;

@Log4j2
public class WonToPomsMapper {

    static final OwnerType OWNER = OwnerType.MIS;

    private final BroadcasterService broadcasterService;

    Clock clock = Clock.systemUTC();

    @Inject
    public WonToPomsMapper(BroadcasterService broadcasterService) {
        this.broadcasterService = broadcasterService;
    }

    public MediaTable mapToPoms(InputStream entries) throws IOException {
        return mapToPoms(Utils.unmarshal(entries));
    }

    public MediaTable mapToPoms(List<CatalogEntry> entries) {
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

    public MediaObject mapToPoms(CatalogEntry entry) {
        return switch (entry.contentType()) {
            case episode -> mapToBroadcast(entry);
            case season ->  mapToSeason(entry);
            case serie -> mapToSeries(entry);
        };
    }
    protected Program mapToBroadcast(CatalogEntry entry) {
        return map(entry, broadcast())
            .vodEvent(entry.prid(), entry.publicationTimestamp())
            .plannedAvailability(Authority.SYSTEM)
            .episodeOf(entry.relations() != null && entry.relations().season() != null ? toMid(entry.relations().season()) : null,
                entry.episodeNumber())
            .build();

    }
    protected Group mapToSeason(CatalogEntry entry) {
        return map(entry, season())
            .memberOf(entry.relations() != null && entry.relations().series() != null ? toMid(entry.relations().series()) : null)
            .build();
    }
    protected Group mapToSeries(CatalogEntry entry) {
        return map(entry, series())
            .build();
    }

    protected String toMid(PridReference pridReference) {
        if (pridReference == null) {
            return null;
        }
        if (StringUtils.isEmpty(pridReference.prid())) {
            return null;
        }
        return pridReference.prid();
    }

    protected <B extends MediaBuilder<B, T>, T extends MediaObject> B map(CatalogEntry entry, B builder) {
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
            .mainTitle(entry.title(), OWNER)
            .originalTitle(entry.originalTitle(), OWNER)
            .subTitle(entry.displayTitle(), OWNER)
            .languages(entry.languages() == null ? new UsedLanguage[0] :
                entry.languages().stream()
                .map(this::mapToLanguageCode)
                .filter(Objects::nonNull) // sloppyness
                .toArray(UsedLanguage[]::new))
            .releaseYear(entry.productionYear())
            .credits(mapToCredits(entry.castAndCrew()).toArray(new Credits[0]))
            //.genres(entry.relations() == null ? new String[0] : mapToGenres(entry.relations().toArray(new String[0]))
            .dubbed(entry.isDubbed())
            .availableSubtitles(entry.captions() == null ? new AvailableSubtitles[0] :
                entry.captions().stream()
                .map(this::mapToAvailableSubtitles)
                .toArray(AvailableSubtitles[]::new))
            ;
    }
    protected Broadcaster mapToBroadcaster(String broadcaster) {
        return broadcasterService.findFor(BroadcasterService.IdType.WON, broadcaster).orElseThrow(() -> new IllegalArgumentException("Broadcaster: " + broadcaster + " not found for WON in " + broadcasterService));

    }

    protected List<Credits> mapToCredits(List<CreditsType> castAndCrew) {
        if (castAndCrew == null) {
            return Collections.emptyList();
        }
        List<Credits> credits = new ArrayList<>();
        for (CreditsType creditsType : castAndCrew) {
            PersonType person = creditsType.person();
            Person c  = switch (creditsType.function()) {
                case Director -> new Person(person.givenName(), person.familyName(), RoleType.DIRECTOR);
                case Actor -> new Person(person.givenName(), person.familyName(), RoleType.ACTOR);
            };
            c.setExternalId("won:" + person.id());
            credits.add(c);

        }
        return credits;
    }
    protected  AgeRating mapToRating(RatingType rating) {
        if (rating == null) {
            return null;
        }
        String ageRating = rating.age();
        if ("AL".equalsIgnoreCase(ageRating)) {
            return AgeRating.ALL;
        }
        return AgeRating.xmlValueOf(ageRating);
    }

    protected UsedLanguage  mapToLanguageCode(LanguageType languageType) {
        if (languageType == null || languageType.language() == null) {
            return null;
        }
        Locale locale = languageType.language();
        UsedLanguage.Usage usage = UsedLanguage.Usage.valueOf(languageType.usage() == null ? "AUDIODESCRIPTION" : languageType.usage().toUpperCase());
        return new UsedLanguage(locale, usage);

    }

    protected static ContentRating mapToRating(AdvisoryType type) {
        return ContentRating.valueOf(type.name().charAt(0));
    }
    protected  ContentRating[] mapToRatings(CatalogEntry entry) {
        if (entry.rating() == null || entry.rating().advisories() == null) {
            return new ContentRating[0];
        }
        return entry.rating().advisories().stream().flatMap(m -> m.entrySet().stream())
            .map(e -> mapToRating(e.getKey())).toArray(ContentRating[]::new);
    }


    protected  AvailableSubtitles mapToAvailableSubtitles(CaptionType captionType) {
        if (captionType.supplemental() != null && captionType.supplemental()) {
            log.warn("{}", captionType);
        }
        return new AvailableSubtitles(
            captionType.language(),
            captionType.closed() != null && captionType.closed()? SubtitlesType.CAPTION : SubtitlesType.TRANSLATION);

    }



}
