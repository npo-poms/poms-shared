package nl.vpro.domain.media.support;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import javax.xml.XMLConstants;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonValue;

import nl.vpro.domain.Identifiable;
import nl.vpro.domain.Xmlns;
import nl.vpro.i18n.Locales;
import nl.vpro.validation.NoHtml;
import nl.vpro.xml.bind.LocaleAdapter;

@Entity
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "tagType", namespace = Xmlns.MEDIA_NAMESPACE)
public class Tag implements Serializable, Comparable<Tag>, Identifiable<Long> {
    @Serial
    private static final long serialVersionUID = 0L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private Long id;

    @Column(nullable = false, unique = true)
    @NotNull
    @Size(min = 1, max = 255)
    @NoHtml
    private String text;

    @Column
    private Locale language = Locales.DUTCH;

    public Tag() {
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
        this.text = text == null ? null : text.trim();
    }

    @XmlAttribute(name = "lang", namespace = XMLConstants.XML_NS_URI)
    @XmlJavaTypeAdapter(LocaleAdapter.class)
    @Schema(implementation = String.class, type = "string")
    public Locale getLanguage() {
        return language == null || language.equals(Locales.DUTCH) ? null : language;
    }

    public void setLanguage(Locale locale) {
        this.language = locale;
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }

    @Override
    public int compareTo(@NotNull Tag tag) {
        if (text == null) {
            if (tag.text == null) {
                return 0;
            }
            return -1;
        }
        if (tag.text == null) {
            return 1;
        }
        int result = text.compareToIgnoreCase(tag.text);
        return result == 0 ? text.compareTo(tag.text) : result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
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
