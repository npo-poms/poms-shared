package nl.vpro.domain.gtaa;

import java.time.Instant;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.openarchives.oai.Label;
import nl.vpro.w3.rdf.Description;

@GTAAScheme(Scheme.name)
@XmlType(name = "name",
    propOrder = {
        "value",
        "notes",
        "redirectedFrom"
    }
)
@XmlRootElement(name = "name")
public class GTAAName extends AbstractSimpleValueThesaurusItem {


    @lombok.Builder(builderClassName = "Builder")
    public GTAAName(String id, List<Label> notes, String value, String redirectedFrom, Status status, Instant lastModified) {
        super(id, notes, value, redirectedFrom, status, lastModified);
    }
    public GTAAName() {

    }

    public static GTAAName create(Description description) {
        final GTAAName answer = new GTAAName();
        fill(description, answer);
        return answer;
    }


}
