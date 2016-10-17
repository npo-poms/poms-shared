package nl.vpro.domain.media.update;

import java.util.AbstractList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Michiel Meeuwissen
 * @since 4.3
 */
public class TransformingList<T, S> extends AbstractList<T> implements TransformingCollection<T, S> {


    private List<S> wrapped;
    private final Function<S, T> transformer;
    private final Function<T, S> producer;

    public TransformingList(List<S> wrapped, Function<S, T> transformer, Function<T, S> producer) {
        this.wrapped = wrapped;
        this.transformer = transformer;
        this.producer = producer;
    }


    @Override
    public T get(int index) {
        return transformer.apply(wrapped.get(index));
    }

    @Override
    public int size() {
        return wrapped.size();
    }


    @Override
    public T set(int i, T toSet) {
        S newObject = wrapped.set(i, producer.apply(toSet));
        return transformer.apply(newObject);
    }

    @Override
    public void add(int i, T toAdd) {
        wrapped.add(i, producer.apply(toAdd));
    }

    @Override
    public T remove(int i) {
        S removed = wrapped.remove(i);
        return transformer.apply(removed);
    }

    @Override
    public List<S> unwrap() {
        return wrapped;
    }
}
