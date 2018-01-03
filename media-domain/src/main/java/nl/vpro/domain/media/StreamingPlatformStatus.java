package nl.vpro.domain.media;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@XmlRootElement (name="streamingstatus")
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class StreamingPlatformStatus {

    @Getter
    @Setter
    @XmlAttribute
    private String status;

    public StreamingPlatformStatus (String status){this.status = status;}

}
