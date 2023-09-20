/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.classification;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXB;
import javax.xml.parsers.*;
import javax.xml.transform.dom.DOMSource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
public abstract class AbstractClassificationServiceImpl implements ClassificationService {


    private final Logger log = LoggerFactory.getLogger(getClass());

    protected final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    protected SortedMap<TermId, Term> terms = null;

    protected Instant lastModified = null;

    @Override
    public Term getTerm(String termId) throws TermNotFoundException {
        try {
            Term term = getTermsMap().get(new TermId(termId));
            if (term == null) {
                throw new TermNotFoundException(termId);
            }
            return term;
        } catch (NumberFormatException nfe) {
            TermNotFoundException tnfe =  new TermNotFoundException(termId);
            tnfe.initCause(nfe);
            throw tnfe;
        }
    }

    @Override
    public List<Term> getTermsByReference(String reference) {
        return getTermsByReference(reference, values());
    }

    static List<Term> getTermsByReference(String reference, Collection<Term> values) {
        List<Term> result = new ArrayList<>();
        for (Term term : values) {
            for (Reference ref : term.getReferences()) {
                if (ref.getValue().equals(reference)) {
                    result.add(term);
                }
            }
        }
        return result;
    }

    @NonNull
    public Term getTermByReference(String code, Predicate<String> predicate) {
        for (Term term : values()) {
            if (term.getReferences().stream()
                .map(Reference::getValue)
                .anyMatch(predicate.and(s -> s.equals(code)))) {
                return term;
            }
        }
        throw new IllegalArgumentException("No such term with reference " + code);
    }



    @Override
    public boolean hasTerm(String termId) {
        try {
            return getTermsMap().containsKey(new TermId(termId));
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    @Override
    public Collection<Term> values() {
        return getTermsMap().values();
    }

    @Override
    public Collection<Term> valuesOf(String termId) {
        TermId id = new TermId(termId);
        return getTermsMap().subMap(id.first(), id.next()).values();
    }

    @Override
    public ClassificationScheme getClassificationScheme() {
        return new ClassificationScheme(null, values().stream().filter(input -> {
            TermId id = new TermId(input.getTermId());
            return id.getParts().length == 2 && id.getParts()[0] == 3;
        }).collect(Collectors.toList()));
    }

    @Override
    public Instant getLastModified() {
        return lastModified;
    }

    @PreDestroy
    public void cleanUp() {
        executorService.shutdownNow();
    }

    protected synchronized  SortedMap<TermId, Term> getTermsMap() {
        if(terms == null) {
            // This can be called via Jaxb unmarshalling, so it cannot happen in the same thread.
            Future<?> future = executorService.submit(() -> {
                try {
                    List<InputSource> sources = getSources(true);
                    if (sources != null) {
                        terms = readTerms(sources);
                    } else {
                        log.debug("No sources");
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
            try {
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                log.error(e.getMessage(), e);
            }
            if (terms == null) {
                terms = new TreeMap<>();
            }
        }

        return terms;
    }

    protected SortedMap<TermId, Term> readTerms(Iterable<InputSource> streams) throws ParserConfigurationException {
        SortedMap<TermId, Term> result = new TreeMap<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);//"http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setNamespaceAware(true);
        final DocumentBuilder  builder = factory.newDocumentBuilder();


        for (InputSource input : streams) {
            SortedMap<TermId, Term> subResult = new TreeMap<>();
            try {
                Document document = builder.parse(input);
                if (document.getDocumentElement().getNodeName().equals("Term")) {
                    Term term = JAXB.unmarshal(new DOMSource(document), Term.class);
                    put(term, subResult);
                    putAll(term, subResult);
                } else {
                    ClassificationScheme genreCs = JAXB.unmarshal(new DOMSource(document), ClassificationScheme.class);
                    putAll(genreCs, subResult);
                }
            } catch (Exception e) {
                log.error(input.getSystemId() + ":" + e.getMessage(), e);
                continue;
            }
            // only if success, set the parents right
            for (Term term : subResult.values()) {
                try {
                    TermId id = new TermId(term.getTermId());
                    Term parent = result.get(id.getParentId());
                    if (parent != null) {
                        term.setParent(parent);
                        parent.addTerm(term);
                    }
                } catch (Exception e){
                    log.error(input.getSystemId() + ":" + e.getMessage(), e);
                }

            }
            result.putAll(subResult);
        }
        if (lastModified == null) {
            lastModified = Instant.now();
        }
        log.info("Read {}",  toString(result));
        return result;
    }

    void put(Term term, Map<TermId, Term> map) {
        if (term.getTermId() == null) {
            throw new IllegalArgumentException("No id in " + term);
        }
        if (map.containsKey(new TermId(term.getTermId()))) {
            throw new IllegalStateException("Double occurrence of " + term.getTermId());
        }
        map.put(new TermId(term.getTermId()), term);


    }

    void putAll(TermContainer container, Map<TermId, Term> map) {
        for(Term term : container.getTerms()) {
            put(term, map);
            putAll(term, map);
        }
    }

    @Nullable
    protected abstract List<InputSource> getSources(boolean init);

    @Override
    public String toString() {
        return toString(terms);
    }

    protected String toString(Map<TermId, Term> map) {
        if (map == null) {
            return "{still empty}";
        } else {
            return map.size() + " terms, last modified: " + lastModified +
                " (" + map.values()
                .stream()
                .filter(t -> TermId.of(t.getTermId()).getParts().length == 3)
                .map(t -> t.getTermId() + ":" + t.getName())
                .collect(Collectors.joining(", ")) + ")";
        }
    }
}


