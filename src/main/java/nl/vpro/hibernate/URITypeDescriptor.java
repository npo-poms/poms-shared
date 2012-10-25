package nl.vpro.hibernate;

import java.net.URI;
import java.net.URISyntaxException;

import org.hibernate.HibernateException;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;


/**
 * Descriptor for {@link java.net.URI} handling.
 *
 * @author Michiel Meeuwissen
 */
class URITypeDescriptor extends AbstractTypeDescriptor<URI> {
    public static final URITypeDescriptor INSTANCE = new URITypeDescriptor();

    public URITypeDescriptor() {
        super(URI.class);
    }

    @Override
    public String toString(URI value) {
        return value.toString();
    }

    @Override
    public URI fromString(String string) {
        try {
            return new URI(string);
        } catch (URISyntaxException e) {
            throw new HibernateException("Unable to convert string [" + string + "] to URI : " + e);
        }
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <X> X unwrap(URI value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (String.class.isAssignableFrom(type)) {
            return (X) toString(value);
        }
        throw unknownUnwrap(type);
    }

    @Override
    public <X> URI wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (String.class.isInstance(value)) {
            return fromString((String) value);
        }
        throw unknownWrap(value.getClass());
    }
}
