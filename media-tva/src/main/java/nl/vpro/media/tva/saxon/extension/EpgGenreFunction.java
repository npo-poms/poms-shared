/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.tva.saxon.extension;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.*;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.tree.tiny.TinyElementImpl;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.classification.Term;
import nl.vpro.domain.media.MediaClassificationService;

/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
@Slf4j
public class EpgGenreFunction extends ExtensionFunctionDefinition {


    @Getter
    @Setter
    private NotFound notFound = NotFound.ASIS;

    /**
     * For some use cases the TVA xmls are messy and the href can't be used. If you set this, the <em>name</em> of the genre will be prefixed by this, and then matched (See e.g. {@link nl.vpro.media.tva.Constants#BINDINC_GENRE_PREFIX}).
     *
     */
    @Getter
    @Setter
    private String matchOnValuePrefix;

    @Getter
    @Setter
    private Set<String> ignore;

    private Set<String> warned;


    @Override
    public StructuredQName getFunctionQName() {
        return new StructuredQName("vpro", SaxonConfiguration.VPRO_URN, "transformEpgGenre");
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[]{SequenceType.SINGLE_STRING, SequenceType.SINGLE_NODE};
    }

    @Override
    public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
        return SequenceType.SINGLE_STRING;
    }

    @Override
    public ExtensionFunctionCall makeCallExpression() {
        return new ExtensionFunctionCall() {
            @Override
            public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
                String epgValue;
                if (StringUtils.isNotEmpty(matchOnValuePrefix)) {
                    try (SequenceIterator si = arguments[1].iterate()) {

                        TinyElementImpl next = (TinyElementImpl) si.next();
                        epgValue = (matchOnValuePrefix + next.getStringValue());
                    }
                } else {
                    try (SequenceIterator si = arguments[0].iterate()) {
                        epgValue = si.next().getStringValue();
                    }
                }
                try {
                    Term term = MediaClassificationService.getInstance().getTermByReference(epgValue, (s) -> true);
                    return new StringValue(term.getTermId());
                } catch (IllegalArgumentException iea){
                    if (ignore != null && ignore.contains(epgValue)) {
                        return new StringValue("");
                    }
                    if (warned == null || warned.add(epgValue)) {
                        log.warn(iea.getMessage());
                    } else {
                        log.debug(iea.getMessage());
                    }
                    return switch (notFound) {
                        case FATAL -> throw iea;
                        case ASIS -> new StringValue(epgValue);
                        default -> new StringValue("");
                    };
                }

            }
        };
    }

    public void setWarnOnce(boolean warnOnce) {
        if (warnOnce) {
            warned = new CopyOnWriteArraySet<>();
        } else {
            warned = null;
        }
    }
}
