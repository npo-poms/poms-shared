package nl.vpro.domain.gtaa;

import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.meeuw.i18n.regions.Region;

import nl.vpro.w3.rdf.Description;

@GTAAScheme(Scheme.geographicname)
@XmlType(
    name = "geographicNameType",
    propOrder = {
        "name",
        "scopeNotes",
        "redirectedFrom"
    }
)
@XmlRootElement(name = "geographicName")
@Schema(name = "GTAAGeographicName")
public class GTAAGeographicName extends AbstractSimpleValueGTAAConcept implements Region {

    private static final long serialVersionUID = 2600104116644142067L;

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
