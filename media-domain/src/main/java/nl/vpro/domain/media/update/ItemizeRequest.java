package nl.vpro.domain.media.update;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;

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
@XmlType(name = "itemizeType")
@XmlRootElement(name = "itemize")
public class ItemizeRequest {


    @NotNull
    private String mid;
    private Duration start;
    private Duration stop;

    public ItemizeRequest() {

    }

}
