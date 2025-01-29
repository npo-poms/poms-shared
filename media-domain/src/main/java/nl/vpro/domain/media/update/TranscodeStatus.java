package nl.vpro.domain.media.update;

import lombok.*;

import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Data
@AllArgsConstructor
@lombok.Builder(builderClassName = "Builder")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transcodeStatusType")
@XmlRootElement(name = "transcodeStatus")
public class TranscodeStatus {

    String fileName;
    Status status;
    String statusMessage;
    String workflowType;

    @NotNull
    @XmlAttribute
    String mid;

    @XmlAttribute
    Boolean missingMedia;

    @NotNull
    String workflowId;

    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    Instant startTime;

    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    Instant updateTime;

    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    Instant endTime;


    @XmlElementWrapper
    @XmlElement(name = "broadcaster")
    List<String> broadcasters;


    @XmlElementWrapper
    @XmlElement(name = "crids")
    List<String> crids;



    public TranscodeStatus() {
    }

    @XmlType(name = "transcodeStatusEnum")
    @Getter
    public enum Status  {
        RUNNING("De workflow is gestart en in verwerking", false),
        COMPLETED("De workflow is succesvol afgerond en de streams zijn afgemeld bij POMS", true),
        FAILED("De workflow kon niet worden voltooid", true),
        TIMED_OUT("De workflow was niet binnen een redelijke tijd voltooid en is afgebroken", true),
        TERMINATED("De workflow is handmatig afgebroken door een NEP medewerker", true),
        PAUSED("De workflow is gepauzeerd door een NEP medewerker", false);
        private final String description;
        private final boolean endStatus;

        Status(String description, boolean endStatus) {
            this.description = description;
            this.endStatus = endStatus;
        }
    }
}
