package nl.vpro.domain.media.support;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonValue;

import nl.vpro.domain.Xmlns;
import nl.vpro.validation.NoHtml;

@Entity
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "tagType", namespace = Xmlns.MEDIA_NAMESPACE)
public class Tag implements Serializable, Comparable<Tag> {
    private static final long serialVersionUID = 0l;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotNull
    @Length(min = 1, max = 255)
    @NoHtml
    private String text;

    protected Tag() {
    }

    public Tag(final String text) {
        setText(text);
    }

    @XmlValue
    @JsonValue
    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text.trim();
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public int compareTo(Tag tag) {
        int result = text.compareToIgnoreCase(tag.text);
        return result == 0 ? text.compareTo(tag.text) : result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("text", text)
            .toString();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        Tag tag = (Tag)o;

        return text == null ? tag.text == null : text.equals(tag.text);

    }
}
