package nl.vpro.domain.media.update;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Data
@AllArgsConstructor
@lombok.Builder(builderClassName = "Builder")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transcodeType")
@XmlRootElement(name = "transcode")
public class TranscodeRequest {


    @NotNull
    private String mid;
    @NotNull
    private String fileName;
    @NotNull
    private Encryption encryption;
    @NotNull
    private Priority priority;


    public TranscodeRequest() {

    }

    @XmlType(name = "encryptionType")
    public enum Encryption {
        DRM, NONE;

    }
    @XmlType(name = "priorityType")
    public enum Priority {
        LOW, NORMAL, HIGH, URGENT;

    }
}
