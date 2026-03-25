package nl.vpro.domain.npo.wonvpp;

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
            case episode -> mapToBroadcast(entry);
            case season ->  mapToSeason(entry);
            case series -> mapToSeries(entry);
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
            .mainTitle(entry.title());
    }



}
