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
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

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
    private boolean notFoundIsFatal = true;

    @Override
    public StructuredQName getFunctionQName() {
        return new StructuredQName("vpro", SaxonConfiguration.VPRO_URN, "transformEpgGenre");
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
                CharSequence epgValue = arguments[0].iterate().next().getStringValueCS();
                try {
                    Term term = MediaClassificationService.getTermByEpgCode(epgValue.toString());
                    return new StringValue(term.getTermId());
                } catch (IllegalArgumentException iea){
                    log.warn(iea.getMessage());
                    if (notFoundIsFatal) {
                        throw iea;
                    } else {
                        return new StringValue(epgValue);
                    }
                }

            }
        };
    }
}