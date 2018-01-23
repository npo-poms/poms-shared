package nl.vpro.nep;

import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * @author Michiel Meeuwissen
 * @since 5.6

 */
@Data
public class IngestResponse {

    String workflowId;
    Status status;
    Instant startTime;
    Instant updateTime;
    Instant endTime;
    List<Link> _links;
    List<String> errors;

    public enum Status {
        RUNNING("De ​workflow ​is ​gestart ​en ​in ​verwerking", false),
        COMPLETED("De ​workflow ​is ​succesvol ​afgerond ​en ​de ​streams ​zijn ​afgemeld bij ​POMS", true),
        FAILED("De ​workflow ​kon ​niet ​worden ​voltooid", true),
        TIMED_OUT("De ​workflow ​was ​niet ​binnen ​een ​redelijke ​tijd ​voltooid ​en ​is afgebroken", true),
        TERMINATED("De ​workflow ​is ​handmatig ​afgebroken ​door ​een ​NEP ​medewerker", true),
        PAUSED("De ​workflow ​is ​gepauzeerd ​door ​een ​NEP ​medewerker", false);
        private final String description;
        private final boolean endStatus;

        Status(String description, boolean endStatus) {
            this.description = description;
            this.endStatus = endStatus;
        }
    }
}
