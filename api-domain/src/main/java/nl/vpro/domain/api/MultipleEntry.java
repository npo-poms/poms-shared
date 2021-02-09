package nl.vpro.domain.api;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.constraint.PredicateTestResult;

/**
 * @author Michiel Meeuwissen
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.NONE)
@Data
@XmlType(propOrder = {"id", "result", "error", "reason"})
@JsonPropertyOrder({"id", "result", "error", "reason"})
public class MultipleEntry<T> {

    @XmlElement
    private String id;

    @XmlElement
    private T result;

    @XmlElement
    private String error;

    @XmlElement
    private PredicateTestResult reason;

    public MultipleEntry(String id, T object) {
        this.result = object;
        this.id = id;
        this.error = object == null ? "Not found " + id : null;
    }

    public MultipleEntry() {
    }

    public boolean isFound() {
        return result != null;
    }

}
