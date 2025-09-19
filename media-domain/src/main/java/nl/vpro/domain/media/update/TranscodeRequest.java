package nl.vpro.domain.media.update;

import lombok.*;

import java.util.Arrays;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.domain.media.*;
import nl.vpro.i18n.Displayable;

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
public class TranscodeRequest implements MediaIdentifiable {

    @NotNull
    @XmlAttribute
    private String mid;

    /**
     * File name. If starts with /, then it is prefixed by your ftp account name. Otherwise, the ftp account of POMS itself will be implicitly prefixed.
     * {@code null} would mean that the request was already issued, probabely via sourcing service route which does not offer the possibility to separate upload and request.
     */
    @Nullable
    private String fileName;


    @Nullable
    private Encryption encryption;

    @Nullable
    @lombok.Builder.Default
    private Priority priority = Priority.NORMAL;




    public TranscodeRequest() {

    }

    @Override
    public MediaType getMediaType() {
        return MediaType.MEDIA;
    }

    @Override
    public List<String> getCrids() {
        return Arrays.asList();

    }

    public static class Builder {

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
