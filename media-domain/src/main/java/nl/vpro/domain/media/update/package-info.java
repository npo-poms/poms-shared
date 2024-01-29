/**
 * The update objects are data transfer objects, and represent versions of the objects in {@link nl.vpro.domain.media} that are simplified, and contain in principal no redundant information, or data that is not updatable or settable at all. E.g. in update objects all field that are {@link nl.vpro.domain.media.support.Ownable} in {@link nl.vpro.domain.media.MediaObject} are not ownable in {@link nl.vpro.domain.media.update.MediaUpdate}
 */
@XmlSchema(
    namespace = Xmlns.UPDATE_NAMESPACE,
    xmlns =
        {
            @XmlNs(prefix = "",      namespaceURI = Xmlns.UPDATE_NAMESPACE),
            @XmlNs(prefix = "media", namespaceURI = Xmlns.MEDIA_NAMESPACE),
            @XmlNs(prefix = "shared",namespaceURI = Xmlns.SHARED_NAMESPACE)
        },
    elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED

)
package nl.vpro.domain.media.update;

import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlSchema;

import nl.vpro.domain.Xmlns;

