package nl.vpro.domain.media;

import lombok.AllArgsConstructor;

import java.util.*;

import javax.xml.bind.annotation.*;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;


@XmlRootElement(name = "mediaInformation")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mediaTableType",
         propOrder = {"programTable",
                      "groupTable",
                      "locationTable",
                      "schedule"})
@lombok.Builder
@AllArgsConstructor
public class MediaTable implements Iterable<MediaObject> {

    public MediaTable() {

    }

    @XmlElementWrapper(name = "programTable")
    @XmlElement(name = "program")
    protected List<Program> programTable;

    @XmlElementWrapper(name = "groupTable")
    @XmlElement(name = "group")
    protected List<Group> groupTable;

    @XmlElement
    protected LocationTable locationTable;

    @XmlElement
    protected Schedule schedule;

    @XmlAttribute
    protected Date publicationTime;

    @XmlAttribute
    protected String publisher;

    @XmlAttribute
    protected Short version;

    @XmlAttribute
    protected String source;

    public MediaTable addProgram(Program program) {
        if(programTable == null) {
            programTable = new ArrayList<>();
        }

        programTable.add(program);
        return this;
    }

    public <T extends MediaObject> T find(String mid) {
        for (MediaObject p : Iterables.concat(programTable, groupTable)) {
            if (Objects.equals(p.getMid(), mid)) {
                return (T) p;
            }
        }
        return null;
    }

    public List<Program> getProgramTable() {
        if(programTable == null) {
            programTable = new ArrayList<>();
        }
        return programTable;
    }

    public void setProgramTable(List<Program> programTable) {
        this.programTable = programTable;
    }

    public MediaTable addGroup(Group group) {
        if(groupTable == null) {
            groupTable = new ArrayList<>();
        }

        groupTable.add(group);
        return this;
    }

    public List<Group> getGroupTable() {
        if(groupTable == null) {
            groupTable = new ArrayList<>();
        }
        return this.groupTable;
    }

    public void setGroupTable(List<Group> groupTable) {
        this.groupTable = groupTable;
    }

    public LocationTable getLocationTable() {
        return locationTable;
    }

    public void setLocationTable(LocationTable value) {
        this.locationTable = value;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule value) {
        this.schedule = value;
    }

    public Date getPublicationTime() {
        return publicationTime;
    }

    public void setPublicationTime(Date value) {
        this.publicationTime = value;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String value) {
        this.publisher = value;
    }

    public Short getVersion() {
        return version;
    }

    public void setVersion(Short value) {
        this.version = value;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "MediaTable " + getGroupTable().size() + " groups " + getProgramTable().size() + " program " + getSchedule();
    }

    @Override
    public Iterator<MediaObject> iterator() {
        return Iterators.concat(getProgramTable().listIterator(), getGroupTable().listIterator());
    }
}
