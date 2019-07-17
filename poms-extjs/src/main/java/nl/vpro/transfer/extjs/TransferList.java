/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs;

import java.util.*;

import org.checkerframework.checker.nullness.qual.NonNull;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "success",
        "results",
        "message",
        "list",
        "writable"
        })
public abstract class TransferList<T> implements Iterable<T> {
    protected boolean success;

    protected Integer results = 0;

    protected String message;

    protected Boolean writable = null;

    @XmlElement(name = "item")
    @JsonProperty("list")
    protected List<T> list = new ArrayList<>();

    public TransferList<T> add(T transfer) {
        list.add(transfer);
        results = list.size();
        return this;
    }

    public TransferList<T> addAll(Collection<T> transfers) {
        list.addAll(transfers);
        results = list.size();
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    /**
     * Total results. When using a pager the number of results might
     * differ from the number of items returned by #getList.
     *
     * @return - number of results for a query
     */
    public Integer getResults() {
        return results;
    }

    public String getMessage() {
        return message;
    }

    public List<T> getList() {
        return list;
    }

    public Boolean getWritable() {
        return writable;
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}
