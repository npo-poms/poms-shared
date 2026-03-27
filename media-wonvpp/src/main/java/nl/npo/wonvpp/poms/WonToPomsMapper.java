package nl.npo.wonvpp.poms;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import nl.npo.wonvpp.domain.*;
import nl.vpro.domain.media.*;
import nl.vpro.domain.subtitles.SubtitlesType;

import static nl.vpro.domain.media.MediaBuilder.*;

@Log4j2
public class WonToPomsMapper {

    public static MediaTable mapToPoms(InputStream entries) throws IOException {
        return mapToPoms(Utils.unmarshal(entries));
    }

    public static MediaTable mapToPoms(List<CatalogEntry> entries) {
        MediaTable table = new MediaTable();
        for (CatalogEntry entry : entries) {
            table.add(mapToPoms(entry));
        }
        return table;
    }

    public static MediaObject mapToPoms(CatalogEntry entry) {
        return switch (entry.contentType()) {
            case episode -> mapToBroadcast(entry);
            case season ->  mapToSeason(entry);
            case serie -> mapToSeries(entry);
        };
    }
    protected static Program mapToBroadcast(CatalogEntry entry) {
        return map(entry, broadcast())
            .vodEvent(entry.prid(), entry.publicationTimestamp())
            .plannedAvailability()

            .build();

    }
    protected static Group mapToSeason(CatalogEntry entry) {
        return map(entry, season())
            .build();
    }
    protected static Group mapToSeries(CatalogEntry entry) {
        return map(entry, series())
            .build();
    }

    protected static <B extends MediaBuilder<B, T>, T extends MediaObject> B map(CatalogEntry entry, B builder) {


        return builder
            .mid(entry.prid())
            .avType(AVType.valueOf(entry.mediaType().name().toUpperCase()))
            .broadcasters(entry.broadcasters() == null ? new String[0] : entry.broadcasters().toArray(new String[0]))
            .crids("crid://" + entry.metadataSource() + "/" +  entry.prid())
            .ageRating(mapToRating(entry.rating()))
            .contentRatings(mapToRatings(entry))
            .mainTitle(entry.title())
            .dubbed(entry.isDubbed())
            .availableSubtitles(entry.captions() == null ? new AvailableSubtitles[0] :
                entry.captions().stream().map(WonToPomsMapper::mapToAvailableSubtitles).toArray(AvailableSubtitles[]::new))
            ;
    }

    protected static AgeRating mapToRating(RatingType rating) {
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
    protected static ContentRating[] mapToRatings(CatalogEntry entry) {
        if (entry.rating() == null || entry.rating().advisories() == null) {
            return new ContentRating[0];
        }
        return entry.rating().advisories().stream().flatMap(m -> m.entrySet().stream())
            .map(e -> mapToRating(e.getKey())).toArray(ContentRating[]::new);
    }


    protected static AvailableSubtitles mapToAvailableSubtitles(CaptionType captionType) {
        if (captionType.supplemental() != null && captionType.supplemental()) {
            log.warn("{}", captionType);
        }
        return new AvailableSubtitles(
            captionType.language(),
            captionType.closed() != null && captionType.closed()? SubtitlesType.CAPTION : SubtitlesType.TRANSLATION);

    }



}
