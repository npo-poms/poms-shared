package nl.npo.wonvpp.poms;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import jakarta.inject.Inject;

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

    @Inject
    public WonToPomsMapper(BroadcasterService broadcasterService) {
        this.broadcasterService = broadcasterService;
    }

    public MediaTable mapToPoms(InputStream entries) throws IOException {
        return mapToPoms(Utils.unmarshal(entries));
    }

    public MediaTable mapToPoms(List<CatalogEntry> entries) {
        MediaTable table = new MediaTable();
        Schedule schedule = new Schedule();
        table.setSchedule(schedule);
        table.setSource("WONVPP");
        schedule.setChannel(Channel.NVOD);
        for (CatalogEntry entry : entries) {
            table.add(mapToPoms(entry));
        }
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
            .plannedAvailability()

            .build();

    }
    protected Group mapToSeason(CatalogEntry entry) {
        return map(entry, season())
            .build();
    }
    protected Group mapToSeries(CatalogEntry entry) {
        return map(entry, series())
            .build();
    }

    protected <B extends MediaBuilder<B, T>, T extends MediaObject> B map(CatalogEntry entry, B builder) {


        return builder
            .mid(entry.prid())
            .avType(AVType.valueOf(entry.mediaType().name().toUpperCase()))
            .broadcasters(entry.broadcasters() == null ? Collections.emptyList() : entry.broadcasters().stream().map(this::mapToBroadcaster).toList())
            .crids("crid://" + entry.metadataSource() + "/" +  entry.prid())
            .ageRating(mapToRating(entry.rating()))
            .contentRatings(mapToRatings(entry))
            .mainTitle(entry.title(), OWNER)
            .originalTitle(entry.originalTitle(), OWNER)
            .subTitle(entry.displayTitle(), OWNER)
            //.languages(entry.languages() == null ? new Locale[0] : entry.languages().stream().map(LanguageType::language).toArray(LanguageCode))
            .releaseYear(entry.productionYear())
            .credits(mapToCredits(entry.castAndCrew()).toArray(new Credits[0]))
            .dubbed(entry.isDubbed())
            .availableSubtitles(entry.captions() == null ? new AvailableSubtitles[0] :
                entry.captions().stream().map(this::mapToAvailableSubtitles).toArray(AvailableSubtitles[]::new))
            ;
    }
    protected Broadcaster mapToBroadcaster(String broadcaster) {
        return broadcasterService.findFor(BroadcasterService.IdType.WON, broadcaster).orElseThrow();

    }

    protected List<Credits> mapToCredits(List<CreditsType> castAndCrew) {
        if (castAndCrew == null) {
            return Collections.emptyList();
        }
        List<Credits> credits = new ArrayList<>();
        for (CreditsType creditsType : castAndCrew) {
            PersonType person = creditsType.person();
            switch (creditsType.function()) {
                case Director -> new Person(person.givenName(), person.familyName(), RoleType.DIRECTOR);
                case Actor -> new Person(person.givenName(), person.familyName(), RoleType.ACTOR);
            }
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
