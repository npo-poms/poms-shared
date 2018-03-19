package nl.vpro.domain.media.update;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URI;

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
@XmlType(name = "itemizeResponseType")
@XmlRootElement(name = "itemizeResponse")
public class ItemizeResponse {


    @NotNull
    private ItemizeRequest request;

    @XmlAttribute
    private boolean success;

    private URI result;

    public ItemizeResponse() {

    }

}
