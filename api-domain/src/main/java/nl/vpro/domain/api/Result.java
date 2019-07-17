/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.EqualsAndHashCode;
import nl.vpro.domain.media.Group;
import nl.vpro.domain.media.Program;
import nl.vpro.domain.media.Segment;
import nl.vpro.domain.page.Page;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resultType", propOrder = {"items"})
@XmlSeeAlso({Page.class, Program.class, Group.class, Segment.class, ApiScheduleEvent.class, Suggestion.class})
@JsonPropertyOrder({"total", "offset", "max", "items"})
@EqualsAndHashCode
public class Result<T> implements Iterable<T> {

    @XmlAttribute
    protected Long total;

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

    public Result(List<? extends T> items, Long offset, Integer max, Long total) {
        this.items = items;
        this.offset = offset;
        this.max = max;
        this.total = total;
    }

    public Result(Result<? extends T> copy) {
        this.items = copy.items;
        this.offset = copy.offset;
        this.max = copy.max;
        this.total = copy.total;
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
        return items == null ? Collections.<T>emptyIterator() : Collections.<T>unmodifiableList(getItems()).iterator();
    }

    public Stream<? extends T> stream() {
        return items == null ? Collections.<T>emptyList().stream() : items.stream();
    }

    public static <T> Collector<T, List<T>, Result<T>> collector() {
        return new Collector<T, List<T>, Result<T>>() {
            @Override
            public Supplier<List<T>> supplier() {
                return ArrayList<T>::new;

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
                return l -> new Result<T>(l, null, null, (long) l.size());
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


}
