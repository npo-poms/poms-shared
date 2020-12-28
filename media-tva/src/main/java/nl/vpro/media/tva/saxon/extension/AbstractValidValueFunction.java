/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.media.tva.saxon.extension;

import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.*;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.SequenceType;

import java.util.Set;

import javax.validation.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
public abstract class AbstractValidValueFunction extends ExtensionFunctionDefinition {


    static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    static Validator validator = factory.getValidator();

    @Override
    public StructuredQName getFunctionQName() {
        return new StructuredQName("vpro", SaxonConfiguration.VPRO_URN, getFunctionName());
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[]{SequenceType.SINGLE_STRING, SequenceType.SINGLE_STRING, SequenceType.SINGLE_STRING};
    }

    @Override
    public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
        return SequenceType.SINGLE_BOOLEAN;
    }

    @Override
    public ExtensionFunctionCall makeCallExpression() {
        return new ExtensionFunctionCall() {
            @Override
            public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
                try {
                    String clazzName = arguments[0].iterate().next().getStringValueCS().toString().trim();
                    String propertyName = arguments[1].iterate().next().getStringValueCS().toString().trim();
                    Item valueItem = arguments[2].iterate().next();
                    Object value = getValue(valueItem.getStringValueCS().toString().trim());
                    Set<? extends ConstraintViolation<?>> constraintViolations =
                        validator.validateValue(
                            Class.forName(clazzName), propertyName, value,
                            validationGroups()
                        );
                    for (ConstraintViolation<?> cv : constraintViolations) {
                        log.warn("Encountered violation error in {}: {}", context.getCurrentOutputUri(), cv.getMessage());
                    }
                    return BooleanValue.get(constraintViolations.isEmpty());
                } catch (ClassNotFoundException e) {
                    log.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            }
        };
    }
    protected abstract String getFunctionName();

    protected abstract Object getValue(String value);

    protected abstract Class<?>[] validationGroups();
}
