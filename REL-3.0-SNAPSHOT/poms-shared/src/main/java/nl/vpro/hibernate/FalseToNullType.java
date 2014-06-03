package nl.vpro.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

/**
 * TODO Not in use anymore?
 *
 * @author Michiel Meeuwissen
 */
public class FalseToNullType implements UserType {

    public static final FalseToNullType INSTANCE = new FalseToNullType();


    public FalseToNullType() {
        super();
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.BOOLEAN};
    }

    @Override
    public Class returnedClass() {
        return Boolean.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if(x == null) {
            x = Boolean.FALSE;
        }
        if(y == null) {
            y = Boolean.FALSE;
        }
        return x.equals(y);

    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return 0;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        Boolean result = rs.getBoolean(names[0]);
        if (result == null || !result) return null;
        return result;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setBoolean(index, false);
        } else {
            st.setBoolean(index, (Boolean) value);
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
}
