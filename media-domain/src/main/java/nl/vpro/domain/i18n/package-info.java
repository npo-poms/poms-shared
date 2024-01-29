/**
 * Contains the translation objects for {@link nl.vpro.domain.media.MediaObject}
 * <p>
 * {@code MediaObject}s are (partially) internationizable. Fields like titles and descriptions are basically in Dutch, but translations can be provided for other languages.
 */
@jakarta.xml.bind.annotation.XmlSchema(namespace = Xmlns.MEDIA_I18N_NAMESPACE,
    xmlns=
        {
            @XmlNs(prefix = "mediai18n",       namespaceURI = Xmlns.MEDIA_I18N_NAMESPACE)

        },
    elementFormDefault = jakarta.xml.bind.annotation.XmlNsForm.QUALIFIED,
    attributeFormDefault = jakarta.xml.bind.annotation.XmlNsForm.UNQUALIFIED
)
package nl.vpro.domain.i18n;

import jakarta.xml.bind.annotation.XmlNs;

import nl.vpro.domain.Xmlns;
