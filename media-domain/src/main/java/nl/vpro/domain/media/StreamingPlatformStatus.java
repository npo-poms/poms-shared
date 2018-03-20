package nl.vpro.domain.media;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement (name="streamingstatus")
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class StreamingPlatformStatus {

    @Getter
    @Setter
    @XmlAttribute
    private String status;

    public StreamingPlatformStatus(String status){
        this.status = status;
    }

}
