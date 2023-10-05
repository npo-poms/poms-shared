/*
 * Copyright (C) 2015 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.beeldengeluid.gtaa;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXB;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import nl.vpro.domain.gtaa.*;
import nl.vpro.logging.LoggerOutputStream;
import nl.vpro.openarchives.oai.Record;
import nl.vpro.openarchives.oai.*;
import nl.vpro.util.BatchedReceiver;
import nl.vpro.util.CountedIterator;
import nl.vpro.w3.rdf.Description;
import nl.vpro.w3.rdf.RDF;

import static java.time.format.DateTimeFormatter.ISO_INSTANT;

/**
 * See <a href="http://editor.openskos.org/apidoc/index.html">okpenskos</a> ?
 *
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
@Slf4j
public class OpenskosRepository implements GTAARepository {

    public static final String CONFIG_FILE = "openskosrepository.properties";

    public static final ZoneId ZONE_ID = ZoneId.of("Europe/Amsterdam");

    static OpenskosRepository instance;

    public static OpenskosRepository getInstance() {
        if (instance == null) {
            throw new IllegalStateException("No openskos repository instance");
        }
        return instance;
    }

    private final RestTemplate template;

    private final String gtaaUrl;
    private final String gtaaKey;


    @Value("${gtaa.personsSpec:#{T(nl.vpro.domain.gtaa.Scheme).person.getSpec()}}")
    @Getter
    @Setter
    @NonNull
    private String personsSpec = Scheme.person.getSpec();

    @Value("${gtaa.geolocationsSpec:#{T(nl.vpro.domain.gtaa.Scheme).geographicname.getSpec()}}")
    @Getter
    @Setter
    @NonNull
    private String geoLocationsSpec = Scheme.geographicname.getSpec();

    @Value("${gtaa.tenant}")
    @Getter
    @Setter
    @Nullable
    private String tenant;

    @Value("${gtaa.retries}")
    @Getter
    @Setter
    private int retries;

    @Getter
    @Setter
    private String creator = "POMS";

    @Getter
    @Setter
    private String oai = "oai-pmh";


    private MeterRegistry meterRegistry;

    @Inject
    public OpenskosRepository(
        @Value("${gtaa.baseUrl}")
        @NonNull String baseUrl,
        @Value("${gtaa.key}")
        @NonNull String key,
        @Nullable MeterRegistry meterRegistry) {
        this(baseUrl, key, null, null, null, null, 1,
            meterRegistry);
    }

    @lombok.Builder(builderClassName = "Builder")
    private OpenskosRepository(
        @NonNull String baseUrl,
        @NonNull String key,
        @Nullable RestTemplate template,
        @Nullable String personsSpec,
        @Nullable String geoLocationsSpec,
        @Nullable String tenant,
        int retries,
        @Nullable MeterRegistry meterRegistry) {
        this.gtaaUrl = baseUrl;
        this.gtaaKey = key;
        this.template = createTemplateIfNull(template);
        this.tenant = tenant;
        this.personsSpec = StringUtils.isEmpty(personsSpec) ? Scheme.person.getSpec() : personsSpec;
        this.geoLocationsSpec = StringUtils.isEmpty(geoLocationsSpec) ? Scheme.geographicname.getSpec() : geoLocationsSpec;
        this.retries = retries;
        this.meterRegistry = meterRegistry == null ? new LoggingMeterRegistry(log::info) : meterRegistry;
        if (instance != null) {
            log.warn("There is already an openskos repository");
        }
        instance = this;

    }



    private void addErrorHandler() {
        template.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(@NonNull ClientHttpResponse response) throws IOException {
                final boolean hasError = ! response.getStatusCode().is2xxSuccessful();
                if (hasError) {
                    log.warn("{} has error: {}", template, response.getStatusCode());
                } else {
                    Post_RDF.remove();
                }
                return hasError;
            }

            @Override
            public void handleError(@NonNull ClientHttpResponse response) throws IOException {
                final StringWriter body = new StringWriter();
                IOUtils.copy(response.getBody(), body, StandardCharsets.UTF_8);
                final RDFPost postRdf = Post_RDF.get();
                try {
                    switch (response.getStatusCode()) {
                        case CONFLICT:
                            throw new GTAAConflict("Conflicting or duplicate label: " + postRdf.prefLabel + ": " + body);
                        case BAD_REQUEST:
                            if (body.toString().startsWith("The pref label already exists in that concept scheme")) {
                                throw new GTAAConflict(body.toString());
                            }
                        default:
                            final StringWriter writer = new StringWriter();
                            if (postRdf != null) {
                                writer.append("Request:\n");
                                JAXB.marshal(postRdf.rdf, writer);
                            }
                            writer.append("Response:\n");
                            writer.append(body.toString());
                            throw new GTAAError(
                                response.getRawStatusCode(),
                                body.toString(),
                                "For " + gtaaUrl + " " +
                                response.getStatusCode() + " " + response.getStatusText() + " " + writer);
                    }
                } finally {
                    Post_RDF.remove();
                }
            }
        });
    }

    private static RestTemplate createTemplateIfNull(@Nullable RestTemplate template) {
        if (template == null) {

            Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
            jaxb2Marshaller.setPackagesToScan(
                "nl.vpro.beeldengeluid.gtaa",
                "nl.vpro.w3.rdf",
                "nl.vpro.openarchives.oai"
            );

            try {
                jaxb2Marshaller.afterPropertiesSet();
            } catch (Exception ex) {
                log.warn(ex.getMessage());

            }
            DOMSourceUnmarshaller domSourceUnmarshaller = new DOMSourceUnmarshaller();

            MarshallingHttpMessageConverter rdfHttpMessageConverter = new MarshallingHttpMessageConverter();
            rdfHttpMessageConverter.setMarshaller(jaxb2Marshaller);
            rdfHttpMessageConverter.setUnmarshaller(jaxb2Marshaller);

            MarshallingHttpMessageConverter rdfToDomHttpMessageConverter = new MarshallingHttpMessageConverter();
            rdfToDomHttpMessageConverter.setMarshaller(jaxb2Marshaller);
            rdfToDomHttpMessageConverter.setUnmarshaller(domSourceUnmarshaller);

            template = new RestTemplate();

            DefaultUriBuilderFactory uriBuilder = new DefaultUriBuilderFactory();
            uriBuilder.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
            template.setUriTemplateHandler(uriBuilder);

            template.setMessageConverters(
                Arrays.asList(rdfHttpMessageConverter, rdfToDomHttpMessageConverter)
            );
        }
        return template;
    }

    @PostConstruct
    public void init() {
        log.info("Communicating with {} (personSpec: {}, geolocationsSpec: {})",
            gtaaUrl,
            personsSpec,
            geoLocationsSpec
        );
        addErrorHandler();
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
        return (T) GTAAConcepts.toConcept(description)
            .orElseThrow(() -> new IllegalStateException("Could not convert " + description));

    }


    @SuppressWarnings("StringConcatenationInLoop")
    private Description submit(@NonNull String prefLabel, @NonNull  List<Label> notes, @NonNull  String creator, @NonNull  Scheme scheme) {

        ResponseEntity<Source> response = null;
        RuntimeException rte = null;
        try {
            response = postRDF(prefLabel, notes, creator, scheme);
            meterRegistry.counter("gtaa.submit", "status", response.getStatusCode().toString()).increment();
        } catch (GTAAConflict ex) {
            meterRegistry.counter("gtaa.submit", "status", "CONFLICT").increment();
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
                    rte = ex2;
                }
                postFix += ".";
            }
            if (response == null) {
                throw ex;
            }
        } catch (NullPointerException npe) {
            log.error(npe.getClass().getName() + " " + npe.getMessage(), npe);
            rte = npe;
        } catch (RuntimeException rt) {
            log.error(rt.getClass().getName() + " " + rt.getMessage());
            rte = rt;
        }

        if (response != null && response.getBody() != null) {

            Source doc = response.getBody();
            logSource(doc);

            RDF rdf = JAXB.unmarshal(doc, RDF.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return rdf.getDescriptions().get(0);
            } else {
                // Is this possible at all?
                throw new RuntimeException("Status " + response.getStatusCode() + " for prefLabel: " + prefLabel, rte);
            }
        } else {
            throw new RuntimeException("For prefLabel: " + prefLabel, rte);
        }

    }

    private void logSource(Source doc) {
        if (log.isDebugEnabled()) {
            try {
                TransformerFactory factory = TransformerFactory.newInstance();
                factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                Transformer transformer = factory.newTransformer();
                Result result = new StreamResult(LoggerOutputStream.debug(log));
                transformer.transform(doc, result);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }

            log.debug("{}", doc);
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

    private CountedIterator<Record> getUpdates(@NonNull Instant from, @NonNull Instant until, @Nullable String spec) {

        final AtomicLong totalSize = new AtomicLong(-1L);
        Supplier<Iterator<Record>> getter = new Supplier<>() {
            @Nullable
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

    @Nullable
    ListRecord getListRecord(@NonNull Instant from, @NonNull Instant until, @Nullable String type) {
        String set = "";
        if(type != null) {
            set = "&set=" + type;
        }
        String path = String.format(oai + "?verb=ListRecords&metadataPrefix=oai_rdf%s", set) + "&from="
                + ISO_INSTANT.format(from.truncatedTo(ChronoUnit.SECONDS)) + "&until="
                + ISO_INSTANT.format(until.truncatedTo(ChronoUnit.SECONDS));

        final OAI_PMH oai_pmh = getForPath(path, OAI_PMH.class);
        if (oai_pmh != null) {
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
        } else {
            return null;
        }

    }

    ListRecord getUpdates(ResumptionToken resumptionToken) {
        final OAI_PMH oai_pmh = getForPath(oai + "?verb=ListRecords&resumptionToken=" + resumptionToken.getValue(),
                OAI_PMH.class);
        if (oai_pmh == null || oai_pmh.getListRecord() == null) {
            return ListRecord.empty();
        }

        return oai_pmh.getListRecord();
    }

    ThreadLocal<RDFPost> Post_RDF = ThreadLocal.withInitial(() -> null);


    protected static class RDFPost {
        final String prefLabel;
        final RDF rdf;

        public RDFPost(String prefLabel, RDF rdf) {
            this.prefLabel = prefLabel;
            this.rdf = rdf;
        }
    }

    @SneakyThrows
    private ResponseEntity<Source> postRDF(
        final  @NonNull String prefLabel,
        final @NonNull List<@NonNull Label> notes,
        final @NonNull String creator,
        final @NonNull Scheme scheme) {
        log.info("Submitting {} {} {} to {}", prefLabel, notes, creator, gtaaUrl);
        final RDF rdf = new RDF();
        rdf.setDescriptions(
            Collections.singletonList(
                Description.builder()
                    .type(Types.SKOS_CONCEPT)
                    .tenant(tenant)
                    .creator(creator)
                    .prefLabelOrXL(true, prefLabel, tenant)
                    .scopeNote(notes)
                    .dateSubmitted(Instant.now().atZone(ZONE_ID))
                    .inScheme(scheme.getUrl())
                    .build()));


        Post_RDF.set(new RDFPost(prefLabel, rdf));
        // Beware parameter ordering is relevant
        String encodedKey = Stream.of(gtaaKey.split(":", 2)).map(this::encode).collect(Collectors.joining(":"));
        //String encodedKey = encode(gtaaKey);

        return template.postForEntity(
            String.format("%s/api/concept?key=%s&collection=gtaa&autoGenerateIdentifiers=true&tenant=%s",
                gtaaUrl,
                encodedKey,
                encode(tenant)
            ),
            rdf, Source.class);
    }


    @SneakyThrows
    @Nullable
    private String encode(@Nullable String u) {
        return u == null ? null : URLEncoder.encode(u, StandardCharsets.US_ASCII);
    }

    /**
     * <a href="http://accept.openskos.beeldengeluid.nl.pictura-dp.nl/apidoc/index.html#api-FindConcept-FindConcepts">see at pictura</a>
     */
    @Override
    public List<Description> findPersons(String input, Integer max) {
        if (max == null) {
            max = 50;
        }
        // String fields = "&fl=uuid,uri,prefLabel,altLabel,hiddenLabel,status";
        input = input.replaceAll("[\\-.,]+", " ");
        String query = "(status:(candidate OR approved) OR (status:not_compliant AND dc_creator:" + creator + ")) " +
                "AND inScheme:\"" + Scheme.person.getUrl()  + "\" " +
                "AND (" + input + "*)";

        String path = "api/find-concepts?tenant=" + encode(tenant) + "&collection=gtaa&q=" + encode(query) + "&rows=" + max;
        return descriptions(getForPath(path, RDF.class));
    }

    @Nullable
    protected <T> T getForPath(final String path, final Class<T> tClass) {
        String url = gtaaUrl + path;
        log.info("Calling gtaa {}", url);
        meterRegistry.counter("gtaa.get", "path", path).increment();

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
        return findForSchemes(input, max, Arrays.stream(Scheme.values()).map(s -> new SchemeOrNot(s.getUrl(), false)).toArray(SchemeOrNot[]::new));
    }

    @Override
    public List<Description> findForSchemes(String input, Integer max, SchemeOrNot... schemes) {
        if (max == null) {
            max = 50;
        }
        input = input.replaceAll("[\\-.,]+", " ");

        String query = String.format("(status:(candidate OR approved) " +
                "OR (status:not_compliant AND dc_creator:" + creator + ")) " +
                 generateQueryByScheme(schemes) +
                "AND ( %s*)", input);

        String path = String.format("api/find-concepts?tenant=%s&collection=gtaa&q=%s&rows=%s",
            encode(tenant), encode(query), max);

        return descriptions(getForPath(path, RDF.class));


    }

    private static final Pattern NOT_FOUND = Pattern.compile(".*The requested resource .* was not found.*", Pattern.DOTALL);

    @Override
    public Optional<Description> retrieveConceptStatus(String id) {
        String url = gtaaUrl + "api/find-concepts?id=" + id;
        try {
            RDF rdf = template.getForObject(url, RDF.class);
            meterRegistry.counter("gtaa.retrieve", "id", id).increment();

            List<Description> descriptions = descriptions(rdf);
            return descriptions.stream().findFirst();
        } catch (GTAAError e) {

            meterRegistry.counter("gtaa.retrieve.error", "id", id, "status", String.valueOf(e.getStatusCode())).increment();

            // It is odd that openskos issues an internal server error for what basically is a 404
            return switch (e.getStatusCode()) {
                case 500 -> {
                    if (NOT_FOUND.matcher(e.getResponseBodyAsString()).matches()) {
                        yield Optional.empty();
                    }
                    throw e;
                }
                case 404 -> Optional.empty();
                default -> {
                    log.error("Unexpected error doing call to openskos for item id {}: {}: {}", id, url, e.getResponseBodyAsString(), e);
                    throw e;
                }
            };
        }
    }

    @Override
    public Optional<GTAAConcept> get(String id) {
        String url = gtaaUrl + "api/find-concepts?id=" + id;
        try {
            RDF rdf = template.getForObject(url, RDF.class);
            meterRegistry.counter("gtaa.get", "id", id).increment();

            List<Description> descriptions = descriptions(rdf);
            return descriptions.stream().findFirst().flatMap(GTAAConcepts::toConcept);
        } catch (GTAAError clientError) {
            meterRegistry.counter("gtaa.get.error", "id", id, "status", String.valueOf(clientError.getStatusCode())).increment();

            if (clientError.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                return Optional.empty();
            }
            if (clientError.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                if (NOT_FOUND.matcher(clientError.getResponseBodyAsString()).matches()) {
                    return Optional.empty();
                }
            }
            log.error("Unexpected error doing call to openskos for item id {}: {}: {}", id, url, clientError.getResponseBodyAsString(), clientError);
            throw clientError;
        }
    }

    private String generateQueryByScheme(SchemeOrNot... schemeList) {
        if (schemeList.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        sb.append("AND (");

        String operator = "";
        for (SchemeOrNot axis : schemeList) {
            boolean not = axis.isNot();
            sb.append(
                    String.format(
                        "%s %s inScheme:\"%s\" ",
                        operator,
                        not ? "NOT" : "",
                        axis.getScheme()
                    )
            );
            operator = "OR";
        }
        sb.append(")");

        return sb.toString();
    }


    private List<Description> descriptions(@Nullable RDF rdf) {
        if (rdf == null || rdf.getDescriptions() == null) {
            return Collections.emptyList();
        }

        return rdf.getDescriptions();
    }

    @Override
    public String toString() {
        return OpenskosRepository.class.getSimpleName() + " " + gtaaUrl;
    }

    private static class DOMSourceUnmarshaller implements Unmarshaller {
        @Override
        public boolean supports(@NonNull Class<?> aClass) {
            return Source.class.isAssignableFrom(aClass);
        }

        @NonNull
        @Override
        public Object unmarshal(@NonNull Source source) throws XmlMappingException {
            try {
                TransformerFactory factory = TransformerFactory.newInstance();
                factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                Transformer transformer = factory.newTransformer();
                DOMResult result = new DOMResult();
                transformer.transform(source, result);
                return new DOMSource(result.getNode());
            } catch (TransformerException e) {
                  throw new WrappedTransformerException(e);
            }

        }
    }

    public static class  WrappedTransformerException extends XmlMappingException implements Serializable {
        @Serial
        private static final long serialVersionUID = -4299814482145831053L;

        WrappedTransformerException(TransformerException e) {
            super(e.getMessage(), e);
        }
    }

}
