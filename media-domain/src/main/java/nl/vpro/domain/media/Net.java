package nl.vpro.domain.media;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlValue;

import nl.vpro.domain.Identifiable;
import nl.vpro.i18n.Displayable;


@Entity
public class Net implements Displayable, Serializable, Comparable<Net>, Identifiable<String> {

    @Serial
    private static final long serialVersionUID = -5278065987837127858L;

    @Id
    @NotNull(message = "type is required")
    @jakarta.validation.constraints.Pattern(regexp = "[A-Z0-9_-]{2,12}", message = "type should conform to: [A-Z0-9_-]{2,12}")
    @XmlValue
    private String id;

    @Column
    private String displayName;

    protected Net() {
        // hibernate needs default constructor (somewhy)
    }

    public Net(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public Net(String id) {
        this.id = id;
        this.displayName = null;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return this.id + (this.displayName == null ? "" : " (" + this.displayName + ")");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Net)) return false;

        Net net = (Net) o;

        return id.equals(net.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public int compareTo(Net o) {
        return Objects.compare(this.id, o.id, Comparator.naturalOrder());

    }
}
