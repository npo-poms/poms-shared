/*
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.beeldengeluid.gtaa;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Context;
import javax.xml.bind.JAXB;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.client.*;

import nl.vpro.domain.gtaa.*;
import nl.vpro.openarchives.oai.*;
import nl.vpro.util.BatchedReceiver;
import nl.vpro.util.CountedIterator;
import nl.vpro.w3.rdf.Description;
import nl.vpro.w3.rdf.RDF;

import static org.springframework.http.HttpStatus.CREATED;

/**
 * See http://editor.openskos.org/apidoc/index.html ?
 *
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
@Slf4j
public class OpenskosRepository implements GTAARepository {

    public static final String CONFIG_FILE = "openskosrepository.properties";

    public static final ZoneId ZONE_ID = ZoneId.of("Europe/Amsterdam");

    private final DateTimeFormatter isoInstant = DateTimeFormatter.ISO_INSTANT;

    private final RestTemplate template;

    private final String gtaaUrl;
    private final String gtaaKey;

    @Value("${gtaa.personsSpec}")
    @Getter
    @Setter
    private String personsSpec;

    @Value("${gtaa.geolocationsSpec}")
    @Getter
    @Setter
    private String geoLocationsSpec;

    @Value("${gtaa.useXLLabels}")
    @Getter
    @Setter
    private boolean useXLLabels;

    @Value("${gtaa.tenant}")
    @Getter
    @Setter
    private String tenant;

    @Value("${gtaa.retries}")
    @Getter
    @Setter
    private int retries;

    public OpenskosRepository(
        @Value("${gtaa.baseUrl}")
        @NonNull String baseUrl,
        @Value("${gtaa.key}")
        @NonNull String key) {
        this(baseUrl, key, null, null, null, true, null, 1);
    }

    @lombok.Builder(builderClassName = "Builder")
    private OpenskosRepository(
        @NonNull String baseUrl,
        @NonNull String key,
        RestTemplate template,
        String personsSpec,
        String geoLocationsSpec,
        boolean useXLLabels,
        @NonNull String tenant,
        int retries
        ) {
        this.gtaaUrl = baseUrl;
        this.gtaaKey = key;
        this.template = createTemplateIfNull(template);
        this.tenant = tenant;
        this.personsSpec = personsSpec;
        this.geoLocationsSpec = geoLocationsSpec;
        this.useXLLabels = useXLLabels;
        this.retries = retries;

    }

    private static RestTemplate createTemplateIfNull(RestTemplate template) {
        if (template == null) {
            MarshallingHttpMessageConverter marshallingHttpMessageConverter = new MarshallingHttpMessageConverter();
            Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
            jaxb2Marshaller.setPackagesToScan(
                "nl.vpro.beeldengeluid.gtaa",
                "nl.vpro.w3.rdf",
                "nl.vpro.openarchives.oai"
            );

            try {
                jaxb2Marshaller.afterPropertiesSet();
            } catch (Exception ex) {
                /* Ignore */
            }
            marshallingHttpMessageConverter.setMarshaller(jaxb2Marshaller);
            marshallingHttpMessageConverter.setUnmarshaller(jaxb2Marshaller);

            template = new RestTemplate();
            template.setMessageConverters(Collections.singletonList(marshallingHttpMessageConverter));
        }
        return template;
    }

    @PostConstruct
    public void init() {
        log.info("Communicating with {} (personSpec: {}, geolocationsSpec: {}), useXLLabels: {})",
            gtaaUrl,
            personsSpec,
            geoLocationsSpec,
            useXLLabels
        );
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T extends GTAAConcept, S extends GTAANewConcept> T submit(@NonNull S thesaurusObject, @NonNull String creator) {
        final Description description = submit(
            thesaurusObject.getName(),
            thesaurusObject.getScopeNotesAsLabel(),
            creator,
            thesaurusObject.getObjectType()
        );
        return (T) GTAAConcepts.toConcept(description);

    }


    @SuppressWarnings("StringConcatenationInLoop")
    private Description submit(@NonNull String prefLabel, @NonNull  List<Label> notes, @NonNull  String creator, @NonNull  Scheme scheme) {

        ResponseEntity<RDF> response = null;
        RuntimeException rte = null;
        try {
            response = postRDF(prefLabel, notes, creator, scheme);
        } catch (GTAAConflict ex) {
            String postFix = ".";
            while (postFix.length() <= retries) {
                try {
                    // Retry the submit by adding a "." after the label name when a 409 Conflict is
                    // returned
                    // See MSE-3366
                    log.warn("Retrying label on 409 Conflict: \"{}\"", prefLabel + postFix);
                    response = postRDF(prefLabel + postFix, notes, creator, scheme);
                    break;
                } catch (GTAAConflict ex2) {
                    /* The version with "." already exists too */
                    log.debug("Duplicate label: {}", prefLabel);
                    ex = ex2;
                }
                postFix += ".";
            }
            if (response == null) {
                throw ex;
            }
        } catch (NullPointerException npe) {
            log.error(npe.getClass().getName() + " " + npe.getMessage(), npe);
            rte = npe;
            response = null;
        } catch (RuntimeException rt) {
            log.error(rt.getClass().getName() + " " + rt.getMessage());
            rte = rt;
            response = null;
        }

        if (response != null
            && response.getBody() != null
            && response.getBody().getDescriptions() != null && ! response.getBody().getDescriptions().isEmpty()) {
            if (response.getStatusCode() == CREATED) {
                return response.getBody().getDescriptions().get(0);
            } else {
                // Is this possible at all?
                throw new RuntimeException("Status " + response.getStatusCode() + " for prefLabel: " + prefLabel, rte);
            }
        } else {
            throw new RuntimeException("For prefLabel: " + prefLabel, rte);
        }

    }

    @Override
    public CountedIterator<Record> getPersonUpdates(@Context Instant from, @Context Instant to) {
        return getUpdates(from, to, personsSpec);
    }

    @Override
    public CountedIterator<Record> getGeoLocationsUpdates(@Context Instant from, @Context Instant to) {
        return getUpdates(from, to, geoLocationsSpec);
    }

    @Override
    public CountedIterator<Record> getAllUpdates(Instant from, Instant until) {
        return getUpdates(from, until, null);
    }

    private CountedIterator<Record> getUpdates(Instant from, Instant until, String spec) {

        final AtomicLong totalSize = new AtomicLong(-1L);
        Supplier<Iterator<Record>> getter = new Supplier<Iterator<Record>>() {
            ListRecord listRecord = null;

            @Override
            public Iterator<Record> get() {
                if (listRecord == null) {
                    listRecord = getListRecord(from, until, spec);
                    if (listRecord == null) {
                        log.debug("Found no listrecord for {} - {}", from, until);
                        if (totalSize.get() < 0) {
                            totalSize.set(0L);
                        }
                        return Collections.emptyIterator();
                    }
                } else {
                    ResumptionToken token = listRecord.getResumptionToken();
                    if (token != null && StringUtils.isNotEmpty(token.getValue())) {
                        listRecord = getUpdates(token);
                        if (listRecord == null) {
                            log.warn("Found no listrecord from token {}", token);
                            return Collections.emptyIterator();
                        }
                    } else {
                        listRecord = null;
                        return Collections.emptyIterator();
                    }

                }

                if (totalSize.get() < 0) {
                    if (listRecord.getResumptionToken() != null
                            && listRecord.getResumptionToken().getCompleteListSize() != null) {
                        totalSize.set(listRecord.getResumptionToken().getCompleteListSize());
                    } else {
                        totalSize.set(0L);
                    }
                }
                return listRecord.getRecords().iterator();

            }
        };

        Iterator<Record> iterator = BatchedReceiver.<Record>builder().batchGetter(getter).build();

        return CountedIterator.of(totalSize, iterator);
    }

    ListRecord getListRecord(Instant from, Instant until, String type) {
        String set = "";
        if(type != null) {
            set = "&set=" + type;
        }
        String path = String.format("oai-pmh?verb=ListRecords&metadataPrefix=oai_rdf%s", set) + "&from="
                + isoInstant.format(from.truncatedTo(ChronoUnit.SECONDS)) + "&until="
                + isoInstant.format(until.truncatedTo(ChronoUnit.SECONDS));

        final OAI_PMH oai_pmh = getForPath(path, OAI_PMH.class);
        if (oai_pmh.getError() != null) {
            String code = oai_pmh.getError().getCode();
            if ("noRecordsMatch".equals(code)) {
                // Not really an error, is it.
                log.info("For {}{}: {}", gtaaUrl, path, oai_pmh.getError().getMessage());
            } else {
                throw new RuntimeException(
                        "For " + gtaaUrl + path + " " + code + ":" + oai_pmh.getError().getMessage());
            }
        }
        return oai_pmh.getListRecord();

    }

    ListRecord getUpdates(ResumptionToken resumptionToken) {
        final OAI_PMH oai_pmh = getForPath("oai-pmh?verb=ListRecords&resumptionToken=" + resumptionToken.getValue(),
                OAI_PMH.class);
        if (oai_pmh == null || oai_pmh.getListRecord() == null) {
            return ListRecord.empty();
        }

        return oai_pmh.getListRecord();
    }

    private ResponseEntity<RDF> postRDF(
        @NonNull String prefLabel,
        @NonNull List<Label> notes,
        @NonNull String creator,
        @NonNull Scheme scheme) {
        log.info("Submitting {} {} {} to {}", prefLabel, notes, creator, gtaaUrl);
        RDF rdf = new RDF();
        rdf.setDescriptions(
            Collections.singletonList(
                Description.builder()
                    .type(Types.SKOS_CONCEPT)
                    .tenant(tenant)
                    .creator(creator)
                    .prefLabelOrXL(useXLLabels, prefLabel, tenant)
                    .scopeNote(notes)
                    .dateSubmitted(Instant.now().atZone(ZONE_ID))
                    .inScheme(scheme.getUrl())
                    .build()));

        template.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(@NonNull ClientHttpResponse response) throws IOException {
                return !response.getStatusCode().is2xxSuccessful();
            }

            @Override
            public void handleError(@NonNull ClientHttpResponse response) throws IOException {
                StringWriter body = new StringWriter();
                IOUtils.copy(response.getBody(), body, StandardCharsets.UTF_8);
                switch (response.getStatusCode()) {
                case CONFLICT:
                    throw new GTAAConflict("Conflicting or duplicate label: " + prefLabel + ": " + body);
                case BAD_REQUEST:
                    if (body.toString().startsWith("The pref label already exists in that concept scheme")) {
                        throw new GTAAConflict(body.toString());
                    }
                default:
                    StringWriter writer = new StringWriter();
                    writer.append("Request:\n");
                    JAXB.marshal(rdf, writer);
                    writer.append("Response:\n");
                    writer.append(body.toString());
                    throw new RuntimeException("For " + gtaaUrl + " " +
                        response.getStatusCode() + " " + response.getStatusText() + " " + writer.toString());
                }
            }
        });

        // Beware parameter ordering is relevant
        return template.postForEntity(
                String.format("%s/api/concept?key=%s&collection=gtaa&autoGenerateIdentifiers=true&tenant=%s",
                        gtaaUrl ,gtaaKey, tenant),
                rdf, RDF.class);
    }


    /**
     * http://accept.openskos.beeldengeluid.nl.pictura-dp.nl/apidoc/index.html#api-FindConcept-FindConcepts
     */
    @Override
    public List<Description> findPersons(String input, Integer max) {
        if (max == null) {
            max = 50;
        }
        // String fields = "&fl=uuid,uri,prefLabel,altLabel,hiddenLabel,status";
        input = input.replaceAll("[\\-.,]+", " ");
        String query = "(status:(candidate OR approved) OR (status:not_compliant AND dc_creator:POMS)) " +
                "AND inScheme:\"" + Scheme.person.getUrl()  + "\" " +
                "AND (" + input + "*)";

        String path = "api/find-concepts?tenant=" + tenant + "&collection=gtaa&q=" + query + "&rows=" + max;
        return descriptions(getForPath(path, RDF.class));
    }

    protected <T> T getForPath(final String path, final Class<T> tClass) {
        String url = gtaaUrl + path;
        log.info("Calling gtaa {}", url);
        try {
            ResponseEntity<T> entity = template.getForEntity(url, tClass);
            return entity.getStatusCode().is2xxSuccessful() ? entity.getBody() : null;
        } catch (NullPointerException npe) {
            log.error("For GET {}: {}", url, npe.getMessage(), npe);
            throw npe;
        } catch (RuntimeException rt) {
            log.error("For GET {}: {}", url, rt.getMessage());
            throw rt;
        }
    }


    @Override
    public List<Description> findAnything(String input, Integer max) {
        if (max == null) {
            max = 50;
        }
        input = input.replaceAll("[\\-.,]+", " ");
        String query = String.format("(status:(candidate OR approved) " +
                "OR (status:not_compliant AND dc_creator:POMS)) " +
                "AND ( %s*)", input);

        String path = String.format("api/find-concepts?tenant=%s&collection=gtaa&q=%s&rows=%s", tenant, query, max);
        return descriptions(getForPath(path, RDF.class));
    }

    @Override
    public List<Description> findForSchemes(String input, Integer max, String... schemes) {
        if (max == null) {
            max = 50;
        }
        input = input.replaceAll("[\\-.,]+", " ");

        String query = String.format("(status:(candidate OR approved) " +
                "OR (status:not_compliant AND dc_creator:POMS)) " +
                 generateQueryByScheme(schemes) +
                "AND ( %s*)", input);

        String path = String.format("api/find-concepts?tenant=%s&collection=gtaa&q=%s&rows=%s",
            tenant, query, max);

        return descriptions(getForPath(path, RDF.class));


    }

    private static final Pattern NOT_FOUND = Pattern.compile(".*The requested resource .* was not found.*", Pattern.DOTALL);

    @Override
    public Optional<Description> retrieveConceptStatus(String id) {
        String url = gtaaUrl + "api/find-concepts?id=" + id;
        try {
            RDF rdf = template.getForObject(url, RDF.class);
            List<Description> descriptions = descriptions(rdf);
            return descriptions.stream().findFirst();
        } catch (HttpServerErrorException e) {
            switch(e.getStatusCode()) {
                case INTERNAL_SERVER_ERROR:
                    // It is idiotic that openskos issues an internal server error for what basicly is a 404
                    if(NOT_FOUND.matcher(e.getResponseBodyAsString()).matches()) {
                        return Optional.empty();
                    }
                default:
                    log.error("Unexpected error doing call to openskos for item id {}: {}: {}", id, url, e.getResponseBodyAsString(), e);
                    throw e;
            }
        }
    }


    private String generateQueryByScheme(String... schemeList) {
        Predicate<String> empty = s -> s.equals("");
        if (Arrays.stream(schemeList).allMatch(empty)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        sb.append("AND (");

        String operator = "";
        for (String axis : schemeList) {
            boolean not = false;
            if (axis.startsWith("!")) {
                not = true;
                axis = axis.substring(1);
            }
            sb.append(
                    String.format(
                        "%s %s inScheme:\"%s\" ",
                        operator,
                        not ? "NOT" : "",
                        Scheme.valueOf(axis).getUrl())
            );
            operator = "OR";
        }
        sb.append(")");

        return sb.toString();
    }


    private List<Description> descriptions(RDF rdf) {
        if (rdf == null || rdf.getDescriptions() == null) {
            return Collections.emptyList();
        }

        return rdf.getDescriptions();
    }

    @Override
    public String toString() {
        return super.toString() + " " + gtaaUrl;
    }

}
