package nl.vpro.transfer.extjs.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Michiel Meeuwissen
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "count"
        })
public class BroadcasterView extends OrganizationView {

    private long count;
    private String whatsOnId;
    private String neboId;
    private String misId;


    BroadcasterView(String id, String displayName, boolean active, Long count, String whatsOnId, String neboId, String misId) {
        super(id, displayName, active);
        this.count = count;
        this.whatsOnId = whatsOnId;
        this.neboId = neboId;
        this.misId = misId;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getWhatsOnId() {
        return whatsOnId;
    }

    public void setWhatsOnId(String whatsOnId) {
        this.whatsOnId = whatsOnId;
    }

    public String getNeboId() {
        return neboId;
    }

    public void setNeboId(String neboId) {
        this.neboId = neboId;
    }

    public String getMisId() {
        return misId;
    }

    public void setMisId(String misId) {
        this.misId = misId;
    }
}
