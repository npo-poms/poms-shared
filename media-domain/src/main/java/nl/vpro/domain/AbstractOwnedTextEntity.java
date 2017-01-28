package nl.vpro.domain;

import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.validation.NoHtml;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@MappedSuperclass
@ToString(exclude = {"parent"})
@XmlAccessorType(XmlAccessType.NONE)
@XmlTransient
public abstract class AbstractOwnedTextEntity<T extends AbstractOwnedTextEntity, P> extends AbstractOwnedText<T> {

    @Column(nullable = false)
    @NotNull(message = "{nl.vpro.constraints.NotNull}")
    @Size.List({
        @Size(min = 1, message = "{nl.vpro.constraints.text.Size.min}"),
        @Size(max = 255, message = "{nl.vpro.constraints.text.Size.max}")
    })
    @NoHtml
    @XmlValue
    protected String value;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlTransient
    private Long id;

    @ManyToOne
    private P parent;

    public AbstractOwnedTextEntity(String value, OwnerType owner, TextualType type) {
        super(owner, type);
        this.value = value;
    }

    protected AbstractOwnedTextEntity() {
    }


    @Override
    public String get() {
        return value;
    }

    @Override
    public void set(String value) {
        this.value = value;
    }

    Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    public P getParent() {
        return parent;
    }

    public void setParent(P  parent) {
        this.parent = parent;
    }
}
