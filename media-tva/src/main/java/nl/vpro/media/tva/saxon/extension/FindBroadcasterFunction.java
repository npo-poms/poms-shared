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

import javax.inject.Inject;

import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;

/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
@Slf4j
public class FindBroadcasterFunction extends ExtensionFunctionDefinition {

    private final BroadcasterService broadcasterService;

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
            public Sequence<?> call(XPathContext context, Sequence[] arguments) throws XPathException {
                String value = arguments[0].iterate().next().getStringValueCS().toString().trim().toUpperCase();
                Broadcaster broadcaster = broadcasterService.findForIds(value).orElse(null);
                if (broadcaster == null) {
                    log.warn("No (WON/PD/NEBO) broadcaster for value '{}'", value);
                }
                return broadcaster != null ? new StringValue(broadcaster.getId()) :
                    /* will go wrong in hibernate then, but with catchable error */ new StringValue(value);
            }
        };
    }
}
