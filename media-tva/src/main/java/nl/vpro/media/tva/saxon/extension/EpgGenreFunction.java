/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.media.tva.saxon.extension;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
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
     * For some use cases the TVA xmls are messy and the href can't be used. If you set this, the _name_ of the genre will be prefixed by this, and then matched.
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
                CharSequence epgValue;
                if (StringUtils.isNotEmpty(matchOnValuePrefix)) {
                    TinyElementImpl next = (TinyElementImpl) arguments[1].iterate().next();
                    epgValue = (matchOnValuePrefix  + next.getStringValueCS());
                } else {
                    epgValue = arguments[0].iterate().next().getStringValueCS();
                }
                try {
                    Term term = MediaClassificationService.getInstance().getTermByReference(epgValue.toString(), (s) -> true);
                    return new StringValue(term.getTermId());
                } catch (IllegalArgumentException iea){
                    if (ignore != null && ignore.contains(epgValue.toString())) {
                        return new StringValue("");
                    }
                    if (warned == null || warned.add(epgValue.toString())) {
                        log.warn(iea.getMessage());
                    } else {
                        log.debug(iea.getMessage());
                    }
                    switch(notFound) {
                        case FATAL:
                            throw iea;
                        case ASIS:
                             return new StringValue(epgValue);
                        case IGNORE:
                        default:
                            return new StringValue("");
                    }
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
