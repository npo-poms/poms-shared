package nl.vpro.domain.media;

import javax.persistence.Entity;
import javax.xml.bind.annotation.*;

@Entity(name = "name")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "nameType")
public class Name extends Credits  {



    public Name() {
    }

    public Name(Name source, MediaObject parent) {
        //this.gtaaRecord = source.gtaaRecord;
        this.mediaObject = parent;
    }

    public static Name copy(Name source) {
        return copy(source, source.mediaObject);
    }

    public static Name copy(Name source, MediaObject parent) {
        if (source == null) {
            return null;
        }

        return new Name(source, parent);
    }

/*

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "gtaa_uri")
    @Getter
    @Setter
    private GTAARecord gtaaRecord = new GTAARecord();

*/

}
