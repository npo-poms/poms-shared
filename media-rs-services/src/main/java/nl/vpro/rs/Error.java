package nl.vpro.rs;

import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@XmlType(name = "error",
    propOrder = {
        "message",
        "clazz",
        "stackTraceElement"
    }
)
@JsonPropertyOrder({"message",
    "class",
    "stackTraceElements"
    })
public class Error {

    String message;

    @XmlElement(name = "class")
    String clazz;

    @JsonProperty("stackTraceElements")
    List<String> stackTraceElement;

    public Error() {

    }

    public Error(Throwable  e) {
        this.message = e.getMessage();
        this.stackTraceElement = Arrays.stream(e.getStackTrace())
            .map(StackTraceElement::toString)
            .collect(Collectors.toList());
        this.clazz = e.getClass().getName();
    }

    @Override
    public String toString() {
        return message + "\n" + clazz + "\n" + String.join("\n", stackTraceElement);
    }
}
