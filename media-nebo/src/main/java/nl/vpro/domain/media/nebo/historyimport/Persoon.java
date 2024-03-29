//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2012.08.17 at 05:17:31 PM CEST
//


package nl.vpro.domain.media.nebo.historyimport;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element ref="{}rol" maxOccurs="unbounded"/>
 *         &lt;element ref="{}naam"/>
 *         &lt;element ref="{}geslacht" minOccurs="0"/>
 *         &lt;element ref="{}nationaliteit" minOccurs="0"/>
 *         &lt;element ref="{}geboortejaar" minOccurs="0"/>
 *         &lt;element ref="{}overlijdingsjaar" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "rol",
    "naam",
    "geslacht",
    "nationaliteit",
    "geboortejaar",
    "overlijdingsjaar"
})
@XmlRootElement(name = "persoon")
public class Persoon {

    @XmlElement(required = true)
    protected List<Rol> rol;
    @XmlElement(required = true)
    protected String naam;
    protected String geslacht;
    protected String nationaliteit;
    protected String geboortejaar;
    protected String overlijdingsjaar;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger id;

    /**
     * Gets the value of the rol property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rol property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRol().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Rol }
     *
     *
     */
    public List<Rol> getRol() {
        if (rol == null) {
            rol = new ArrayList<>();
        }
        return this.rol;
    }

    /**
     * Gets the value of the naam property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNaam() {
        return naam;
    }

    /**
     * Sets the value of the naam property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNaam(String value) {
        this.naam = value;
    }

    /**
     * Gets the value of the geslacht property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGeslacht() {
        return geslacht;
    }

    /**
     * Sets the value of the geslacht property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGeslacht(String value) {
        this.geslacht = value;
    }

    /**
     * Gets the value of the nationaliteit property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNationaliteit() {
        return nationaliteit;
    }

    /**
     * Sets the value of the nationaliteit property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNationaliteit(String value) {
        this.nationaliteit = value;
    }

    /**
     * Gets the value of the geboortejaar property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGeboortejaar() {
        return geboortejaar;
    }

    /**
     * Sets the value of the geboortejaar property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGeboortejaar(String value) {
        this.geboortejaar = value;
    }

    /**
     * Gets the value of the overlijdingsjaar property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOverlijdingsjaar() {
        return overlijdingsjaar;
    }

    /**
     * Sets the value of the overlijdingsjaar property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOverlijdingsjaar(String value) {
        this.overlijdingsjaar = value;
    }

    /**
     * Gets the value of the id property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setId(BigInteger value) {
        this.id = value;
    }

}
