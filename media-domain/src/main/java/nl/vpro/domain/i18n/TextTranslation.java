package nl.vpro.domain.i18n;

import lombok.*;

import java.util.function.Supplier;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlTransient;

import nl.vpro.validation.NoHtml;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@MappedSuperclass
@ToString
@Access(AccessType.FIELD)
@EqualsAndHashCode(exclude = {"id"})
public class TextTranslation implements Supplier<String> {

    @Column(nullable = false)
    @NoHtml(aggressive = false)
    protected String value;

    @Id
    @GeneratedValue
    @XmlTransient
    @Getter @Setter(AccessLevel.PROTECTED)
    private Long id;


    public TextTranslation(String value) {
        this.value = value;
    }

    protected TextTranslation() {
    }


    @Override
    public String get() {
        return value;
    }

    public void set(String value) {
        this.value = value;
    }


}
