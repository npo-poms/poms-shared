package nl.vpro.domain.media.nebo.sync;

import java.util.Date;

import jakarta.xml.bind.annotation.*;

/**
 * @author Danny
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(
    name = "Delete",
    propOrder = {
        "record",
        "prid",
        "srid"
    })
@XmlRootElement(name = "delete")
public class DeleteRequest {

    @XmlAttribute
    protected String type;

    @XmlAttribute
    protected Date timestamp;

    @XmlElement(required = true, nillable = false)
    protected Long record;

    @XmlElement(required = true, nillable = false)
    protected String prid;

    @XmlElement(required = false)
    protected String srid;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSrid() {
        return srid;
    }

    public void setSrid(String srid) {
        this.srid = srid;
    }

    public String getPrid() {
        return prid;
    }

    public void setPrid(String prid) {
        this.prid = prid;
    }

    public Long getRecord() {
        return record;
    }

    public void setRecord(Long record) {
        this.record = record;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
