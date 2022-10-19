package nl.vpro.domain.npoplayer;

import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.*;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@Data
@lombok.Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
@XmlType(propOrder = {
    "iat",
    "sub",
    "iss",
    "cip",
    "age"
})
@JsonTypeName("response")
public class Token {
    Long iat;
    String sub;
    String iss;
    String cip;
    Integer age;
}

