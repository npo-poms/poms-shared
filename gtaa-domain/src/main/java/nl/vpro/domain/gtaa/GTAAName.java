package nl.vpro.domain.gtaa;

import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.w3.rdf.Description;

@GTAAScheme(Scheme.name)
@XmlType(name = "nameType",
    propOrder = {
        "name",
        "scopeNotes",
        "redirectedFrom"
    }
)
@XmlRootElement(name = "name")
@Schema(name = "GTAAName")
public class GTAAName extends AbstractSimpleValueGTAAConcept {


    @lombok.Builder(builderClassName = "Builder")
    public GTAAName(URI id, List<String> scopeNotes, String value, URI redirectedFrom, Status status, Instant lastModified) {
        super(id, scopeNotes, value, redirectedFrom, status, lastModified);
    }
    public GTAAName() {

    }

    public static GTAAName create(Description description) {
        final GTAAName answer = new GTAAName();
        fill(description, answer);
        return answer;
    }


}
