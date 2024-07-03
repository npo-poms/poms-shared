package nl.vpro.domain;

import java.io.Serial;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.validation.NoHtml;

/**
 *
 * An {@link AbstractOwnedTextEntity} with no constraint on the length of the value.
 * @author Michiel Meeuwissen
 * @since 8.2
 */
@MappedSuperclass
@XmlAccessorType(XmlAccessType.NONE)
@XmlTransient
public abstract class AbstractDescriptionEntity<T extends AbstractDescriptionEntity<T, P>, P> extends AbstractOwnedTextEntity<T, P> {

    @Serial
    private static final long serialVersionUID = -4621135759610402997L;

    @Column(nullable = false)
    @NotNull(message = "{nl.vpro.constraints.NotNull}")
    @Size.List({
        @Size(min = 1, message = "{nl.vpro.constraints.text.Size.min}"),
    })
    @NoHtml(aggressive = false)
    @XmlValue
    protected String value;


    protected AbstractDescriptionEntity(@NonNull String value, OwnerType owner, TextualType type) {
        super(owner, type);
        this.value = value;
    }

    protected AbstractDescriptionEntity() {
    }


    @Override
    public String get() {
        return value;
    }

    @Override
    public void set(String value) {
        this.value = value;
    }

}
