package nl.vpro.domain.api;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Michiel Meeuwissen
 * @since 4.2
 */
public class FormUtils {

    private FormUtils() {
    }

    public static String getText(AbstractTextSearch<?> searches) {
        if (searches != null) {
            SimpleTextMatcher text = searches.getText();
            if (text != null) {
                String value = text.getValue();
                if (StringUtils.isNotBlank(value)) {
                    return value;
                }
            }
        }
        return null;
    }
}
