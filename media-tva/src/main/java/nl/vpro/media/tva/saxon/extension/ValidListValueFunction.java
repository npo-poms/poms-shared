package nl.vpro.media.tva.saxon.extension;

import java.util.Arrays;

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
}
