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
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

import java.util.*;

import javax.inject.Inject;

import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;
import nl.vpro.logging.Slf4jHelper;

import static nl.vpro.logging.simple.Level.DEBUG;
import static nl.vpro.logging.simple.Level.WARN;

/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
@Slf4j
public class FindBroadcasterFunction extends ExtensionFunctionDefinition {

    @Getter
    @Setter
    private NotFound notFound = NotFound.ASIS;

    private final BroadcasterService broadcasterService;

    private static final Set<String> warned = Collections.synchronizedSet(new HashSet<>());

    @Inject
    public FindBroadcasterFunction(BroadcasterService broadcasterService) {
        this.broadcasterService = broadcasterService;
    }

    @Override
    public StructuredQName getFunctionQName() {
        return new StructuredQName("vpro", SaxonConfiguration.VPRO_URN, "findBroadcaster");
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

                    String value = si.next().getStringValue().trim().toUpperCase();
                    Broadcaster broadcaster = broadcasterService.findForIds(value).orElse(null);


                    if (broadcaster != null) {
                        if (! broadcaster.getWhatsOnId().equalsIgnoreCase(value)) {
                            Slf4jHelper.log(log, warned.add(broadcaster.getId()) ? WARN : DEBUG, "Broadcaster {} did not match on whatson id {}", broadcaster, value);
                        }
                        return new StringValue(broadcaster.getId());
                    }

                    log.warn("No (WON/PD/NEBO) broadcaster for value '{}' in {}", value, broadcasterService);

                    return switch (notFound) {
                        case ASIS ->
                            /* will go wrong in hibernate then, but with catchable error */
                            new StringValue(value);
                        case IGNORE -> new StringValue("");
                        default -> throw new IllegalArgumentException("No such broadcaster " + value);
                    };

                }
            }
        };
    }
}
