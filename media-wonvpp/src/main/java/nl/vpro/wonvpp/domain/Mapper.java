package nl.vpro.wonvpp.domain;

import java.util.List;

import nl.vpro.domain.media.*;

import static nl.vpro.domain.media.MediaBuilder.*;

public class Mapper {


    public static MediaTable mapToPoms(List<CatalogEntry> entries) {
        MediaTable table = new MediaTable();
        for (CatalogEntry entry : entries) {
            table.add(mapToPoms(entry));
        }
        return table;
    }

    public static MediaObject mapToPoms(CatalogEntry entry) {
        return switch (entry.contentType()) {
            case ContentTypeEnum.episode -> mapToBroadcast(entry);
            case ContentTypeEnum.season ->  mapToSeason(entry);
            case ContentTypeEnum.serie -> mapToSeries(entry);
        };
    }
    protected static Program mapToBroadcast(CatalogEntry entry) {
        return map(entry, broadcast())
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
            .ageRating(entry.rating() == null ? null : AgeRating.xmlValueOf(entry.rating().age()))
            //.contentRatings(entry.rating().advisories())
            .mainTitle(entry.title());
    }



}
