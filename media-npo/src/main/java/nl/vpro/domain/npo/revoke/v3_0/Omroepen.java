//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2016.03.04 at 03:34:08 PM CET
//


package nl.vpro.domain.npo.revoke.v3_0;

import java.util.ArrayList;
import java.util.List;
import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="omroep" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "omroep"
})
@XmlRootElement(name = "omroepen")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class Omroepen {

    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected List<String> omroep;

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
     * {@link String }
     *
     *
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public List<String> getOmroep() {
        if (omroep == null) {
            omroep = new ArrayList<String>();
        }
        return this.omroep;
    }

}
