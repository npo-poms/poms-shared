package nl.vpro.media.tva.saxon.extension;

import java.util.Arrays;

import javax.validation.groups.Default;

import nl.vpro.validation.PomsValidatorGroup;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public class ValidListValueFunction extends AbstractValidValueFunction {

    @Override
    protected String getFunctionName() {
        return "validListValue";
    }

    @Override
    protected Object getValue(String value) {
        return Arrays.asList(value);
    }

    @Override
    protected Class<?>[] validationGroups() {
        return new Class<?>[]{PomsValidatorGroup.class, Default.class};
    }
}
