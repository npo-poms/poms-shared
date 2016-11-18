package nl.vpro.domain.media.update;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Michiel Meeuwissen
 * @since 4.3
 */
public class TransformingSortedSet<T, S> extends AbstractSet<T> implements SortedSet<T>, TransformingCollection<T, S> {
    private final SortedSet<S> wrapped;
    private final Function<S, T> transformer;
    private final Function<T, S> producer;

    private final Comparator<T> comparator = new Comparator<T>() {
        @Override
        public int compare (T o1, T o2){
            return wrapped.comparator().compare(producer.apply(o1), producer.apply(o2));
        }
    };



    public TransformingSortedSet(SortedSet<S> wrapped, Function<S, T> transformer, Function<T, S> producer) {
        this.wrapped = wrapped;
        this.transformer = transformer;
        this.producer = producer;
    }



    @Override
    public Iterator<T> iterator() {
        final Iterator<S> i = wrapped.iterator();
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return i.hasNext();
            }

            @Override
            public T next() {
                return transformer.apply(i.next());
            }
            @Override
            public void remove() {
                i.remove();
            }
        };
    }

    @Override
    public int size() {
        return wrapped.size();
    }


    @Override
    public boolean add(T toAdd) {
        return wrapped.add(producer.apply(toAdd));
    }

    @Override
    public Comparator<? super T> comparator() {
        return comparator;
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        return new TransformingSortedSet<>(wrapped.subSet(producer.apply(fromElement), producer.apply(toElement)), transformer, producer);

    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        return new TransformingSortedSet<>(wrapped.headSet(producer.apply(toElement)), transformer, producer);
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        return new TransformingSortedSet<>(wrapped.tailSet(producer.apply(fromElement)), transformer, producer);
    }

    @Override
    public T first() {
        return transformer.apply(wrapped.first());
    }

    @Override
    public T last() {
        return transformer.apply(wrapped.last());
    }

    public SortedSet<S> unwrap() {
        return wrapped;
    }

    public SortedSet<T> filter() {
        SortedSet<T> result = new TreeSet<>();
        result.addAll(this);
        return result;
    }
    public SortedSet<T> filter(Predicate<S> s) {
        SortedSet<T> result = new TreeSet<>();
        for (S in : wrapped) {
            if (s.test(in)) {
                result.add(transformer.apply(in));
            }
        }
        return result;
    }
}
