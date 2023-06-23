/*
 * Copyright (C) 2022 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.npoplayer;

import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.*;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author r.jansen
 */
@XmlRootElement
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlType(propOrder = {
    "mid"
})
@JsonTypeName("tokenRequest")
public class TokenRequest {
    String mid;
}
