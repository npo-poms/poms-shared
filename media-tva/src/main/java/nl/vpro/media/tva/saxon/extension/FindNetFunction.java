/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.tva.saxon.extension;

import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.*;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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

    private final static ConcurrentMap<String, AtomicInteger> WARNS = new ConcurrentHashMap<>();

    private final static Set<String>  ACKNOWLEDGED = Set.of(
        "NEDERLAND 1",
        "NEDERLAND 2",
        "NEDERLAND 3"
    );

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
            public Sequence call(XPathContext context, Sequence[] arguments) {
                try (SequenceIterator si = arguments[0].iterate()) {
                    final String value = si.next().getStringValue().trim().toUpperCase();
                    for (Net net : netsSupplier.get()) {
                        if (net.getDisplayName().toUpperCase().equals(value) || net.getId().toUpperCase().equals(value)) {
                            return new StringValue(net.getId());
                        }
                    }
                    AtomicInteger occurrence = WARNS.computeIfAbsent(value, (v) -> new AtomicInteger(0));
                    boolean acknowledge = ACKNOWLEDGED.contains(value);
                    Level level = occurrence.incrementAndGet() % 100 == 1 ?
                        acknowledge ? Level.INFO : Level.WARN : Level.DEBUG;
                    Slf4jHelper.log(log, level, "No such net {} (#{}, now returning empty string, which indicates that it can be ignored){}", value, occurrence.get(), acknowledge ? " (acknowledge)" : "");
                    return new StringValue("");
                }
            }
        };
    }
}
