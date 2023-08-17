package nl.vpro.media.tva.saxon.extension;

/**
 * @author Michiel Meeuwissen
 * @since 7.7
 */
public class ValidValueFunction extends AbstractValidValueFunction {

    @Override
    protected String getFunctionName() {
        return "validValue";
    }

    @Override
    protected Object getValue(String value) {
        return value;
    }
}
