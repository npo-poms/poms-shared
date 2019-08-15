package nl.vpro.domain.gtaa;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.meeuw.i18n.Region;

import nl.vpro.w3.rdf.Description;

@GTAAScheme(Scheme.geographicname)
@XmlType(
    name = "geographicName",
    propOrder = {
    "name",
    "scopeNotes",
    "redirectedFrom"
})
@XmlRootElement(name = "geographicName")
public class GTAAGeographicName extends AbstractSimpleValueGTAAConcept implements Region {

    @lombok.Builder(builderClassName = "Builder")
    public GTAAGeographicName(URI id, List<String> scopeNotes, String value, URI redirectedFrom, Status status, Instant lastModified) {
        super(id, scopeNotes, value, redirectedFrom, status, lastModified);
    }

    public GTAAGeographicName() {

    }

    public static GTAAGeographicName create(Description description) {
        final GTAAGeographicName answer = new GTAAGeographicName();
        fill(description, answer);
        return answer;
    }


    @Override
    public String getCode() {
        return getId().toString();

    }

    @Override
    public Type getType() {
        return Type.UNDEFINED;

    }
}
