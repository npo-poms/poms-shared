package nl.vpro.domain.media;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.media.support.StreamingStatus;


/**
 * Thin wrapper around a {@link StreamingStatus} to be able to represent that enum as more or less decent stand alone XML.
 */
@XmlRootElement (name="streamingStatus")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "streamingStatusReport")
@NoArgsConstructor
public class StreamingStatusReport {

    @Getter
    @Setter
    @XmlAttribute
    private StreamingStatus status;


    public StreamingStatusReport(StreamingStatus status){
        this.status = status;
    }

}
