package nl.vpro.domain.gtaa;

import java.io.Serial;
import java.net.URI;
import java.time.Instant;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * A thesaurus item of which the value is represented by a simple {@code <value></value>}
 *
 * These are all ones besides {@link GTAAPerson}.
 *
 * @since 5.11
 */
@XmlTransient
@XmlAccessorType(XmlAccessType.NONE)
public sealed class AbstractSimpleValueGTAAConcept extends AbstractGTAAConcept
    permits
    GTAAClassification,
    GTAAGenre,
    GTAAGeographicName,
    GTAAMaker,
    GTAAName,
    GTAATopic,
    GTAATopicBandG,
    GTAAGenreFilmMuseum {

    @Serial
    private static final long serialVersionUID = 2951729766344371699L;

    public AbstractSimpleValueGTAAConcept(URI id, List<String> scopeNotes, String value, URI redirectedFrom, Status status, Instant lastModified) {
        super(id, scopeNotes, value, redirectedFrom, status, lastModified);
    }

    public AbstractSimpleValueGTAAConcept() {
    }

    @Override
    @XmlElement
    public String getName() {
         return super.getName();
    }

}
