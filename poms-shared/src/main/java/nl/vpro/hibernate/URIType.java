package nl.vpro.hibernate;

import java.net.URI;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.StringType;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

/**
 * A type that maps between {@link java.sql.Types#VARCHAR VARCHAR} and {@link java.net.URI}
 *
 * @author Michiel Meeuwissen
 */
public class URIType extends AbstractSingleColumnStandardBasicType<URI> implements DiscriminatorType<URI> {

    public URIType() {
        super(VarcharTypeDescriptor.INSTANCE, URITypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "uri";
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }

    @Override
    public String toString(URI value) {
        return URITypeDescriptor.INSTANCE.toString(value);
    }

    @Override
    public String objectToSQLString(URI value, Dialect dialect) throws Exception {
        return StringType.INSTANCE.objectToSQLString(toString(value), dialect);
    }

    @Override
    public URI stringToObject(String xml) throws Exception {
        return URITypeDescriptor.INSTANCE.fromString(xml);
    }
}
