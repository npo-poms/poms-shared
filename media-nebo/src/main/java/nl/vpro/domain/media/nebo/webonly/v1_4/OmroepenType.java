//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2012.06.07 at 01:34:11 PM CEST
//


package nl.vpro.domain.media.nebo.webonly.v1_4;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.user.Broadcaster;


/**
 * <p>Java class for omroepenType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="omroepenType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="omroep" type="{}omroepType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "omroepenType", propOrder = {
        "omroep"
        })
public class OmroepenType {

    @XmlElement(required = true)
    protected List<OmroepType> omroep;

    public OmroepenType(List<Broadcaster> broadcasters) {
        if (broadcasters != null && broadcasters.size() > 0) {
            omroep = new ArrayList<>();
            for (Broadcaster broadcaster : broadcasters) {
                omroep.add(new OmroepType(broadcaster, omroep.size() == 0));
            }
        }
    }

    public OmroepenType() {



    }

    /**
     * Gets the value of the omroep property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the omroep property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOmroep().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OmroepType }
     *
     *
     */
    public List<OmroepType> getOmroep() {
        if (omroep == null) {
            omroep = new ArrayList<>();
        }
        return this.omroep;
    }

}
