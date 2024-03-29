package nl.vpro.domain.npoplayer;

import lombok.*;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonTypeName;

@XmlRootElement
@Data
@lombok.Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
@XmlType(propOrder = {
    "iat",
    "sub",
    "iss"
})
@JsonTypeName("response")
@Deprecated
public class Token {
    Long iat;
    String sub;
    String iss;
}

