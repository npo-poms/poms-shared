/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.media.tva.saxon.extension;

import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

import java.util.Collection;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Named;

import nl.vpro.domain.media.Net;

/**
 * @author Michiel Meeuwissen
 * @since 3.1
 */
@Slf4j
public class FindNetFunction extends ExtensionFunctionDefinition {


    private final Supplier<Collection<Net>> netsSupplier;

    @Inject
    public FindNetFunction(@Named("netsSupplier") Supplier<Collection<Net>> netsSupplier) {
        this.netsSupplier = netsSupplier;
    }

    @Override
    public StructuredQName getFunctionQName() {
        return new StructuredQName("vpro", SaxonConfiguration.VPRO_URN, "findNet");
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[]{SequenceType.SINGLE_STRING};
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
                String value = arguments[0].iterate().next().getStringValueCS().toString().trim().toUpperCase();
                for (Net net : netsSupplier.get()) {
                    if (net.getDisplayName().toUpperCase().equals(value) || net.getId().toUpperCase().equals(value)) {
                        return new StringValue(net.getId());
                    }
                }
                log.warn("No such net {} (now returning empty string, which indicates that it can be be ignored)",  value);
                return new StringValue("");
            }
        };
    }
}
