package nl.vpro.domain.media.update;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
public class TranscodeStatus {


    @NotNull
    private String mid;

    String status;
    String statusMessage;
    String workflowType;
    Instant startTime;
    Instant updateTime;
    Instant endTime;
    String fileName;


    public TranscodeStatus() {

    }
}
