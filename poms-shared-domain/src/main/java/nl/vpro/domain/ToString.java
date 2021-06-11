package nl.vpro.domain;

import org.apache.commons.lang3.builder.*;


/**
 * @since 5.28
 */
public class ToString {

    private ToString() {

    }

    public static final ToStringStyle STYLE;
    static {
        StandardToStringStyle style = new StandardToStringStyle() {
            private static final long serialVersionUID = -4020286714849246315L;

            @Override
            protected void appendDetail(final StringBuffer buffer, final String fieldName, final Object value) {
                if (value instanceof CharSequence) {
                    buffer.append('\'');
                }
                super.appendDetail(buffer, fieldName, value);
                if (value instanceof CharSequence) {
                    buffer.append('\'');
                }
            }

        };
        style.setUseShortClassName(true);
        style.setUseIdentityHashCode(false);
        style.setContentEnd("}");
        style.setContentStart("{");
        style.setArrayStart("[");
        style.setArrayEnd("]");
        style.setFieldSeparator(", ");
        STYLE = style;
    }

    public static ToStringBuilder builder(Object object) {
        return new ToStringBuilder(object, ToString.STYLE);
    }

}
