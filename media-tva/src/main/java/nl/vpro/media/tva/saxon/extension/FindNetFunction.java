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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.event.Level;

import nl.vpro.domain.media.Net;
import nl.vpro.logging.Slf4jHelper;

/**
 * @author Michiel Meeuwissen
 * @since 3.1
 */
@Slf4j
public class FindNetFunction extends ExtensionFunctionDefinition {


    private final Supplier<Collection<Net>> netsSupplier;

    private final Map<String, AtomicInteger> warns = new ConcurrentHashMap<>();

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
                AtomicInteger occurence = warns.computeIfAbsent(value, (v) -> new AtomicInteger(0));
                Level level = occurence.getAndIncrement() % 100 == 0 ? Level.WARN : Level.DEBUG;
                Slf4jHelper.log(log, level, "No such net {} (#{}, now returning empty string, which indicates that it can be ignored)",  occurence.get(), value);
                return new StringValue("");
            }
        };
    }
}
