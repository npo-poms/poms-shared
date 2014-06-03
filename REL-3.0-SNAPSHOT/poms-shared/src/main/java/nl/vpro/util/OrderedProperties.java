package nl.vpro.util;

import java.util.*;

/**
 * Extension of properties that remembers insertion order.
 * @author Michiel Meeuwissen
 * @since 1.8
 */
public class OrderedProperties extends Properties {

    private final List<Object> names = new ArrayList<Object>();


    @Override
    public Enumeration propertyNames() {
        return Collections.enumeration(names);
    }

    @Override
    public Set entrySet() {
        return new AbstractSet() {

            @Override
            public Iterator iterator() {
                final Iterator i = names.iterator();
                return new Iterator() {

                    @Override
                    public boolean hasNext() {
                        return i.hasNext();
                    }

                    @Override
                    public Object next() {
                        Object key = i.next();
                        return new AbstractMap.SimpleEntry(key, OrderedProperties.this.get(key));
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }

            @Override
            public int size() {
                return OrderedProperties.this.size();
            }
        };
    }

    @Override
    public Object put(Object key, Object value) {
        names.remove(key);
        names.add(key);

        return super.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        names.remove(key);
        return super.remove(key);
    }
}
