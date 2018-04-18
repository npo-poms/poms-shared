package nl.vpro.domain.media.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.Displayable;
import nl.vpro.domain.media.Encryption;

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
    @XmlAttribute
    private String mid;

    @NotNull
    private String fileName;

    @NotNull
    private Encryption encryption;

    @NotNull
    private Priority priority;


    public TranscodeRequest() {

    }

    public static class Builder {
        @lombok.Builder.Default
        Priority priority = Priority.NORMAL;
    }

    @XmlType(name = "priorityType")
    public enum Priority implements Displayable {
        LOW("Laag"),
        NORMAL("Normaal"),
        HIGH("Hoog"),
        URGENT("Urgent")
        ;

        @Getter
        private final String displayName;

        Priority(String displayName) {
            this.displayName = displayName;
        }
    }
}
