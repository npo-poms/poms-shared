/**
 * Contains the translation objects for {@link nl.vpro.domain.media.MediaObject}
 * <p>
 * {@code MediaObject}s are (partially) internationizable. Fields like titles and descriptions are basically in dutch, but translations can be provided for other languages.
 */
@javax.xml.bind.annotation.XmlSchema(namespace = Xmlns.MEDIA_I18N_NAMESPACE,
    xmlns=
        {
            @XmlNs(prefix = "mediai18n",       namespaceURI = Xmlns.MEDIA_I18N_NAMESPACE)

        },
    elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED,
    attributeFormDefault = javax.xml.bind.annotation.XmlNsForm.UNQUALIFIED
)
package nl.vpro.domain.i18n;

import javax.xml.bind.annotation.XmlNs;

import nl.vpro.domain.Xmlns;
