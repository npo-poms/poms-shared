package nl.vpro.domain;

import javax.xml.bind.annotation.XmlEnumValue;

/**
 * An enum can be made to extend this, which indicates that an extra method will be present {@link #getXmlValue()} which
 * will be the {@link XmlEnumValue} of the enum value.
 * <p>
 * Normally this would be {@link Enum#name()}}, but sometimes this is overriden, via the said annotation, and you need programmatic access to it.
 *
 * @author Michiel Meeuwissen
 * @since 5.5
 * @deprecated use {@link nl.vpro.util.XmlValued}
 */
@Deprecated
public interface XmlValued extends nl.vpro.util.XmlValued {

}
