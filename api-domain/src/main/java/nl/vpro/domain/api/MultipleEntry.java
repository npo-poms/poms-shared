package nl.vpro.domain.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import nl.vpro.domain.constraint.PredicateTestResult;

/**
 * @author Michiel Meeuwissen
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class MultipleEntry<T> {

    private String id;

    private T result;

    private String error;

    private PredicateTestResult<T> reason;

    public MultipleEntry(String id, T object) {
        this.result = object;
        this.id = id;
        this.error = object == null ? "Not found " + id : null;
    }

    public MultipleEntry() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public PredicateTestResult<T> getReason() {
        return reason;
    }

    public void setReason(PredicateTestResult<T> reason) {
        this.reason = reason;
    }
}
