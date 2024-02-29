package nl.vpro.domain.gtaa;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.*;
import java.io.Serial;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import nl.vpro.w3.rdf.Description;
import org.meeuw.i18n.regions.Region;

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
@XmlAccessorType(XmlAccessType.NONE)
@Schema(name = "GTAAGeographicName")
public final class GTAAGeographicName extends AbstractSimpleValueGTAAConcept {

    @Serial
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
    
    
    /**
     * @since 8.0
     */
    public Code toCode() {
        return new Code();
    }


    /**
     * @since 8.0
     */

    public class Code implements Region {

        @Override
        public String getCode() {
            return GTAAGeographicName.this.getId().toString();
        }

        @Override
        public Type getType() {
            return Type.UNDEFINED;
        }

        @Override
        public String getName() {
            return GTAAGeographicName.this.getName();
        }
    }

}
