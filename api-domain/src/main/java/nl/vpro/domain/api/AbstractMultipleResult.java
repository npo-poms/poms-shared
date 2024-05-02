package nl.vpro.domain.api;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;
import java.util.function.BiFunction;

import jakarta.xml.bind.annotation.XmlTransient;

import nl.vpro.domain.constraint.DisplayablePredicate;
import nl.vpro.domain.constraint.PredicateTestResult;

/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
public abstract class AbstractMultipleResult<M extends Serializable> extends Result<MultipleEntry<M>> {

    @XmlTransient
    protected final BiFunction<String, M, MultipleEntry<M>> producer;

    public AbstractMultipleResult(BiFunction<String, M, MultipleEntry<M>> producer) {
        this.producer = producer;
        this.offset = null;
    }

    public AbstractMultipleResult(BiFunction<String, M, MultipleEntry<M>> producer, List<String> ids, List<M> objects) {
        this(producer, ids, objects, null);
    }

    public AbstractMultipleResult(BiFunction<String, M, MultipleEntry<M>> producer, List<String> ids, List<M> objects, DisplayablePredicate<M> predicate) {
        this(producer);
        this.items = adapt(ids, objects, predicate);
        this.total = (long) objects.size();
        this.totalQualifier = TotalQualifier.EQUAL_TO;
        this.offset = null;
        this.max = null;

    }

    private List<? extends MultipleEntry<M>> adapt(final List<String> ids, final List<? extends M> list, DisplayablePredicate<M> predicate) {
        return new AbstractList<MultipleEntry<M>>() {
            @Override
            public MultipleEntry<M> get(int index) {
                MultipleEntry<M> entry = producer.apply(ids.get(index), list.get(index));
                if (predicate != null && entry.isFound()) {
                    PredicateTestResult result = predicate.testWithReason(entry.getResult());
                    if (!result.applies()) {
                        entry.setError("Does not match profile " + predicate + ": " + ids.get(index));
                        entry.setReason(result);
                        entry.setResult(null); // ?
                    }
                }
                return entry;
            }

            @Override
            public int size() {
                return list.size();
            }
        };
    }

    public Result<M> toResult() {
        return stream().map(MultipleEntry::getResult).collect(Result.collector());
    }
}
