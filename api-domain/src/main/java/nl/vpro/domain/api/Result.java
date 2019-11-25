/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Stream;

import javax.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.*;

import nl.vpro.domain.media.*;
import nl.vpro.domain.page.Page;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resultType", propOrder = {"items"})
@XmlSeeAlso({Page.class, Program.class, Group.class, Segment.class, ApiScheduleEvent.class, Suggestion.class})
@JsonPropertyOrder({"total", "totalQualifier", "offset", "max", "items"})
@EqualsAndHashCode
public class Result<T> implements Iterable<T> {

    @XmlAttribute
    protected Long total;


    /**
     * The value of {@link #getTotal()} may  not be exact
     */
    @XmlAttribute
    @Getter
    protected TotalQualifier totalQualifier = TotalQualifier.MISSING;

    @XmlAttribute
    protected Long offset = 0L;

    @XmlAttribute
    protected Integer max;

    @XmlElementWrapper(name = "items")
    @XmlElement(name = "item")
    @JsonProperty("items")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    protected List<? extends T> items;

    public Result() {
    }

    public Result(List<? extends T> items, Long offset, Integer max, Long total, TotalQualifier totalQualifier) {
        this.items = items;
        this.offset = offset;
        this.max = max;
        this.total = total;
        this.totalQualifier = totalQualifier;
    }

    public Result(Result<? extends T> copy) {
        this.items = copy.items;
        this.offset = copy.offset;
        this.max = copy.max;
        this.total = copy.total;
        this.totalQualifier = copy.totalQualifier;
    }

    public List<? extends T> getItems() {
        return items;
    }

    /**
     * The offset (base 0) of the list.
     */
    public Long getOffset() {
        return offset;
    }

    /**
     * The maximum size of this list as originally requested by the client.
     */
    public Integer getMax() {
        return max;
    }

    /**
     * The actual size of this list.
     */
    public Integer getSize() {
        return items == null ? 0 : items.size();
    }


    /**
     * The size of the list if no offset or max would have been applied.
     */
    public Long getTotal() {
        return total;
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return items == null ? Collections.emptyIterator() : Collections.<T>unmodifiableList(getItems()).iterator();
    }

    public Stream<? extends T> stream() {
        return items == null ? Stream.of() : items.stream();
    }

    public static <T> Collector<T, List<T>, Result<T>> collector() {
        return new Collector<T, List<T>, Result<T>>() {
            @Override
            public Supplier<List<T>> supplier() {
                return ArrayList::new;

            }

            @Override
            public BiConsumer<List<T>, T> accumulator() {
                return List::add;
            }

            @Override
            public BinaryOperator<List<T>> combiner() {
                return (left,right) -> {left.addAll(right); return left;};

            }

            @Override
            public Function<List<T>, Result<T>> finisher() {
                return l -> new Result<T>(l, null, null, (long) l.size(), TotalQualifier.EQUAL_TO);
            }


            @Override
            public Set<Characteristics> characteristics() {
                return Collections.emptySet();

            }
        };
    }

    @Override
    public String toString() {
        return "" + getItems();
    }


    /**
     * @since 5.12
     */
    public enum TotalQualifier {
        EQUAL_TO,
        APPROXIMATE,
        GREATER_THAN_OR_EQUAL_TO,
        MISSING
    }


}
