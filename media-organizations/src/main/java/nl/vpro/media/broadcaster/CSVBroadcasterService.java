/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.broadcaster;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;
import nl.vpro.util.URLResource;

/**
 * @author michiel
 * @since 8.17
 */
public class CSVBroadcasterService implements BroadcasterService {

    private final URLResource<Map<String, Broadcaster>> broadcasterResource;


    public CSVBroadcasterService() {
        this("classpath:/broadcasters.csv");
    }


    @Inject
    public CSVBroadcasterService(
        @Named("broadcasters.repository.location.csv") String configFile) {
        URI uri = URI.create(configFile);
        this.broadcasterResource = getURLResource(configFile);
    }

    protected URLResource<Map<String, Broadcaster>> getURLResource(String configFile) {
        URLResource<Map<String, Broadcaster>> result = new URLResource<>(URI.create(configFile),
            this::fillFromCSV)
            .setMinAge(Duration.of(1, ChronoUnit.HOURS))
            .setAsync(false);
        result.setConnectTimeout(Duration.ofSeconds(5));
        result.setReadTimeout(Duration.ofSeconds(10));
        return result;
    }

    @Override
    public Broadcaster find(String id) {
        return broadcasterResource.get().get(id);
    }

    @Override
    public List<Broadcaster> findAll() {
        return new ArrayList<>(broadcasterResource.get().values());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + broadcasterResource.getUrl() + "]";
    }
    protected Map<String, Broadcaster> fillFromCSV(InputStream inputStream) {
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        Map<String, Broadcaster> result = new TreeMap<>();
        try (MappingIterator<Map<String, String>> rows = new CsvMapper()
            .readerFor(Map.class)
            .with(schema)
            .readValues(inputStream)) {
            while (rows.hasNextValue()) {
                Map<String, String> row = rows.nextValue();
                Broadcaster broadcaster = Broadcaster.builder()
                    .id(row.get("id"))
                    .displayName(row.get("displayName"))
                    .whatsOnId(row.get("whatsOnId"))
                    .neboId(row.get("neboId"))
                    .misId(row.get("misId"))
                    .domain(row.get("domain"))
                    .start(parseDate(row.get("start")))
                    .stop(parseDate(row.get("stop")))
                    .build();
                result.put(broadcaster.getId(), broadcaster);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not parse broadcaster CSV", e);
        }
        return result;
    }

    private static LocalDate parseDate(String value) {
        return value == null || value.isBlank() ? null : LocalDate.parse(value);
    }

}
