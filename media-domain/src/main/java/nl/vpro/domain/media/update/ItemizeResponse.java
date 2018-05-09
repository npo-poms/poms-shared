package nl.vpro.domain.media.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Singular;

import java.net.URI;
import java.util.List;

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
    @XmlElements(value = {
        @XmlElement(name="request",
            type=ItemizeRequest.class),
        @XmlElement(name="liverequest",
            type=LiveItemizeRequest.class)
    })
    private Object request;

    @XmlAttribute
    private boolean success;

    @Singular
    @XmlElement(name = "result")
    private List<URI> results;

    private Integer id;


    public ItemizeResponse() {

    }

}
