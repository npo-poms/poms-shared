package nl.vpro.rs;

import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Error {

    String message;

    List<String> stackTraceElement;

    public Error() {

    }

    public Error(Throwable  e) {
        this.message = e.getClass().getName() + ":" + e.getMessage();
        this.stackTraceElement = Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return message + "\n" + String.join("\n", stackTraceElement);
    }
}
