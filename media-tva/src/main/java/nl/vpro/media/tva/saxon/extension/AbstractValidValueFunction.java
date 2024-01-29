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
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.SequenceType;

import java.util.*;

import jakarta.validation.*;
import jakarta.validation.groups.Default;

import org.meeuw.functional.TriPredicate;

import nl.vpro.domain.media.MediaObject;
import nl.vpro.validation.PomsValidatorGroup;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
public abstract class AbstractValidValueFunction extends ExtensionFunctionDefinition {

    static final Set<String> IGNORE_EMAILS = Set.of(
        "nvt"//   Encountered violation error in null: nvt is geen goed email-adres [Camel (mediaRoutes) thread #2 - file:///share/pg/poms] is

    );

    static final TriPredicate<Class<?>, String, Object> warn = (clazz, propertyName, value) -> {
        if (MediaObject.class.equals(clazz)) {
            if ("email".equals(propertyName) && value instanceof List list) {
                boolean warn = false;
                for (Object e : list) {
                    if (!IGNORE_EMAILS.contains(e)) {
                        warn = true;
                        break;
                    }
                }
                return warn;
            }
            return true;
        }
        return true;
    };

    static final Validator validator;
    static {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

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
                try ( SequenceIterator iterate0 = arguments[0].iterate();
                      SequenceIterator iterate1 = arguments[1].iterate();
                      SequenceIterator iterate2 = arguments[2].iterate();) {
                    String clazzName = iterate0.next().getStringValue().trim();
                    String propertyName = iterate1.next().getStringValue().trim();
                    Item valueItem = iterate2.next();
                    Object value = getValue(valueItem.getStringValue().trim());
                    Class<?> clazz = Class.forName(clazzName);
                    Set<? extends ConstraintViolation<?>> constraintViolations =
                        validator.validateValue(
                            clazz, propertyName, value,
                            validationGroups()
                        );
                    if (warn.test(clazz, propertyName, value)) {
                        for (ConstraintViolation<?> cv : constraintViolations) {
                            log.warn("Encountered violation error in {}: {}", context.getCurrentOutputUri(), cv.getMessage());
                        }
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


    protected Class<?>[] validationGroups() {
        return new Class<?>[]{PomsValidatorGroup.class, Default.class};
    }
}
